package com.quiz.project.dto.resp;

import java.util.List;

import com.quiz.project.entity.MajorEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SubjectRespDTO {
	private Long id;
	private String name;
	private String description;
	private List<MajorRespDTO> major;
}
