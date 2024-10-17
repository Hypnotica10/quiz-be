package com.quiz.project.service;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.quiz.project.config.ModelMapperConfig;
import com.quiz.project.dto.req.SubjectReqDTO;
import com.quiz.project.dto.resp.SubjectRespDTO;
import com.quiz.project.entity.SubjectEntity;
import com.quiz.project.repository.MajorRepository;
import com.quiz.project.repository.SubjectRepository;
import com.quiz.project.util.ConvertModel;
import com.quiz.project.util.error.InvalidException;

@Service
public class SubjectService {

	private SubjectRepository subjectRepository;
	private MajorService majorService;

	public SubjectService(SubjectRepository subjectRepository, MajorService majorService) {
		this.subjectRepository = subjectRepository;
		this.majorService = majorService;
	}

	private boolean checkExistsById(Long id) {
		return this.subjectRepository.existsById(id);
	}

	private boolean checkExistsByName(String name) {
		return this.subjectRepository.existsByName(name);
	}

	public SubjectRespDTO createSubject(SubjectReqDTO newSubject) throws InvalidException {
		String subjectName = newSubject.getName().toLowerCase();
		boolean isExist = this.subjectRepository.existsByName(subjectName);
		if (isExist) {
			throw new InvalidException("subject is existed");
		}
		newSubject.setName(subjectName);
		SubjectEntity newSubjectEntity = ModelMapperConfig.map(newSubject, SubjectEntity.class);
		SubjectRespDTO subjectRespDto = ModelMapperConfig.map(this.subjectRepository.save(newSubjectEntity),
				SubjectRespDTO.class);
		return subjectRespDto;
	}

	public List<SubjectRespDTO> getListSubject() {
		List<SubjectEntity> listSubjectEntities = this.subjectRepository.findAll();
		List<SubjectRespDTO> listSubjectRespDtos = new ArrayList<SubjectRespDTO>();
		for (SubjectEntity subjectEntity : listSubjectEntities) {
			SubjectRespDTO subjectRespDTO = ModelMapperConfig.map(subjectEntity, SubjectRespDTO.class);
			subjectRespDTO.setMajor(this.majorService.getAllMajorBySubjectId(subjectEntity.getId()));
			listSubjectRespDtos.add(subjectRespDTO);
		}
		return listSubjectRespDtos;
	}

	public SubjectRespDTO updateSubject(Long id, SubjectReqDTO subjectReqDTO) throws InvalidException {
		if (!checkExistsById(id)) {
			throw new InvalidException("Id invalid");
		}
		if (subjectReqDTO.getName() != null) {
			String subjectName = subjectReqDTO.getName().toLowerCase();
			if (checkExistsByName(subjectName)) {
				throw new InvalidException("subject is existed");
			}
		}
		SubjectEntity subjectEntity = this.subjectRepository.findById(id).get();
		ConvertModel.getModelMapping(subjectReqDTO, subjectEntity);
		SubjectEntity savedEntity = this.subjectRepository.save(subjectEntity);

		SubjectRespDTO subjectRespDto = new SubjectRespDTO();
		ConvertModel.getModelMapping(savedEntity, subjectRespDto);
//		SubjectEntity subjectEntity = ModelMapperConfig.map(subjectReqDTO, SubjectEntity.class);
//		SubjectRespDTO subjectRespDto = ModelMapperConfig.map(this.subjectRepository.save(subjectEntity),
//				SubjectRespDTO.class);
		return subjectRespDto;
	}

	public void deleteSubject(Long id) throws InvalidException {
		boolean isExist = this.subjectRepository.existsById(id);
		if (!isExist) {
			throw new InvalidException("Id invalid");
		}
		this.subjectRepository.deleteById(id);
	}
}
