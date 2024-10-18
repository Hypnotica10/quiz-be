package com.quiz.project.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.quiz.project.config.security.SecurityUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// interceptor duoc check truoc khi chay vao controller
public class PermissionInterceptor implements HandlerInterceptor {

	@Autowired
	private SecurityUtil securityUtil;

	@SuppressWarnings("unused")
	@Override
	@Transactional
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		String path = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
		String requestURI = request.getRequestURI();
		String httpMethod = request.getMethod();
//		System.out.println(">>> RUN preHandle");
//		System.out.println(">>> path= " + path);
//		System.out.println(">>> httpMethod= " + httpMethod);
//		System.out.println(">>> requestURI= " + requestURI);

	
		// check NoResourceFoundException
		boolean checkIsExists = false;
		String[] pathAccess = { "/course", "/sentence", "/major", "/subject", "/auth", "/users", "/quiz-test" };
		for (String item : pathAccess) {
			checkIsExists = path.startsWith(item);
			if (checkIsExists) {
				break;
			}
		}

		if (!checkIsExists) {
			throw new NoResourceFoundException(HttpMethod.GET, "");
		}

		String username = this.securityUtil.getCurrentUserLogin().isPresent()
				? this.securityUtil.getCurrentUserLogin().get()
				: "";
		if (username != null && !username.isEmpty()) {
			if (!username.equals("duytaan")) {
				throw new AccessDeniedException("Access denied");
			}
		}
		return true;
	}
}
