package com.quiz.project.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class SubjectReqDTO {
	private Long id;
	@NotBlank(message = "name is required")
	private String name;
	private String description;
}
