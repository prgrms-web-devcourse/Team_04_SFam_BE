package com.kdt.team04.common.security.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConstructorBinding
@ConfigurationProperties(prefix = "jwt")
public record JwtConfig(
	TokenProperties accessToken,
	TokenProperties refreshToken,
	String issuer,
	String clientSecret
) {
	public JwtConfig(
		TokenProperties accessToken,
		TokenProperties refreshToken,
		String issuer,
		String clientSecret
	) {
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		this.issuer = issuer;
		this.clientSecret = clientSecret;
	}

	public record TokenProperties(String header, int expirySeconds) {
	}
}
