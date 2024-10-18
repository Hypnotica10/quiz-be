package com.quiz.project.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.quiz.project.config.CorsConfig;
import com.quiz.project.config.CustomAuthenticationEntryPoint;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
//	neu khong ghi de UserDetailsService bang annotation @Component("userDetailsService") se dung cach nay de nap vaoa
//	@Bean 
//	public AuthenticationManager authenticationManager(HttpSecurity httpSecurity) throws Exception {
//		AuthenticationManagerBuilder authenticationManagerBuilder = httpSecurity.getSharedObject(AuthenticationManagerBuilder.class);
//		authenticationManagerBuilder.userDetailsService(customUserDetailService);
//		return authenticationManagerBuilder.build();
//	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(10);
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http,
			CustomAuthenticationEntryPoint customAuthenticationEntryPoint, CorsConfig corsConfig) throws Exception {
		http.authorizeHttpRequests(authz -> authz.requestMatchers("/users/**", "/auth/signin", "/auth/refresh")
//				.hasAuthority(null) // su dung voi quyen truy cap mac dinh cua spring security
				.permitAll().requestMatchers(HttpMethod.GET, "/subject").permitAll().anyRequest().authenticated())
				.csrf(csrf -> csrf.disable()).cors(cors -> cors.configurationSource(corsConfig.corsConfiguration()))
				.formLogin(f -> f.disable())
				.oauth2ResourceServer((oauth2) -> oauth2.jwt(Customizer.withDefaults())
						.authenticationEntryPoint(customAuthenticationEntryPoint))
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		return http.build();
	}
	// check quyen han truy cap tu jwt khi su dung spring security
//	@Bean
//	public JwtAuthenticationConverter jwtAuthenticationConverter() {
//		JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
//		grantedAuthoritiesConverter.setAuthorityPrefix("");
//		grantedAuthoritiesConverter.setAuthoritiesClaimName("permission");
//		JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
//
//		jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
//		return jwtAuthenticationConverter;
//	}

//	.exceptionHandling(
//	exceptions -> exceptions.authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint()) // 401
//			.accessDeniedHandler(new BearerTokenAccessDeniedHandler())) // 403
}
