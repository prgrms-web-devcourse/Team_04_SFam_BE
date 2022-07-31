package com.kdt.team04.domain.matches.proposal.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record MatchChatResponse() {

	public record LastChat(
		@Schema(description = "마지막 채팅 내용")
		String content
	) {}
}
