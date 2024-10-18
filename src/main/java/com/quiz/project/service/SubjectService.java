package com.quiz.project.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.quiz.project.dto.req.SubjectReqDTO;
import com.quiz.project.dto.resp.MajorRespDTO;
import com.quiz.project.dto.resp.SubjectRespDTO;
import com.quiz.project.entity.SubjectEntity;
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

	public SubjectEntity reqDtoToEntity(SubjectReqDTO subjectReqDto) {
		SubjectEntity subjectEntity = new SubjectEntity();

		ConvertModel.getModelMapping(subjectReqDto, subjectEntity);

		return subjectEntity;
	}

	public SubjectRespDTO entityToRespDto(SubjectEntity subjectEntity) {
		SubjectRespDTO subjectRespDto = new SubjectRespDTO();

		ConvertModel.getModelMapping(subjectEntity, subjectRespDto);

		if (subjectEntity.getListMajor().size() > 0) {
			List<MajorRespDTO> majorRespDtos = this.majorService.getAllMajorBySubjectId(subjectEntity.getId());
			subjectRespDto.setMajor(majorRespDtos);
		}

		return subjectRespDto;
	}

	public SubjectRespDTO createSubject(SubjectReqDTO newSubject) throws InvalidException {
		String subjectName = newSubject.getName().toLowerCase();
		boolean isExist = this.subjectRepository.existsByName(subjectName);
		if (isExist) {
			throw new InvalidException("subject is existed");
		}
		newSubject.setName(subjectName);

		SubjectEntity saveSubject = reqDtoToEntity(newSubject);

		return entityToRespDto(saveSubject);
	}

	public List<SubjectRespDTO> getListSubject() {
		List<SubjectEntity> listSubjectEntities = this.subjectRepository.findAll();
		List<SubjectRespDTO> listSubjectRespDtos = new ArrayList<SubjectRespDTO>();
		for (SubjectEntity subjectEntity : listSubjectEntities) {
			listSubjectRespDtos.add(entityToRespDto(subjectEntity));
		}
		return listSubjectRespDtos;
	}

	public boolean checkExistsById(Long id) {
		return this.subjectRepository.existsById(id);
	}

	public SubjectEntity getSubjectById(Long id) {
		return this.subjectRepository.findById(id).get();
	}
}
