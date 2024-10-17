package com.quiz.project.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Response<T> {
	private int statusCode;
	private String error;
	private Object message;
	private T data;
}
