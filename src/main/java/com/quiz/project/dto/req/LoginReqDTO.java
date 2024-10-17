package com.quiz.project.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoginReqDTO {
	@NotBlank(message = "username is required")
	private String username;
	@NotBlank(message = "password is required")
	private String password;
}
