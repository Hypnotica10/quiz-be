package com.quiz.project.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.quiz.project.config.ModelMapperConfig;
import com.quiz.project.dto.req.SentenceReqDTO;
import com.quiz.project.dto.resp.SentenceRespDTO;
import com.quiz.project.entity.CourseEntity;
import com.quiz.project.entity.SentenceEntity;
import com.quiz.project.repository.CourseRepository;
import com.quiz.project.repository.SentenceRepository;
import com.quiz.project.util.error.InvalidException;

@Service
public class SentenceService {
	private SentenceRepository sentenceRepository;
	private CourseRepository courseRepository;

	public SentenceService(SentenceRepository sentenceRepository, CourseRepository courseRepository) {
		this.sentenceRepository = sentenceRepository;
		this.courseRepository = courseRepository;
	}

	private boolean checkExistsById(Long id) {
		return this.sentenceRepository.existsById(id);
	}

	public void createSentence(SentenceReqDTO sentenceReqDTO, Long courseId) throws InvalidException {
		if (sentenceReqDTO.getImage() == null) {
			sentenceReqDTO.setImage("term-default.png");
		}
		SentenceEntity sentenceEntity = ModelMapperConfig.map(sentenceReqDTO, SentenceEntity.class);
		CourseEntity courseEntity = this.courseRepository.findById(courseId).get();
		sentenceEntity.setCourse(courseEntity);
		this.sentenceRepository.save(sentenceEntity);
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
		SentenceRespDTO sentenceRespDto = ModelMapperConfig.map(sentenceEntity, SentenceRespDTO.class);
		return sentenceRespDto;
	}

	public List<SentenceRespDTO> getAllSentenceByCourseId(Long courseId) throws InvalidException {
		CourseEntity courseEntity = this.courseRepository.findById(courseId).orElse(null);
		if (courseEntity == null) {
			throw new InvalidException("Id invalid");
		}
		List<SentenceEntity> sentenceEntities = this.sentenceRepository.findAllByCourseId(courseId);
		List<SentenceRespDTO> sentenceRespDTOs = ModelMapperConfig.mapAll(sentenceEntities, SentenceRespDTO.class);
		return sentenceRespDTOs;
	}

	public int countSentencesByCourseId(Long id) {
		return this.sentenceRepository.countSentencesByCourseId(id);
	}

	public void deleteSentenceByCourseId(Long courseId) throws InvalidException {
		CourseEntity courseEntity = this.courseRepository.findById(courseId).orElse(null);
		if (courseEntity == null) {
			throw new InvalidException("Id invalid");
		}
		this.sentenceRepository.deleteAllByCourseId(courseId);
	}

	public void deleteSentenceByListId(List<Long> ids) {
		this.sentenceRepository.deleteByIdIn(ids);
	}
}
