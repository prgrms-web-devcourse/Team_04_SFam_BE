package com.kdt.team04.domain.auth.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kdt.team04.common.security.jwt.JwtAuthenticationToken;

public record AuthResponse() {
	public record SignInResponse(
		Long id,
		String username,
		@JsonIgnore
		TokenDto accessToken,
		@JsonIgnore
		TokenDto refreshToken,
		@JsonIgnore
		JwtAuthenticationToken jwtAuthenticationToken
	) {
	}

	public record SignUpResponse(Long id) {

	}
}
