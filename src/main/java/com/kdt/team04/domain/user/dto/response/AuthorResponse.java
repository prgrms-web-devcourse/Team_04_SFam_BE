package com.kdt.team04.domain.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record AuthorResponse(
	@Schema(description = "회원 ID(고유 PK)")
	Long id,
	@Schema(description = "회원 닉네임")
	String nickname) {
}
