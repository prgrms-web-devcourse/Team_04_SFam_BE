package com.kdt.team04.domain.user.controller;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kdt.team04.common.ApiResponse;
import com.kdt.team04.common.exception.NotAuthenticationException;
import com.kdt.team04.common.security.jwt.JwtAuthentication;
import com.kdt.team04.domain.user.dto.UserRequest;
import com.kdt.team04.domain.user.dto.UserResponse;
import com.kdt.team04.domain.user.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "회원 API")
@Validated
@RestController
@RequestMapping("/api/users")
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@Operation(summary = "회원 프로필 조회", description = "회원 프로필을 닉네임을 통해 조회할 수 있다.")
	@GetMapping
	public ApiResponse<List<UserResponse.UserFindResponse>> findUsers(
		@RequestParam(required = false) @NotBlank(message = "닉네임은 필수입니다.") String nickname
	) {
		List<UserResponse.UserFindResponse> foundUsers = userService.findAllByNickname(nickname);

		return new ApiResponse<>(foundUsers);
	}

	@Operation(summary = "회원 프로필 조회", description = "회원 프로필을 조회할 수 있다.")
	@GetMapping("/{id}")
	public ApiResponse<UserResponse.FindProfile> findProfile(@PathVariable Long id) {
		UserResponse.FindProfile user = userService.findProfileById(id);

		return new ApiResponse<>(user);
	}

	@Operation(summary = "회원 위치 정보 업데이트", description = "회원 위치 정보(위도, 경도)를 업데이트 한다.")
	@PutMapping("/{id}/location")
	public ApiResponse<UserResponse.UpdateLocationResponse> update(
		@AuthenticationPrincipal JwtAuthentication auth,
		@RequestBody @Valid @NotNull UserRequest.UpdateLocationRequest request) {
		if (auth == null) {
			throw new NotAuthenticationException("not authenticated");
		}

		UserResponse.UpdateLocationResponse response = userService.updateLocation(auth.id(), request);

		return new ApiResponse<>(response);
	}

	@GetMapping("/nickname/duplication")
	public ApiResponse<Boolean> nicknameDuplicationCheck(@RequestParam String input) {
		return new ApiResponse<>(userService.nicknameDuplicationCheck(input));
	}

	@GetMapping("/username/duplication")
	public ApiResponse<Boolean> usernameDuplicationCheck(@RequestParam String input) {
		return new ApiResponse<>(userService.usernameDuplicationCheck(input));
	}
}
