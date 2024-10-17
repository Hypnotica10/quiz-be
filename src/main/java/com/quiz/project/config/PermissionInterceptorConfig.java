package com.quiz.project.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class PermissionInterceptorConfig implements WebMvcConfigurer {
	@Bean
	PermissionInterceptor getPermissionInterceptor() {
		return new PermissionInterceptor();
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		String[] whiteList = { "/", "/auth/**", "/users/**", "/course/**", "/sentence/**", "/subject/all", "/major/all", "/quiz-test" };
		registry.addInterceptor(getPermissionInterceptor()).excludePathPatterns(whiteList);
	}
}
