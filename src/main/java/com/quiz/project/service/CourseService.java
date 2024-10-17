package com.quiz.project.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.quiz.project.config.ModelMapperConfig;
import com.quiz.project.dto.req.CourseReqDTO;
import com.quiz.project.dto.req.SentenceReqDTO;
import com.quiz.project.dto.resp.AuthRespDTO;
import com.quiz.project.dto.resp.CourseRespDTO;
import com.quiz.project.dto.resp.MajorRespDTO;
import com.quiz.project.dto.resp.QuizTestRespDto;
import com.quiz.project.dto.resp.SentenceRespDTO;
import com.quiz.project.dto.resp.SentenceTestRespDto;
import com.quiz.project.dto.resp.SubjectRespDTO;
import com.quiz.project.entity.CourseEntity;
import com.quiz.project.entity.MajorEntity;
import com.quiz.project.entity.SubjectEntity;
import com.quiz.project.entity.UserEntity;
import com.quiz.project.repository.CourseRepository;
import com.quiz.project.repository.MajorRepository;
import com.quiz.project.repository.SubjectRepository;
import com.quiz.project.repository.UserRepository;
import com.quiz.project.util.ConvertModel;
import com.quiz.project.util.error.InvalidException;

@Service
public class CourseService {
	private CourseRepository courseRepository;
	private MajorRepository majorRepository;
	private UserRepository userRepository;
	private SentenceService sentenceService;
	private SubjectRepository subjectRepository;

	public CourseService(CourseRepository courseRepository, MajorRepository majorRepository,
			UserRepository userRepository, SentenceService sentenceService, SubjectRepository subjectRepository) {
		this.courseRepository = courseRepository;
		this.majorRepository = majorRepository;
		this.userRepository = userRepository;
		this.sentenceService = sentenceService;
		this.subjectRepository = subjectRepository;
	}

	private boolean checkExistsById(Long id) {
		return this.courseRepository.existsById(id);
	}

	public CourseRespDTO createCourse(CourseReqDTO newCourse) throws InvalidException {
		boolean isMajorExists = this.majorRepository.existsById(newCourse.getMajorId());
		if (!isMajorExists) {
			throw new InvalidException("major does not exist");
		}
		boolean isUserExists = this.userRepository.existsById(newCourse.getUserId());
		if (!isUserExists) {
			throw new InvalidException("user does not exist");
		}

		MajorEntity majorEntity = this.majorRepository.findById(newCourse.getMajorId()).get();
		UserEntity userEntity = this.userRepository.findById(newCourse.getUserId()).get();
		CourseEntity newCourseEntity = ModelMapperConfig.map(newCourse, CourseEntity.class);
		newCourseEntity.setMajor(majorEntity);
		newCourseEntity.setUser(userEntity);

		CourseEntity courseEntity = this.courseRepository.save(newCourseEntity);

		List<SentenceReqDTO> sentenceReqDtos = newCourse.getSentences();
		for (SentenceReqDTO senReqDTO : sentenceReqDtos) {
			this.sentenceService.createSentence(senReqDTO, courseEntity.getId());
		}

		MajorRespDTO majorRespDTO = new MajorRespDTO();
		ConvertModel.getModelMapping(majorEntity, majorRespDTO);

		AuthRespDTO.UserLogin userLogin = new AuthRespDTO.UserLogin();
		ConvertModel.getModelMapping(userEntity, userLogin);

		CourseRespDTO courseRespDTO = new CourseRespDTO();
		courseRespDTO.setMajor(majorRespDTO);
		courseRespDTO.setUser(userLogin);
		ConvertModel.getModelMapping(courseEntity, courseRespDTO);

		int count = this.sentenceService.countSentencesByCourseId(courseEntity.getId());
		courseRespDTO.setCountSentence(count);

		return courseRespDTO;
	}

	public List<CourseRespDTO> getListCourseByUserId(Long id) {
		List<CourseEntity> listCourseEntities = this.courseRepository.findAllByUserId(id);
		List<CourseRespDTO> listCourseRespDtos = new ArrayList<CourseRespDTO>();
		for (CourseEntity courseEntity : listCourseEntities) {
			CourseRespDTO courseRespDTO = new CourseRespDTO();
			ConvertModel.getModelMapping(courseEntity, courseRespDTO);
			int count = this.sentenceService.countSentencesByCourseId(courseEntity.getId());
			courseRespDTO.setCountSentence(count);

			MajorEntity majorEntity = this.majorRepository.findById(courseEntity.getMajor().getId()).get();
			MajorRespDTO majorRespDTO = new MajorRespDTO();
			ConvertModel.getModelMapping(majorEntity, majorRespDTO);
			courseRespDTO.setMajor(majorRespDTO);

			UserEntity userEntity = this.userRepository.findById(courseEntity.getUser().getId()).get();
			AuthRespDTO.UserLogin userLogin = new AuthRespDTO.UserLogin();
			ConvertModel.getModelMapping(userEntity, userLogin);
			courseRespDTO.setUser(userLogin);

			listCourseRespDtos.add(courseRespDTO);
		}
		return listCourseRespDtos;
	}

	public CourseRespDTO updateCourse(Long id, CourseReqDTO courseReqDTO) throws InvalidException {
		if (courseReqDTO.getId() == null) {
			throw new InvalidException("Id is required");
		}
		if (!checkExistsById(id)) {
			throw new InvalidException("Id invalid");
		}
		boolean isMajorExists = this.majorRepository.existsById(courseReqDTO.getMajorId());
		if (!isMajorExists) {
			throw new InvalidException("major does not exist");
		}
		boolean isUserExists = this.userRepository.existsById(courseReqDTO.getUserId());
		if (!isUserExists) {
			throw new InvalidException("user does not exist");
		}
		CourseEntity courseEntityCheck = this.courseRepository.findAllByIdAndUserId(id, courseReqDTO.getUserId())
				.orElse(null);
		if (courseEntityCheck == null) {
			throw new InvalidException("user id or course id invalid ");
		}

		List<SentenceRespDTO> sentenceRespDTOs = this.sentenceService.getAllSentenceByCourseId(id);
		List<SentenceReqDTO> sentenceReqDtos = courseReqDTO.getSentences();
		for (SentenceReqDTO senReqDTO : sentenceReqDtos) {
			this.sentenceService.createSentence(senReqDTO, courseReqDTO.getId());
		}
		List<Long> listIdInReq = sentenceReqDtos.stream().map(item -> item.getId()).collect(Collectors.toList());
		List<Long> listIdDelete = new ArrayList<Long>();
		for (SentenceRespDTO sentence : sentenceRespDTOs) {
			if (!listIdInReq.contains(sentence.getId())) {
				listIdDelete.add(sentence.getId());
			}
		}
		this.sentenceService.deleteSentenceByListId(listIdDelete);

		MajorEntity majorEntity = this.majorRepository.findById(courseReqDTO.getMajorId()).get();
		UserEntity userEntity = this.userRepository.findById(courseReqDTO.getUserId()).get();
		CourseEntity courseEntity = new CourseEntity();
		ConvertModel.getModelMapping(courseReqDTO, courseEntity);
		courseEntity.setMajor(majorEntity);
		courseEntity.setUser(userEntity);

		CourseEntity saveCourse = this.courseRepository.save(courseEntity);
		CourseRespDTO courseRespDto = new CourseRespDTO();
		ConvertModel.getModelMapping(saveCourse, courseRespDto);

		MajorRespDTO majorRespDTO = new MajorRespDTO();
		ConvertModel.getModelMapping(majorEntity, majorRespDTO);
		courseRespDto.setMajor(majorRespDTO);

		AuthRespDTO.UserLogin userLogin = new AuthRespDTO.UserLogin();
		ConvertModel.getModelMapping(userEntity, userLogin);
		courseRespDto.setUser(userLogin);

		int count = this.sentenceService.countSentencesByCourseId(id);
		courseRespDto.setCountSentence(count);

		return courseRespDto;
	}

	public void deleteCourse(Long id) throws InvalidException {
		boolean isExist = this.courseRepository.existsById(id);
		if (!isExist) {
			throw new InvalidException("Id invalid");
		}
		this.sentenceService.deleteSentenceByCourseId(id);
		this.courseRepository.deleteById(id);
	}

	public CourseRespDTO getCourse(Long id) throws InvalidException {
		if (!checkExistsById(id)) {
			throw new InvalidException("Id invalid");
		}
		CourseEntity courseEntity = this.courseRepository.findById(id).get();
		CourseRespDTO courseRespDto = ModelMapperConfig.map(courseEntity, CourseRespDTO.class);
		courseRespDto.setMajor(ModelMapperConfig.map(courseEntity.getMajor(), MajorRespDTO.class));
		courseRespDto.setUser(ModelMapperConfig.map(courseEntity.getUser(), AuthRespDTO.UserLogin.class));
		int count = this.sentenceService.countSentencesByCourseId(id);
		courseRespDto.setCountSentence(count);
		return courseRespDto;
	}

	public static <T> void shuffleList(final List<T> list) {

		int length = list.size();
		Random random = new Random();

		for (int i = length - 1; i > 0; i--) {

			int j = random.nextInt(i + 1);
			T temp = list.get(i);

			list.set(i, list.get(j));
			list.set(j, temp);
		}
	}

	public QuizTestRespDto generateQuizTest(Map<String, String> map, Long questionNumber) {
		Set<String> keySet = map.keySet();
		List<SentenceTestRespDto> listSentenceTestRespDtos = new ArrayList<SentenceTestRespDto>();
		Map<String, String> solution = new HashMap<String, String>();
		QuizTestRespDto quizTestRespDto = new QuizTestRespDto();
		if (map.size() < 5) {
			for (String key : keySet) {
				SentenceTestRespDto sentencesTestRespDTO = new SentenceTestRespDto();
				sentencesTestRespDTO.setQuestion(key);
				Set<String> listAnswer = new HashSet<String>();
				listAnswer.add(map.get(key));
				solution.put(key, map.get(key));
				for (String k : keySet) {
					listAnswer.add(map.get(k));
				}
				sentencesTestRespDTO.setAnswer(new ArrayList<String>(listAnswer));
				listSentenceTestRespDtos.add(sentencesTestRespDTO);
			}
			quizTestRespDto.setQuestionNumber(questionNumber);
			quizTestRespDto.setListSentence(listSentenceTestRespDtos);
			quizTestRespDto.setSolution(solution);
			return quizTestRespDto;
		}
		List<String> key = new ArrayList<String>(keySet);
		Random rand = new Random();
		for (int i = 0; i < questionNumber; i++) {
			SentenceTestRespDto sentencesTestRespDTO = new SentenceTestRespDto();
			sentencesTestRespDTO.setQuestion(key.get(i));
			Set<String> listAnswer = new HashSet<String>();
			listAnswer.add(map.get(key.get(i)));
			solution.put(key.get(i), map.get(key.get(i)));
			while (listAnswer.size() < 4) {
				int randomIndex = rand.nextInt(key.size());
				listAnswer.add(map.get(key.get(randomIndex)));
			}
			sentencesTestRespDTO.setAnswer(new ArrayList<String>(listAnswer));
			listSentenceTestRespDtos.add(sentencesTestRespDTO);
		}
		quizTestRespDto.setQuestionNumber(questionNumber);
		quizTestRespDto.setListSentence(listSentenceTestRespDtos);
		quizTestRespDto.setSolution(solution);
		return quizTestRespDto;
	}

	public QuizTestRespDto getQuizTest(Long courseId, Long questionNumber) throws InvalidException {
		CourseEntity courseEntity = this.courseRepository.findById(courseId).orElse(null);
		if (courseEntity == null) {
			throw new InvalidException("Course id invalid");
		}
		List<SentenceRespDTO> listRespDTOs = this.sentenceService.getAllSentenceByCourseId(courseId);
		if (questionNumber > listRespDTOs.size()) {
			throw new InvalidException("Question number invalid");
		}
		shuffleList(listRespDTOs);
		Map<String, String> mapSentences = new LinkedHashMap<String, String>();
		for (SentenceRespDTO sentenceRespDTO : listRespDTOs) {
			mapSentences.put(sentenceRespDTO.getTerm(), sentenceRespDTO.getDefinition());
		}
		QuizTestRespDto quizTestRespDto = generateQuizTest(mapSentences, questionNumber);
		quizTestRespDto.setTitle(courseEntity.getTitle());

		return quizTestRespDto;
	}

	public Page<CourseRespDTO> searchCourse(String cname, Pageable pageable) {
		Page<CourseEntity> result = this.courseRepository.findByTitleContainingOrDescriptionContaining(cname, cname,
				pageable);
		List<CourseRespDTO> lisCourseRespDTOs = new ArrayList<CourseRespDTO>();

		for (CourseEntity courseEntity : result) {
			MajorEntity majorEntity = this.majorRepository.findById(courseEntity.getMajor().getId()).orElse(null);
			MajorRespDTO majorRespDTO = new MajorRespDTO();
			ConvertModel.getModelMapping(majorEntity, majorRespDTO);

			UserEntity userEntity = this.userRepository.findById(courseEntity.getUser().getId()).orElse(null);
			AuthRespDTO.UserLogin userLogin = new AuthRespDTO.UserLogin();
			ConvertModel.getModelMapping(userEntity, userLogin);

			int count = this.sentenceService.countSentencesByCourseId(courseEntity.getId());

			CourseRespDTO courseRespDTO = new CourseRespDTO();
			ConvertModel.getModelMapping(courseEntity, courseRespDTO);
			courseRespDTO.setMajor(majorRespDTO);
			courseRespDTO.setUser(userLogin);
			courseRespDTO.setCountSentence(count);
			lisCourseRespDTOs.add(courseRespDTO);
		}

		return new PageImpl<CourseRespDTO>(lisCourseRespDTOs, pageable, result.getTotalElements());
	}

	public Page<CourseRespDTO> getCourseBySubjectId(Long id, Pageable pageable) throws InvalidException {
		boolean checkSubjectExists = this.subjectRepository.existsById(id);
		if (!checkSubjectExists) {
			throw new InvalidException("Subject id invalid");
		}
		List<Long> listIdMajor;
		if (id == 1) {
			Long a[] = new Long[] { 1L, 2L, 3L, 4L, 5L };
			listIdMajor = Arrays.asList(a);
		} else if (id == 2) {
			Long a[] = new Long[] { 6L, 7L, 8L, 9L, 10L, 11L, 12L };
			listIdMajor = Arrays.asList(a);
		} else if (id == 3) {
			Long a[] = new Long[] { 13L, 14L, 15L, 16L, 17L, 18L };
			listIdMajor = Arrays.asList(a);
		} else if (id == 4) {
			Long a[] = new Long[] { 19L, 20L, 21L, 22L, 23L, 24L };
			listIdMajor = Arrays.asList(a);
		} else {
			Long a[] = new Long[] { 25L, 26L, 27L };
			listIdMajor = Arrays.asList(a);
		}

		Page<CourseEntity> result = this.courseRepository.findByMajorIdIn(listIdMajor, pageable);
		List<CourseRespDTO> lisCourseRespDTOs = new ArrayList<CourseRespDTO>();

		for (CourseEntity courseEntity : result) {
			MajorEntity majorEntity = this.majorRepository.findById(courseEntity.getMajor().getId()).orElse(null);
			MajorRespDTO majorRespDTO = new MajorRespDTO();
			ConvertModel.getModelMapping(majorEntity, majorRespDTO);

			UserEntity userEntity = this.userRepository.findById(courseEntity.getUser().getId()).orElse(null);
			AuthRespDTO.UserLogin userLogin = new AuthRespDTO.UserLogin();
			ConvertModel.getModelMapping(userEntity, userLogin);

			int count = this.sentenceService.countSentencesByCourseId(courseEntity.getId());

			CourseRespDTO courseRespDTO = new CourseRespDTO();
			ConvertModel.getModelMapping(courseEntity, courseRespDTO);
			courseRespDTO.setMajor(majorRespDTO);
			courseRespDTO.setUser(userLogin);
			courseRespDTO.setCountSentence(count);
			lisCourseRespDTOs.add(courseRespDTO);
		}

		return new PageImpl<CourseRespDTO>(lisCourseRespDTOs, pageable, result.getTotalElements());
	}
}
