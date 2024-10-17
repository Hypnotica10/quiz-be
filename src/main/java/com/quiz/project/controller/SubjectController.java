package com.quiz.project.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.quiz.project.dto.req.SubjectReqDTO;
import com.quiz.project.dto.resp.SubjectRespDTO;
import com.quiz.project.repository.SubjectRepository;
import com.quiz.project.service.SubjectService;
import com.quiz.project.util.annotation.ApiMessage;
import com.quiz.project.util.error.InvalidException;

import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/subject")
public class SubjectController {

	private SubjectService subjectService;

	public SubjectController(SubjectService subjectService) {
		this.subjectService = subjectService;
	}

	@PostMapping(value = "")
	@ApiMessage(value = "Create subject success")
	public ResponseEntity<Void> createSubject(@Valid @RequestBody SubjectReqDTO newSubject) throws InvalidException {
		this.subjectService.createSubject(newSubject);
		return ResponseEntity.created(null).build();
	}

	@GetMapping(value = "/all")
	@ApiMessage(value = "List subject")
	public ResponseEntity<List<SubjectRespDTO>> getListSubject() throws InvalidException {
		return ResponseEntity.ok().body(this.subjectService.getListSubject());
	}

	@PutMapping(value = "/{id}")
	@ApiMessage(value = "Update subject success")
	public ResponseEntity<SubjectRespDTO> updateSubject(@PathVariable("id") Long id,
			@RequestBody SubjectReqDTO subjectReqDTO) throws InvalidException {
		return ResponseEntity.ok().body(this.subjectService.updateSubject(id, subjectReqDTO));
	}

	@DeleteMapping(value = "/{id}")
	@ApiMessage(value = "Delete subject success")
	public ResponseEntity<Void> updateSubject(@PathVariable("id") Long id) throws InvalidException {
		this.subjectService.deleteSubject(id);
		return ResponseEntity.noContent().build();
	}
}
