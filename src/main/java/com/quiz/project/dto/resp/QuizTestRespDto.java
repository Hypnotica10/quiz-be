package com.quiz.project.dto.resp;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class QuizTestRespDto {
	private Long questionNumber;
	private String title;
	private List<SentenceTestRespDto> listSentence;
	private Map<String, String> solution;
}
