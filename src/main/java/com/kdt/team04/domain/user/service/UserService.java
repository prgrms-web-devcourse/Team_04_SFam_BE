package com.kdt.team04.domain.user.service;

import com.kdt.team04.domain.user.dto.UserRequest;
import com.kdt.team04.domain.user.dto.UserResponse;

public interface UserService {
	UserResponse findByUsername(String username);

	Long create(UserRequest.CreateRequest request);
}
