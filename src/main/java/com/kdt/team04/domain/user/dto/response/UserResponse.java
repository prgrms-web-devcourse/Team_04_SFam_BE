package com.kdt.team04.domain.user.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kdt.team04.domain.user.Role;
import com.kdt.team04.domain.user.entity.Location;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record UserResponse(
	@Schema(description = "회원 ID(고유 PK)")
	Long id,

	@Schema(description = "회원 아이디")
	String username,

	@JsonIgnore
	@Schema(description = "회원 비밀번호")
	String password,

	@Schema(description = "회원 닉네임")
	String nickname,

	@Schema(description = "회원 위치 정보")
	Location location,

	@Schema(description = "회원 이메일")
	String email,

	@Schema(description = "회원 프로필 url")
	String profileImageUrl,

	@Schema(description = "회원 권한")
	Role role
) {
}

