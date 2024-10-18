package com.quiz.project.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.quiz.project.dto.req.MajorReqDTO;
import com.quiz.project.dto.resp.MajorRespDTO;
import com.quiz.project.entity.MajorEntity;
import com.quiz.project.repository.MajorRepository;
import com.quiz.project.util.ConvertModel;
import com.quiz.project.util.error.InvalidException;

@Service
public class MajorService {

	private MajorRepository majorRepository;
	private SubjectService subjectService;

	public MajorService(MajorRepository majorRepository, SubjectService subjectService) {
		this.majorRepository = majorRepository;
		this.subjectService = subjectService;
	}

	private boolean checkExistsByName(String name) {
		return this.majorRepository.existsByName(name);
	}

	public MajorEntity reqDtoToEntity(MajorReqDTO majorReqDto) {
		MajorEntity majorEntity = new MajorEntity();

		ConvertModel.getModelMapping(majorReqDto, majorEntity);

		majorEntity.setSubject(this.subjectService.getSubjectById(majorReqDto.getSubjectId()));

		return majorEntity;
	}

	public MajorRespDTO entityToRespDto(MajorEntity majorEntity) {
		MajorRespDTO majorRespDto = new MajorRespDTO();

		ConvertModel.getModelMapping(majorEntity, majorRespDto);

		return majorRespDto;
	}

	public MajorEntity getMajorEntityById(Long id) {
		return this.majorRepository.findById(id).get();
	}
	
	public boolean checkExistsById(Long id) {
		return this.majorRepository.existsById(id);
	}

	public void createMajor(MajorReqDTO newMajor) throws InvalidException {
		boolean isSubjectExists = this.subjectService.checkExistsById(newMajor.getSubjectId());
		if (!isSubjectExists) {
			throw new InvalidException("subject does not exist");
		}
		String majorName = newMajor.getName().toLowerCase();
		if (checkExistsByName(majorName)) {
			throw new InvalidException("major is existed");
		}
		newMajor.setName(majorName);

		this.majorRepository.save(reqDtoToEntity(newMajor));
	}

	public List<MajorRespDTO> getListMajor() {
		List<MajorEntity> listMajorEntities = this.majorRepository.findAll();
		List<MajorRespDTO> listMajorRespDtos = new ArrayList<MajorRespDTO>();
		for (MajorEntity majorEntity : listMajorEntities) {
			listMajorRespDtos.add(entityToRespDto(majorEntity));
		}
		return listMajorRespDtos;
	}

	public List<MajorRespDTO> getAllMajorBySubjectId(Long id) {
		List<MajorEntity> listMajorEntities = this.majorRepository.findAllBySubjectId(id);
		List<MajorRespDTO> listMajorRespDtos = new ArrayList<MajorRespDTO>();
		for (MajorEntity majorEntity : listMajorEntities) {
			listMajorRespDtos.add(entityToRespDto(majorEntity));
		}
		return listMajorRespDtos;
	}
}
