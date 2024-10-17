package com.quiz.project.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.quiz.project.dto.req.CourseReqDTO;
import com.quiz.project.dto.req.SubjectReqDTO;
import com.quiz.project.dto.resp.CourseRespDTO;
import com.quiz.project.dto.resp.SubjectRespDTO;
import com.quiz.project.service.CourseService;
import com.quiz.project.util.annotation.ApiMessage;
import com.quiz.project.util.error.InvalidException;

import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/course")
public class CourseController {

	private CourseService courseService;

	public CourseController(CourseService courseService) {
		this.courseService = courseService;
	}

	@PostMapping(value = "")
	@ApiMessage(value = "Create course success")
	public ResponseEntity<CourseRespDTO> createCourse(@Valid @RequestBody CourseReqDTO newCourse)
			throws InvalidException {
		return ResponseEntity.ok().body(this.courseService.createCourse(newCourse));
	}

	@GetMapping(value = "/user/{id}")
	@ApiMessage(value = "List course")
	public ResponseEntity<List<CourseRespDTO>> getListCourseByUserId(@PathVariable("id") Long id)
			throws InvalidException {
		return ResponseEntity.ok().body(this.courseService.getListCourseByUserId(id));
	}

	@PutMapping(value = "/{id}")
	@ApiMessage(value = "Update course success")
	public ResponseEntity<CourseRespDTO> updateCourse(@PathVariable("id") Long id,
			@Valid @RequestBody CourseReqDTO courseReqDTO) throws InvalidException {
		return ResponseEntity.ok().body(this.courseService.updateCourse(id, courseReqDTO));
	}

	@DeleteMapping(value = "/{id}")
	@ApiMessage(value = "Delete course success")
	public ResponseEntity<Void> updateSubject(@PathVariable("id") Long id) throws InvalidException {
		this.courseService.deleteCourse(id);
		return ResponseEntity.ok().build();
	}

	@GetMapping(value = "/{id}")
	@ApiMessage(value = "Get course success")
	public ResponseEntity<CourseRespDTO> getCourse(@PathVariable("id") Long id) throws InvalidException {
		return ResponseEntity.ok().body(this.courseService.getCourse(id));
	}

	@GetMapping("/search")
	public ResponseEntity<Page<CourseRespDTO>> searchCourse(@RequestParam("cname") String name,
			@RequestParam(name = "page", defaultValue = "0") int page) {
		int pageSize = 9;
		Pageable pageable = PageRequest.of(page, pageSize, Sort.by("title"));
		return ResponseEntity.ok().body(this.courseService.searchCourse(name, pageable));
	}
	
	@GetMapping("")
	public ResponseEntity<Page<CourseRespDTO>> getAllCourseBySubjectId(@RequestParam(name = "id", defaultValue = "1") Long id, @RequestParam(name = "page", defaultValue = "0") int page) throws InvalidException {
		int pageSize = 9;
		Pageable pageable = PageRequest.of(page, pageSize);
		return ResponseEntity.ok().body(this.courseService.getCourseBySubjectId(id, pageable));
	}
}
