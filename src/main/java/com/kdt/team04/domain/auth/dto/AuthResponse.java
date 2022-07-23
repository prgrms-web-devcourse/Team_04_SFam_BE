package com.kdt.team04.domain.auth.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kdt.team04.common.security.jwt.JwtAuthenticationToken;

import io.swagger.v3.oas.annotations.media.Schema;

public record AuthResponse() {
	public record SignInResponse(
		@Schema(description = "회원 고유 PK")
		Long id,
		@Schema(description = "회원 id")
		String username,
		@JsonIgnore
		TokenDto accessToken,
		@JsonIgnore
		TokenDto refreshToken,
		@JsonIgnore
		JwtAuthenticationToken jwtAuthenticationToken
	) {
	}

	public record SignUpResponse(
		@Schema(description = "회원 고유 PK")
		Long id
	) {

	}
}
