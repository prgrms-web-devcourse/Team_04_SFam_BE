package com.kdt.team04.domain.auth.dto;

public record AuthRequest() {
	public record SignInRequest(String username, String password) {
	}

	public record SignUpRequest(
		String username,
		String password,
		String nickname
	) {
	}
}
