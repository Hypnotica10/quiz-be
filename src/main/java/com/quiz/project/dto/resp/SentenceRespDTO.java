package com.quiz.project.dto.resp;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class SentenceRespDTO {
	private Long id;
	private String term;
	private String definition;
	private String image;
}
