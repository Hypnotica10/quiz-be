package com.quiz.project.dto.resp;

import java.time.Instant;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserRespDTO {
	private Long id;
	private String username;
	private String email;
	private String name;
	private String avatar;
	private Instant updateDate;
	private Instant createdDate;
}
