package com.quiz.project.dto.resp;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class SentenceTestRespDto {
	private String question;
	private List<String> answer;
}
