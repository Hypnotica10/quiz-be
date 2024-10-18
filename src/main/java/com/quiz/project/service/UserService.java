package com.quiz.project.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.quiz.project.dto.req.UserReqDTO;
import com.quiz.project.dto.resp.UserRespDTO;
import com.quiz.project.entity.UserEntity;
import com.quiz.project.repository.UserRepository;
import com.quiz.project.util.ConvertModel;
import com.quiz.project.util.error.InvalidException;

@Service
public class UserService {
	private UserRepository userRepository;
	private PasswordEncoder passwordEncoder;

	public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	public UserEntity getUserByUsername(String username) {
		return this.userRepository.findByUsername(username);
	}

	public UserEntity getUserEntityById(Long id) {
		return this.userRepository.findById(id).get();
	}

	public boolean checkExistsById(Long id) {
		return this.userRepository.existsById(id);
	}

	public UserEntity reqDtoToEntity(UserReqDTO userReqDto) {
		UserEntity userEntity = new UserEntity();

		ConvertModel.getModelMapping(userReqDto, userEntity);
		userEntity.setPassword(passwordEncoder.encode(userReqDto.getPassword()));

		return userEntity;
	}

	public UserRespDTO entityToRespDto(UserEntity userEntity) {
		UserRespDTO userRespDto = new UserRespDTO();

		ConvertModel.getModelMapping(userEntity, userRespDto);

		return userRespDto;
	}

	public UserRespDTO createNewUser(UserReqDTO newUser) throws InvalidException {
		boolean isExist = this.userRepository.existsByUsername(newUser.getUsername());
		if (isExist) {
			throw new InvalidException("Username is existed");
		}

		UserEntity saveUser = this.userRepository.save(reqDtoToEntity(newUser));

		return entityToRespDto(saveUser);
	}

	public UserRespDTO getUserById(Long id) throws InvalidException {
		boolean isExist = this.userRepository.existsById(id);
		if (!isExist) {
			throw new InvalidException("Id invalid");
		}

		UserEntity userEntity = this.userRepository.findById(id).get();

		return entityToRespDto(userEntity);
	}

	public boolean checkPassword(String password, String username) throws InvalidException {
		UserEntity userEntity = getUserByUsername(username);
		if (userEntity == null) {
			throw new InvalidException("Token invalid");
		}
		boolean isMatches = passwordEncoder.matches(password, userEntity.getPassword());
		return isMatches;
	}

	public UserRespDTO updateUser(UserReqDTO userReqDTO, String username) throws InvalidException {
		boolean isMatchesPassword = checkPassword(userReqDTO.getPassword(), username);
		if (!isMatchesPassword) {
			throw new InvalidException("Wrong password");
		}

		UserEntity userEntityInDb = getUserByUsername(username);
		UserEntity updateUser = reqDtoToEntity(userReqDTO);

		updateUser.setCreatedDate(userEntityInDb.getCreatedDate());

		UserEntity saveUser = this.userRepository.save(updateUser);

		return entityToRespDto(saveUser);
	}
}
