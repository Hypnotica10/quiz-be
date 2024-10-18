package com.quiz.project.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.quiz.project.dto.req.UserReqDTO;
import com.quiz.project.dto.resp.UserRespDTO;
import com.quiz.project.service.UserService;
import com.quiz.project.util.annotation.ApiMessage;
import com.quiz.project.util.error.InvalidException;

import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/users")
public class UserController {

	private UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@PostMapping(value = "")
	@ApiMessage(value = "Create account success")
	public ResponseEntity<UserRespDTO> createNewUser(@Valid @RequestBody UserReqDTO newUser) throws InvalidException {
		return ResponseEntity.created(null).body(this.userService.createNewUser(newUser));
	}

	@GetMapping(value = "/{id}")
	@ApiMessage(value = "Get user success")
	public ResponseEntity<UserRespDTO> getUserById(@PathVariable("id") Long userId) throws InvalidException {
		return ResponseEntity.ok().body(this.userService.getUserById(userId));
	}

	// update user refresh-token
//	this.userService.updateUserRefreshToken(refresh-token, username)
	// set cookie
//	ResponseCookie resCookie = ResponseCookie.from("refresh-token", refresh-token).httpOnly(true).path("/").maxAge(refreshToken).domain("").secure(true).build();
//	ResponseEntity.header(HttpHeaders.SET_COOKIE, resCookie.toString())

}
