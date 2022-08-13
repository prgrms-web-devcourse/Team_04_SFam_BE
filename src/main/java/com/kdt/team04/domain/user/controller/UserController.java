package com.kdt.team04.domain.user.controller;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.net.MediaType;
import com.kdt.team04.common.ApiResponse;
import com.kdt.team04.common.config.resolver.AuthUser;
import com.kdt.team04.common.exception.BusinessException;
import com.kdt.team04.common.exception.ErrorCode;
import com.kdt.team04.common.security.jwt.JwtAuthentication;
import com.kdt.team04.domain.user.dto.UpdateUserRequest;
import com.kdt.team04.domain.user.dto.request.UpdateUserSettingsRequest;
import com.kdt.team04.domain.user.dto.response.FindProfileResponse;
import com.kdt.team04.domain.user.dto.response.UpdateUserSettingsResponse;
import com.kdt.team04.domain.user.dto.response.UserFindResponse;
import com.kdt.team04.domain.user.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

	@Operation(summary = "회원 프로필 조회", description = "회원 프로필을 닉네임을 통해 조회한다.")
	@GetMapping
	public ApiResponse<List<UserFindResponse>> findUsers(
		@Parameter(description = "회원 닉네임") @RequestParam(required = false) @NotBlank(message = "닉네임은 필수입니다.") String nickname
	) {
		List<UserFindResponse> foundUsers = userService.findAllByNickname(nickname);

		return new ApiResponse<>(foundUsers);
	}

	@Operation(summary = "회원 프로필 조회", description = "회원 프로필을 조회한다.")
	@GetMapping("/{id}")
	public ApiResponse<FindProfileResponse> findProfile(@Parameter(description = "회원 ID") @PathVariable Long id) {
		FindProfileResponse user = userService.findProfileById(id);

		return new ApiResponse<>(user);
	}

	@Operation(summary = "회원 위치 정보 업데이트", description = "회원 위치 정보(위도, 경도)를 업데이트 한다.")
	@PutMapping("/location")
	public ApiResponse<UpdateUserSettingsResponse> updateSettings(
		@AuthUser JwtAuthentication auth,
		@RequestBody @Valid @NotNull UpdateUserSettingsRequest request
	) {
		UpdateUserSettingsResponse response = userService.updateSettings(auth.id(), request);

		return new ApiResponse<>(response);
	}

	@Operation(summary = "회원 닉네임 중복 조회", description = "회원 닉네임 중복 여부를 조회한다.")
	@GetMapping("/nickname/duplication")
	public ApiResponse<Boolean> nicknameDuplicationCheck(
		@Parameter(description = "회원 닉네임") @RequestParam String input) {
		return new ApiResponse<>(userService.nicknameDuplicationCheck(input));
	}

	@Operation(summary = "회원 아이디 중복 조회", description = "회원 아이디 중복 여부를 조회한다.")
	@GetMapping("/username/duplication")
	public ApiResponse<Boolean> usernameDuplicationCheck(
		@Parameter(description = "회원 아이디") @RequestParam String input) {
		return new ApiResponse<>(userService.usernameDuplicationCheck(input));
	}

	@Operation(summary = "회원 프로필 이미지 업데이트", description = "파일을 받아 사용자 프로필 이미지를 업데이트 한다.")
	@PatchMapping("/profile")
	public String uploadProfile(@AuthUser JwtAuthentication auth, MultipartFile file) {
		if (file.isEmpty() || !file.getContentType().startsWith(MediaType.ANY_IMAGE_TYPE.type())) {
			throw new BusinessException(ErrorCode.INVALID_FILE_TYPE,
				"파일이 첨부되지 않았거나 지원하지 않는 타입입니다.");
		}

		return userService.uploadProfile(auth.id(), file);
	}

	@Operation(summary = "회원 정보 수정", description = "회원 정보를 수정한다.")
	@PatchMapping
	public void update(@AuthUser JwtAuthentication auth, @RequestBody UpdateUserRequest request) {
		userService.update(auth.id(), request);
	}

}
