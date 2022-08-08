package com.kdt.team04.domain.matches.proposal.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

public record ChattingResponse(
	@Schema(description = "매칭 공고 정보")
	ProposalChatMatchResponse match,

	@Schema(description = "채팅 내용들")
	List<ChatResponse> chats
) {
}