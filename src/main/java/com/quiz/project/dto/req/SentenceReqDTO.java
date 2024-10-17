package com.quiz.project.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class SentenceReqDTO {
	private Long id;
	@NotBlank(message = "term is required")
	private String term;
	@NotBlank(message = "definition is required")
	private String definition;
	private String image;
}
