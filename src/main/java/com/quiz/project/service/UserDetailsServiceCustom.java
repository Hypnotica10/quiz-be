 package com.quiz.project.service;

import java.util.Collections;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.quiz.project.entity.UserEntity;

@Component("userDetailsService")
public class UserDetailsServiceCustom implements UserDetailsService {
	private final UserService userService;

	public UserDetailsServiceCustom(UserService userService) {
		this.userService = userService;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserEntity userEntity = this.userService.getUserByUsername(username);
		if (userEntity == null) {
			throw new UsernameNotFoundException("Incorrect username or password");
		}
		return new User(userEntity.getUsername(), userEntity.getPassword(),
				Collections.singletonList(new SimpleGrantedAuthority("user")));
	}

}
