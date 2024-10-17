package com.quiz.project.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.quiz.project.config.ModelMapperConfig;
import com.quiz.project.dto.Response;
import com.quiz.project.dto.req.UserReqDTO;
import com.quiz.project.dto.resp.AuthRespDTO;
import com.quiz.project.dto.resp.CourseRespDTO;
import com.quiz.project.dto.resp.UserRespDTO;
import com.quiz.project.entity.UserEntity;
import com.quiz.project.repository.CourseRepository;
import com.quiz.project.repository.UserRepository;
import com.quiz.project.util.ConvertModel;
import com.quiz.project.util.error.InvalidException;

@Service
public class UserService {
	private UserRepository userRepository;
	private CourseRepository courseRepository;
	private PasswordEncoder passwordEncoder;

	public UserService(UserRepository userRepository, CourseRepository courseRepository,
			PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.courseRepository = courseRepository;
		this.passwordEncoder = passwordEncoder;
	}

	public UserEntity getUserByUsername(String username) {
		return this.userRepository.findByUsername(username);
	}

	public UserRespDTO createNewUser(UserReqDTO newUser) throws InvalidException {
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
		boolean isExist = this.userRepository.existsByUsername(newUser.getUsername());
		if (isExist) {
			throw new InvalidException("Username is existed");
		}
		UserEntity userEntity = ModelMapperConfig.map(newUser, UserEntity.class);
		userEntity.setPassword(passwordEncoder.encode(newUser.getPassword()));
		UserRespDTO userRespDTO = ModelMapperConfig.map(this.userRepository.save(userEntity), UserRespDTO.class);
		System.out.println(userEntity.getId());
		return userRespDTO;
	}

	public UserRespDTO getUserById(Long id) throws InvalidException {
		boolean isExist = this.userRepository.existsById(id);
		if (!isExist) {
			throw new InvalidException("Id invalid");
		}
		UserRespDTO userRespDTO = ModelMapperConfig.map(this.userRepository.findById(id), UserRespDTO.class);
		return userRespDTO;
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
		UserEntity updateUser = new UserEntity();
		ConvertModel.getModelMapping(userReqDTO, updateUser);
		updateUser.setPassword(passwordEncoder.encode(userReqDTO.getPassword()));
		
		updateUser.setCreatedDate(userEntityInDb.getCreatedDate());

		UserEntity saveUser = this.userRepository.save(updateUser);
		System.out.println(saveUser);
		UserRespDTO userRespDTO = new UserRespDTO();
		ConvertModel.getModelMapping(saveUser, userRespDTO);

		return userRespDTO;
	}
}
