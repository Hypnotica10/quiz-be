package com.quiz.project.dto.resp;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AuthRespDTO {
	
	@JsonProperty("access_token")
	private String accessToken;
	private UserLogin user;

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class UserLogin {
		private Long id;
		private String username;
		private String name;
		private String avatar;
	}
}
