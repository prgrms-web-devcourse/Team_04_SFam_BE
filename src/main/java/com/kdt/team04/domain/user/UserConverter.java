package com.kdt.team04.domain.user;

import org.springframework.stereotype.Component;

import com.kdt.team04.domain.user.dto.UserResponse;
import com.kdt.team04.domain.user.entity.User;

@Component
public class UserConverter {

	public User toUser(UserResponse userResponse) {
		return new User(userResponse.id(), userResponse.password(), userResponse.username(), userResponse.nickname(),
			userResponse.location());
	}

	public UserResponse toUserResponse(User user) {
		return new UserResponse(user.getId(), user.getUsername(), user.getPassword(), user.getNickname(),
			user.getLocation());
	}
}
