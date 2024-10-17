package com.quiz.project.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class MajorReqDTO {
	private Long id;
	@NotBlank(message = "name is required")
	private String name;
	private String description;
	@NotNull(message = "subject is required")
	private Long subjectId;
}
