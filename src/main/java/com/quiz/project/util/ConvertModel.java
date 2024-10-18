package com.quiz.project.util;

import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

public class ConvertModel {
	private static ConvertModel convertModel;

	private ConvertModel() {
	}

	public static ConvertModel getInstance() {
		if (convertModel == null) {
			synchronized (ConvertModel.class) {
				if (convertModel == null) {
					convertModel = new ConvertModel();
				}
			}
		}
		return convertModel;
	}

	public static Object getModelMapping(Object source, Object destination) {
		BeanUtils.copyProperties(source, destination, getNullProperties(source));
		return destination;
	}

	public static String[] getNullProperties(Object src) {
		final BeanWrapper wraper = new BeanWrapperImpl(src);
//		return Stream.of(wraper.getPropertyDescriptors()).map(FeatureDescriptor::getName).filter(property -> {
//			return property != "class" && wraper.getPropertyValue(property) == null;
//		}).toArray(String[]::new);
		PropertyDescriptor[] pds = wraper.getPropertyDescriptors();
		Set<String> fieldNames = new HashSet<String>();
		for (PropertyDescriptor pd : pds) {
			Object srcValue = wraper.getPropertyValue(pd.getName());
			if (srcValue == null) {
				fieldNames.add(pd.getName());
			}
		}
		String[] result = new String[fieldNames.size()];
		return fieldNames.toArray(result);
	}

}
