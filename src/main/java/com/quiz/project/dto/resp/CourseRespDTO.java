package com.quiz.project.dto.resp;

import java.time.Instant;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class CourseRespDTO {
	private Long id;
	private String title;
	private String description;
	private Instant createdDate;
	private MajorRespDTO major;
	private AuthRespDTO.UserLogin user;
	private int countSentence;
}
