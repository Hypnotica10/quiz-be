package com.quiz.project.config;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.quiz.project.dto.resp.AuthRespDTO;

@Configuration
public class JwtConfig {

	@Value("${jwt.signer-key}")
	private String jwtKey;

	@Value("${jwt.access-token.expiration-time-seconds}")
	private long expirationTimeToken;

	@Value("${jwt.refresh-token.expiration-time-seconds}")
	private long expirationTimeRefreshToken;

	public String generateToken(String username, AuthRespDTO.UserLogin dto) {
		Instant now = Instant.now();
		Long expired = now.plus(expirationTimeToken, ChronoUnit.SECONDS).toEpochMilli();

//		List<String> listAuthority = new ArrayList<String>();
//		listAuthority.add("admin");
//		listAuthority.add("user");

		JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
		JWTClaimsSet claimsSet = null;
		if (username.equals("duytaan")) {
			claimsSet = new JWTClaimsSet.Builder().subject(username).issueTime(new Date())
					.expirationTime(new Date(expired)).claim("user", dto).claim("permission", "admin").build();
		} else {
			claimsSet = new JWTClaimsSet.Builder().subject(username).issueTime(new Date())
					.expirationTime(new Date(expired)).claim("user", dto).claim("permission", "user").build();
		}

		Payload payload = new Payload(claimsSet.toJSONObject());
		JWSObject jwsObject = new JWSObject(header, payload);
		try {
			jwsObject.sign(new MACSigner(jwtKey));
		} catch (JOSEException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jwsObject.serialize();
	}

	public String generateRefreshToken(String username, AuthRespDTO dto) {
		Instant now = Instant.now();
		Long expired = now.plus(expirationTimeRefreshToken, ChronoUnit.SECONDS).toEpochMilli();
		JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
		JWTClaimsSet claimsSet = new JWTClaimsSet.Builder().subject(username).issueTime(new Date())
				.expirationTime(new Date(expired)).claim("user", dto.getUser()).build();
		Payload payload = new Payload(claimsSet.toJSONObject());
		JWSObject jwsObject = new JWSObject(header, payload);
		try {
			jwsObject.sign(new MACSigner(jwtKey));
		} catch (JOSEException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jwsObject.serialize();
	}

	@Bean
	public JwtDecoder jwtDecoder() {
		SecretKeySpec keySpec = new SecretKeySpec(jwtKey.getBytes(), "HS512");
		NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withSecretKey(keySpec).macAlgorithm(MacAlgorithm.HS512).build();
		return token -> {
			try {
				return jwtDecoder.decode(token);
			} catch (Exception e) {
				throw e;
			}
		};
	}

	public Jwt checkValidRefreshToken(String token) {
		SecretKeySpec keySpec = new SecretKeySpec(jwtKey.getBytes(), "HS512");
		NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withSecretKey(keySpec).macAlgorithm(MacAlgorithm.HS512).build();
		try {
			return jwtDecoder.decode(token);
		} catch (Exception e) {
			throw e;
		}
	}

//	jwtConfig -> jwtConfig.decoder(jwtDecoder())
}
