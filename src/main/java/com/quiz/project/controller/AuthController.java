package com.quiz.project.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.quiz.project.config.JwtConfig;
import com.quiz.project.config.security.SecurityUtil;
import com.quiz.project.dto.req.LoginReqDTO;
import com.quiz.project.dto.req.UserReqDTO;
import com.quiz.project.dto.resp.AuthRespDTO;
import com.quiz.project.dto.resp.UserRespDTO;
import com.quiz.project.entity.UserEntity;
import com.quiz.project.service.UserService;
import com.quiz.project.util.annotation.ApiMessage;
import com.quiz.project.util.error.InvalidException;

@RestController
@RequestMapping("/auth")
public class AuthController {

	private AuthenticationManagerBuilder authenticationManagerBuilder;
	private JwtConfig jwtConfig;
	private UserService userService;
	private SecurityUtil securityUtil;

	@Value("${jwt.refresh-token.expiration-time-seconds}")
	private long expirationTimeRefreshToken;

	public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder, JwtConfig jwtConfig,
			UserService userService, SecurityUtil securityUtil) {
		this.authenticationManagerBuilder = authenticationManagerBuilder;
		this.jwtConfig = jwtConfig;
		this.userService = userService;
		this.securityUtil = securityUtil;
	}

	@PostMapping(value = "/signin")
	@ApiMessage(value = "Signin success")
	public ResponseEntity<AuthRespDTO> authenticate(@RequestBody LoginReqDTO loginReqDTO) {
		// nap input gom username / password vao security
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
				loginReqDTO.getUsername(), loginReqDTO.getPassword());

		// xac thuc nguoi dung => can viet ham loadUserByUsername
		Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

		// xet thong tin nguoi dung dang nhap vao securitycontext
		SecurityContextHolder.getContext().setAuthentication(authentication);

		AuthRespDTO authRespDTO = new AuthRespDTO();
		UserEntity userEntity = this.userService.getUserByUsername(loginReqDTO.getUsername());
		if (userEntity != null) {
			AuthRespDTO.UserLogin userLogin = new AuthRespDTO.UserLogin(userEntity.getId(), userEntity.getUsername(),
					userEntity.getName(), userEntity.getAvatar());
			authRespDTO.setUser(userLogin);
		}
		String accessToken = this.jwtConfig.generateToken(authentication.getName(), authRespDTO.getUser());
		authRespDTO.setAccessToken(accessToken);

		// create refresh token
		String refreshToken = this.jwtConfig.generateRefreshToken(loginReqDTO.getUsername(), authRespDTO);

		// set cookie
		ResponseCookie responseCookie = ResponseCookie.from("refresh-token", refreshToken).httpOnly(true)
				.maxAge(expirationTimeRefreshToken).secure(true).path("/").build();

		return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, responseCookie.toString()).body(authRespDTO);
	}

	@GetMapping(value = "/account/{id}")
	@ApiMessage("fetch account")
	public ResponseEntity<UserRespDTO> getAccount(@PathVariable("id") Long id) throws InvalidException {
		return ResponseEntity.ok().body(this.userService.getUserById(id));
	}

	@GetMapping(value = "/refresh")
	@ApiMessage(value = "Get user by refresh token")
	public ResponseEntity<AuthRespDTO> getRefreshToken(
			@CookieValue(name = "refresh-token", defaultValue = "refresh-token") String refreshToken)
			throws InvalidException {
		if (refreshToken == null || refreshToken.equals("refresh-token")) {
			throw new InvalidException("Required cookie 'refresh-token' for method parameter is not present");
		}

		// check valid
		Jwt decodedToken = this.jwtConfig.checkValidRefreshToken(refreshToken);
		String username = decodedToken.getSubject();

		// check user tu username va token
		UserEntity currentUser = this.userService.getUserByUsername(username);
		if (currentUser == null) {
			throw new InvalidException("Refresh token invalid");
		}

		AuthRespDTO authRespDTO = new AuthRespDTO();
		UserEntity userEntity = this.userService.getUserByUsername(username);
		if (userEntity != null) {
			AuthRespDTO.UserLogin userLogin = new AuthRespDTO.UserLogin(userEntity.getId(), userEntity.getUsername(),
					userEntity.getName(), userEntity.getAvatar());
			authRespDTO.setUser(userLogin);
		}
		String accessToken = this.jwtConfig.generateToken(username, authRespDTO.getUser());
		authRespDTO.setAccessToken(accessToken);

		// create refresh token
		String newRefreshToken = this.jwtConfig.generateRefreshToken(username, authRespDTO);

		// set cookie
		ResponseCookie responseCookie = ResponseCookie.from("refresh-token", newRefreshToken).httpOnly(true)
				.maxAge(expirationTimeRefreshToken).secure(true).path("/").build();

		return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, responseCookie.toString()).body(authRespDTO);
	}

	@PostMapping(value = "/signout")
	@ApiMessage(value = "Signout success")
	public ResponseEntity<Void> logout() throws InvalidException {
		String username = this.securityUtil.getCurrentUserLogin().isPresent()
				? this.securityUtil.getCurrentUserLogin().get()
				: "";
		if (username.equals("")) {
			throw new InvalidException("Token invalid");
		}

		ResponseCookie deleteCookie = ResponseCookie.from("refresh-token", null).httpOnly(true).maxAge(0).secure(true)
				.path("/").build();

		return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, deleteCookie.toString()).body(null);
	}

	@PostMapping(value = "/update")
	@ApiMessage(value = "Update success")
	public ResponseEntity<UserRespDTO> updateUser(@RequestBody UserReqDTO userReqDTO) throws InvalidException {
		String username = this.securityUtil.getCurrentUserLogin().isPresent()
				? this.securityUtil.getCurrentUserLogin().get()
				: "";
		if (username.equals("")) {
			throw new InvalidException("Token invalid");
		}

		return ResponseEntity.ok().body(this.userService.updateUser(userReqDTO, username));
	}
}
