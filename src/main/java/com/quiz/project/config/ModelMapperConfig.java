package com.quiz.project.config;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

public class ModelMapperConfig {

	private static final ModelMapper modelMapper;

	static {
		modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		;
	}

	private ModelMapperConfig() {
	}

	public static <D, T> D map(final T entity, Class<D> outClass) {
		return modelMapper.map(entity, outClass);
	}

	public static <D, T> List<D> mapAll(final Collection<T> entityList, Class<D> outCLass) {
		return entityList.stream().map(entity -> modelMapper.map(entity, outCLass)).collect(Collectors.toList());
	}

	public static <S, D> D map(final S source, D destination) {
		modelMapper.map(source, destination);
		return destination;
	}
}
