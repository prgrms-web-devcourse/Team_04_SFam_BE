package com.kdt.team04.domain.matches.proposal.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

public record MatchChatResponse(
	@Schema(description = "매칭 공고 정보")
	MatchChatViewMatchResponse match,

	@Schema(description = "채팅 내용들")
	List<ChatItemResponse> chats
) {
}