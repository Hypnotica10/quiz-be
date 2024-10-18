package com.quiz.project.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.quiz.project.config.ModelMapperConfig;
import com.quiz.project.dto.req.SentenceReqDTO;
import com.quiz.project.dto.resp.SentenceRespDTO;
import com.quiz.project.entity.CourseEntity;
import com.quiz.project.entity.SentenceEntity;
import com.quiz.project.repository.SentenceRepository;
import com.quiz.project.util.ConvertModel;
import com.quiz.project.util.error.InvalidException;

@Service
public class SentenceService {
	private SentenceRepository sentenceRepository;
	private CourseService courseService;

	public SentenceService(SentenceRepository sentenceRepository, CourseService courseService) {
		this.sentenceRepository = sentenceRepository;
		this.courseService = courseService;
	}

	private boolean checkExistsById(Long id) {
		return this.sentenceRepository.existsById(id);
	}

	public SentenceEntity reqDtoToEntity(SentenceReqDTO sentenceReqDto, Long courseId) {
		SentenceEntity sentenceEntity = new SentenceEntity();
		ConvertModel.getModelMapping(sentenceReqDto, sentenceEntity);

		CourseEntity courseEntity = this.courseService.getCourseById(courseId);
		sentenceEntity.setCourse(courseEntity);

		return sentenceEntity;
	}

	public SentenceRespDTO entityToRespDto(SentenceEntity sentenceEntity) {
		SentenceRespDTO sentenceRespDto = new SentenceRespDTO();

		ConvertModel.getModelMapping(sentenceEntity, sentenceRespDto);

		return sentenceRespDto;
	}

	public void createSentence(SentenceReqDTO sentenceReqDto, Long courseId) throws InvalidException {
		if (sentenceReqDto.getImage() == null) {
			sentenceReqDto.setImage("term-default.png");
		}

		this.sentenceRepository.save(reqDtoToEntity(sentenceReqDto, courseId));
	}

	public SentenceRespDTO updateSentence(SentenceReqDTO sentenceReqDTO) {
		SentenceEntity sentenceEntity = ModelMapperConfig.map(sentenceReqDTO, SentenceEntity.class);
		SentenceRespDTO sentenceRespDto = ModelMapperConfig.map(sentenceEntity, SentenceRespDTO.class);
		return sentenceRespDto;
	}

	public void deleteSentence(Long id) throws InvalidException {
		if (!checkExistsById(id)) {
			throw new InvalidException("Id invalid");
		}
		this.sentenceRepository.deleteById(id);
	}

	public SentenceRespDTO getSentence(Long id) throws InvalidException {
		if (!checkExistsById(id)) {
			throw new InvalidException("Id invalid");
		}
		SentenceEntity sentenceEntity = this.sentenceRepository.findById(id).get();

		return entityToRespDto(sentenceEntity);
	}

	public List<SentenceRespDTO> getAllSentenceByCourseId(Long courseId) throws InvalidException {
		CourseEntity courseEntity = this.courseService.getCourseById(courseId);
		if (courseEntity == null) {
			throw new InvalidException("Id invalid");
		}
		List<SentenceEntity> sentenceEntities = this.sentenceRepository.findAllByCourseId(courseId);
		List<SentenceRespDTO> sentenceRespDtos = new ArrayList<SentenceRespDTO>();

		for (SentenceEntity sentenceEntity : sentenceEntities) {
			sentenceRespDtos.add(entityToRespDto(sentenceEntity));
		}

		return sentenceRespDtos;
	}

	public int countSentencesByCourseId(Long id) {
		return this.sentenceRepository.countSentencesByCourseId(id);
	}

	public void deleteSentenceByCourseId(Long courseId) throws InvalidException {
		CourseEntity courseEntity = this.courseService.getCourseById(courseId);
		if (courseEntity == null) {
			throw new InvalidException("Id invalid");
		}
		this.sentenceRepository.deleteAllByCourseId(courseId);
	}

	public void deleteSentenceByListId(List<Long> ids) {
		this.sentenceRepository.deleteByIdIn(ids);
	}
}
