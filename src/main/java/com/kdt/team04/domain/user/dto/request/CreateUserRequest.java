package com.kdt.team04.domain.user.dto.request;

import com.kdt.team04.domain.user.Role;

public record CreateUserRequest(
	String username,
	String password,
	String nickname,
	String email,
	String profileImageUrl,
	Role role) {

}