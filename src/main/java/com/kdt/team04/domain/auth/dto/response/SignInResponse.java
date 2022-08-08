package com.kdt.team04.domain.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kdt.team04.common.security.jwt.JwtAuthenticationToken;
import com.kdt.team04.domain.auth.dto.JwtToken;
import com.kdt.team04.domain.user.Role;

import io.swagger.v3.oas.annotations.media.Schema;

public record SignInResponse(
	@Schema(description = "회원 ID(고유 PK)")
	Long id,

	@Schema(description = "회원 아이디")
	String username,

	@Schema(description = "회원 닉네임")
	String nickname,

	@Schema(description = "회원 이메일")
	String email,

	@Schema(description = "회원 프로필 이미지 URL")
	String profileImageUrl,

	@Schema(description = "회원 권한")
	Role role,

	@JsonIgnore
	JwtToken accessToken,

	@JsonIgnore
	JwtToken refreshToken,

	@JsonIgnore
	JwtAuthenticationToken jwtAuthenticationToken
) {
}