package com.kdt.team04.domain.user.dto.request;

public record UserUpdateRequest(
	String nickname,
	String email,
	String profileImageUrl
) {
}
