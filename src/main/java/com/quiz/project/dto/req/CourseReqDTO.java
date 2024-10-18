package com.quiz.project.dto.req;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class CourseReqDTO {
	private Long id;
	@NotBlank(message = "title is required")
	private String title;
	private String description;
	@NotNull(message = "major is required")
	private Long majorId;
	@NotNull(message = "user is required")
	private Long userId;
	private List<SentenceReqDTO> sentences;
}
