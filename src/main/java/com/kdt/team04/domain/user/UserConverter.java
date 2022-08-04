package com.kdt.team04.domain.user;

import org.springframework.stereotype.Component;

import com.kdt.team04.domain.user.dto.UserResponse;
import com.kdt.team04.domain.user.entity.User;

@Component
public class UserConverter {

	public User toUser(UserResponse userResponse) {
		return User.builder()
			.id(userResponse.id())
			.username(userResponse.username())
			.nickname(userResponse.nickname())
			.location(userResponse.location())
			.build();
	}

	public UserResponse toUserResponse(User user) {
		return UserResponse.builder()
			.id(user.getId())
			.username(user.getUsername())
			.nickname(user.getNickname())
			.location(user.getLocation())
			.profileImageUrl(user.getProfileImageUrl())
			.build();
	}
}
