package com.kdt.team04.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record AuthRequest() {
	public record SignInRequest(
		@Schema(description = "회원 id", required = true)
		String username,
		@Schema(description = "회원 password", required = true)
		String password) {
	}

	public record SignUpRequest(
		@Schema(description = "회원 id", required = true)
		String username,
		@Schema(description = "회원 password", required = true)
		String password,
		@Schema(description = "회원 nickname", required = true)
		String nickname
	) {
	}
}
