package com.kdt.team04.domain.matches.proposal.dto.response;

import com.kdt.team04.domain.user.dto.response.ChatTargetProfileResponse;

import io.swagger.v3.oas.annotations.media.Schema;

public record ProposalChatResponse(
	@Schema(description = "매칭 신청 ID(고유 PK)")
	Long id,

	@Schema(description = "매칭 신청 메시지")
	String content,

	@Schema(description = "채팅 대상")
	ChatTargetProfileResponse target,

	@Schema(description = "마지막 채팅 정보")
	ChatLastResponse lastChat
) {
}
