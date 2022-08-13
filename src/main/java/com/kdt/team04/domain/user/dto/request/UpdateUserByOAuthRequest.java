package com.kdt.team04.domain.user.dto.request;

public record UpdateUserByOAuthRequest(
	String nickname,
	String email,
	String profileImageUrl
) {
}
