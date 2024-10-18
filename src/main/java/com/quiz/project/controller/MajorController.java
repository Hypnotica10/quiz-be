package com.quiz.project.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.quiz.project.dto.req.MajorReqDTO;
import com.quiz.project.dto.resp.MajorRespDTO;
import com.quiz.project.service.MajorService;
import com.quiz.project.util.annotation.ApiMessage;
import com.quiz.project.util.error.InvalidException;

import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/major")
public class MajorController {

	private MajorService majorService;

	public MajorController(MajorService majorService) {
		this.majorService = majorService;
	}

	@PostMapping(value = "")
	@ApiMessage(value = "Create major success")
	public ResponseEntity<Void> createSubject(@Valid @RequestBody MajorReqDTO newMajor) throws InvalidException {
		this.majorService.createMajor(newMajor);
		return ResponseEntity.created(null).build();
	}

	@GetMapping(value = "/all")
	@ApiMessage(value = "List major")
	public ResponseEntity<List<MajorRespDTO>> getListSubject() throws InvalidException {
		return ResponseEntity.ok().body(this.majorService.getListMajor());
	}

}
