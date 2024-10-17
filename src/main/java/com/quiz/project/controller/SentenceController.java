package com.quiz.project.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.quiz.project.dto.resp.SentenceRespDTO;
import com.quiz.project.service.SentenceService;
import com.quiz.project.util.annotation.ApiMessage;
import com.quiz.project.util.error.InvalidException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping(value = "/sentence")
public class SentenceController {
	private SentenceService sentenceService;

	public SentenceController(SentenceService sentenceService) {
		this.sentenceService = sentenceService;
	}

	@DeleteMapping(value = "/{id}")
	public ResponseEntity<Void> deleteSentence(@PathVariable("id") Long id) throws InvalidException {
		this.sentenceService.deleteSentence(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/all/{courseId}")
	@ApiMessage(value = "Get all sentences by course id success")
	public ResponseEntity<List<SentenceRespDTO>> getMethodName(@PathVariable("courseId") Long courseId) throws InvalidException {
		List<SentenceRespDTO> lisDtos = this.sentenceService.getAllSentenceByCourseId(courseId);
		return ResponseEntity.ok().body(lisDtos);
	}

}
