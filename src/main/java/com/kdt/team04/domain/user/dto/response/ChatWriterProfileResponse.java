package com.kdt.team04.domain.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record ChatWriterProfileResponse(
	@Schema(description = "채팅 작성자 ID(고유 PK)")
	Long id
) {
}
