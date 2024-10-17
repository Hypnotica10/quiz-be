package com.quiz.project.service;

import java.beans.FeatureDescriptor;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.data.auditing.MappingAuditableBeanWrapperFactory;
import org.springframework.stereotype.Service;

import com.quiz.project.config.ModelMapperConfig;
import com.quiz.project.dto.req.MajorReqDTO;
import com.quiz.project.dto.req.SubjectReqDTO;
import com.quiz.project.dto.resp.MajorRespDTO;
import com.quiz.project.dto.resp.SubjectRespDTO;
import com.quiz.project.entity.MajorEntity;
import com.quiz.project.entity.SubjectEntity;
import com.quiz.project.repository.MajorRepository;
import com.quiz.project.repository.SubjectRepository;
import com.quiz.project.util.ConvertModel;
import com.quiz.project.util.error.InvalidException;

@Service
public class MajorService {

	private MajorRepository majorRepository;
	private SubjectRepository subjectRepository;

	public MajorService(MajorRepository majorRepository, SubjectRepository subjectRepository) {
		this.majorRepository = majorRepository;
		this.subjectRepository = subjectRepository;
	}

	private boolean checkExistsById(Long id) {
		return this.majorRepository.existsById(id);
	}

	private boolean checkExistsByName(String name) {
		return this.majorRepository.existsByName(name);
	}

	public void createMajor(MajorReqDTO newMajor) throws InvalidException {
		boolean isSubjectExists = this.subjectRepository.existsById(newMajor.getSubjectId());
		if (!isSubjectExists) {
			throw new InvalidException("subject does not exist");
		}
		String majorName = newMajor.getName().toLowerCase();
		if (checkExistsByName(majorName)) {
			throw new InvalidException("major is existed");
		}
		newMajor.setName(majorName);
		SubjectEntity subjectEntity = this.subjectRepository.findById(newMajor.getSubjectId()).get();
		MajorEntity majorEntity = ModelMapperConfig.map(newMajor, MajorEntity.class);
		majorEntity.setSubject(subjectEntity);
		this.majorRepository.save(majorEntity);
	}

	public List<MajorRespDTO> getListMajor() {
		List<MajorEntity> listMajorEntities = this.majorRepository.findAll();
		List<MajorRespDTO> listMajorRespDtos = ModelMapperConfig.mapAll(listMajorEntities, MajorRespDTO.class);
		return listMajorRespDtos;
	}

	public MajorRespDTO updateMajor(Long id, MajorReqDTO majorReqDTO) throws InvalidException {
		if (!checkExistsById(id)) {
			throw new InvalidException("Id invalid");
		}
		if (majorReqDTO.getSubjectId() != null) {
			boolean isSubjectExists = this.subjectRepository.existsById(majorReqDTO.getSubjectId());
			if (!isSubjectExists) {
				throw new InvalidException("subject does not exist");
			}
		}
		if (majorReqDTO.getName() != null) {
			String majorName = majorReqDTO.getName().toLowerCase();
			if (checkExistsByName(majorName)) {
				throw new InvalidException("major is existed");
			}
		}

//		MajorEntity majorEntity = this.majorRepository.findById(id).orElse(null);
//		String[] ignoreProps = getNullProperties(majorReqDTO);
//		BeanUtils.copyProperties(majorReqDTO, majorEntity, ignoreProps);
		MajorEntity majorEntity = this.majorRepository.findById(id).get();
		SubjectEntity subjectEntity = this.subjectRepository.findById(
				majorReqDTO.getSubjectId() != null ? majorReqDTO.getSubjectId() : majorEntity.getSubject().getId())
				.get();
		ConvertModel.getModelMapping(majorReqDTO, majorEntity);
		majorEntity.setId(id);
		majorEntity.setSubject(subjectEntity);
		MajorEntity savedEntity = this.majorRepository.save(majorEntity);

		MajorRespDTO majorRespDto = new MajorRespDTO();
		ConvertModel.getModelMapping(savedEntity, majorRespDto);
		return majorRespDto;
	}

	public void deleteMajor(Long id) throws InvalidException {
		if (!checkExistsById(id)) {
			throw new InvalidException("Id invalid");
		}
		this.majorRepository.deleteById(id);
	}

	public List<MajorRespDTO> getAllMajorBySubjectId(Long id) {
		List<MajorEntity> majorEntities = this.majorRepository.findAllBySubjectId(id);
		List<MajorRespDTO> majorRespDTOs = ModelMapperConfig.mapAll(majorEntities, MajorRespDTO.class);
		return majorRespDTOs;
	}
}
