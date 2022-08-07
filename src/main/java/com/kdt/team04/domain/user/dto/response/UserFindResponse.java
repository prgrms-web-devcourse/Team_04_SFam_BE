package com.kdt.team04.domain.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record UserFindResponse(
	@Schema(description = "회원 ID(고유 PK)")
	Long id,
	@Schema(description = "회원 아이디")
	String username,
	@Schema(description = "회원 닉네임")
	String nickname,
	@Schema(description = "회원 프로필 url")
	String profileImageUrl
) {
}