package com.quiz.project.util.error;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.quiz.project.dto.Response;

@RestControllerAdvice
public class GlobalException {

	@ExceptionHandler(value = InvalidException.class)
	public ResponseEntity<Response<Object>> invalidException(Exception ex) {
		Response<Object> res = new Response<Object>();
		res.setStatusCode(HttpStatus.BAD_REQUEST.value());
		res.setError(ex.getMessage());
		res.setMessage("Exception occurred...");
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
	}

	@ExceptionHandler(value = { UsernameNotFoundException.class, BadCredentialsException.class })
	public ResponseEntity<Response<Object>> usernameAndPasswordException(Exception ex) {
		Response<Object> res = new Response<Object>();
		res.setStatusCode(HttpStatus.UNAUTHORIZED.value());
		res.setError("Incorrect username or password");
		res.setMessage(ex.getMessage());
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(res);
	}

	@ExceptionHandler(value = MethodArgumentNotValidException.class)
	public ResponseEntity<Response<Object>> validationError(MethodArgumentNotValidException notValidException) {
		BindingResult result = notValidException.getBindingResult();
		final List<FieldError> fieldError = result.getFieldErrors();

		Response<Object> res = new Response<Object>();
		res.setStatusCode(HttpStatus.BAD_REQUEST.value());
		res.setError(notValidException.getBody().getDetail());

		List<String> errors = fieldError.stream().map(f -> f.getDefaultMessage()).collect(Collectors.toList());
		res.setMessage(errors.size() > 1 ? errors : errors.get(0));
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
	}

	@ExceptionHandler(value = NoResourceFoundException.class)
	public ResponseEntity<Response<Object>> noResourceFoundException(Exception ex) {
		Response<Object> res = new Response<Object>();
		res.setStatusCode(HttpStatus.NOT_FOUND.value());
		res.setError("404 not found, URL may be not exist");
		res.setMessage(ex.getMessage());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
	}

	@ExceptionHandler({ AccessDeniedException.class })
	public ResponseEntity<Response<Object>> accessDeniedException(AccessDeniedException ex) {
		Response<Object> res = new Response<Object>();
		res.setStatusCode(HttpStatus.FORBIDDEN.value());
		res.setMessage(ex.getMessage());
		res.setError("No permission");
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(res);
	}

	@ExceptionHandler({ Exception.class })
	public ResponseEntity<Response<Object>> handleAll(Exception ex) {
		Response<Object> res = new Response<Object>();
		res.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		res.setMessage(ex.getMessage());
		res.setError("Error occurred...");
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
	}
}
