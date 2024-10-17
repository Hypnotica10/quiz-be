package com.quiz.project.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserReqDTO {
	private Long id;
	@NotBlank(message = "username is required")
	private String username;
	@NotBlank(message = "password is required")
	private String password;
	@NotBlank(message = "name is required")
	private String name;
	@NotBlank(message = "email is required")
	private String email;
	private String avatar;
}
