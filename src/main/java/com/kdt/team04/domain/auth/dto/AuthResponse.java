package com.kdt.team04.domain.auth.dto;

import com.kdt.team04.common.security.jwt.JwtAuthenticationToken;

public record AuthResponse() {
	public record SignInResponse(
		Long id,
		String username,
		TokenDto accessToken,
		TokenDto refreshToken,
		JwtAuthenticationToken jwtAuthenticationToken
	) {
	}

	public record SignUpResponse(Long id) {

	}
}
