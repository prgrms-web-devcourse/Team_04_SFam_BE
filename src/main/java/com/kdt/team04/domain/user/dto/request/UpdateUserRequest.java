package com.kdt.team04.domain.user.dto.request;

public record UpdateUserRequest(
	String nickname,
	String email,
	String profileImageUrl
) {
}
