package com.kdt.team04.domain.matches.proposal.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record ChatLastResponse(
	@Schema(description = "마지막 채팅 내용")
	String content
) {
}
