package com.kdt.team04.domain.user;

import org.springframework.stereotype.Component;

import com.kdt.team04.domain.user.dto.request.CreateUserRequest;
import com.kdt.team04.domain.user.dto.response.UserResponse;
import com.kdt.team04.domain.user.entity.User;

@Component
public class UserConverter {

	public User toUser(UserResponse userResponse) {
		return User.builder()
			.id(userResponse.id())
			.username(userResponse.username())
			.nickname(userResponse.nickname())
			.userSettings(userResponse.userSettings())
			.build();
	}

	public User toUser(CreateUserRequest userCreateRequest) {
		return User.builder()
			.username(userCreateRequest.username())
			.password(userCreateRequest.password())
			.nickname(userCreateRequest.nickname())
			.email(userCreateRequest.email())
			.profileImageUrl(userCreateRequest.profileImageUrl())
			.role(userCreateRequest.role())
			.build();
	}

	public UserResponse toUserResponse(User user) {
		return UserResponse.builder()
			.id(user.getId())
			.username(user.getUsername())
			.nickname(user.getNickname())
			.password(user.getPassword())
			.userSettings(user.getUserSettings())
			.email(user.getEmail())
			.profileImageUrl(user.getProfileImageUrl())
			.role(user.getRole())
			.build();
	}
}
