package com.kdt.team04.domain.user.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kdt.team04.common.ApiResponse;
import com.kdt.team04.domain.user.dto.UserResponse;
import com.kdt.team04.domain.user.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "회원 API")
@RestController
@RequestMapping("/api/users")
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@Operation(summary = "회원 프로필 조회", description = "회원 프로필을 조회할 수 있다.")
	@GetMapping("/{id}")
	public ApiResponse<UserResponse.FindProfile> findProfile(@PathVariable Long id) {
		UserResponse.FindProfile user = userService.findProfileById(id);

		return new ApiResponse<>(user);
	}
}
