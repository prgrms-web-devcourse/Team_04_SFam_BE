package com.kdt.team04.domain.auth.dto;

public record SignOutResponse(
	String accessTokenHeader,
	String refreshTokenHeader
) {
}
