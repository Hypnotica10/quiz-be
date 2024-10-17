package com.quiz.project.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.quiz.project.dto.resp.QuizTestRespDto;
import com.quiz.project.dto.resp.SentenceTestRespDto;
import com.quiz.project.service.CourseService;
import com.quiz.project.util.error.InvalidException;

@RestController
@RequestMapping("/quiz-test")
public class QuizTest {

	private CourseService courseService;

	public QuizTest(CourseService courseService) {
		this.courseService = courseService;
	}

	@GetMapping(value = "")
	public ResponseEntity<QuizTestRespDto> getQuizTest(@RequestParam("id") Long id,
			@RequestParam("limit") Long limit) throws InvalidException {
		return ResponseEntity.ok().body(this.courseService.getQuizTest(id, limit));
	}
}
