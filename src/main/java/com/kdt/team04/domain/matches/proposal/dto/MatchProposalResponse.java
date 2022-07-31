package com.kdt.team04.domain.matches.proposal.dto;

import com.kdt.team04.domain.team.dto.TeamResponse;
import com.kdt.team04.domain.user.dto.UserResponse;

import io.swagger.v3.oas.annotations.media.Schema;

public record MatchProposalResponse() {

	public record FixedProposal(
		Long id,
		UserResponse.AuthorResponse proposer,
		TeamResponse.SimpleResponse proposerTeam
	) {}

	public record Chat(
		@Schema(description = "매칭 신청 아이디")
		Long id,

		@Schema(description = "매칭 신청 메시지")
		String content,

		@Schema(description = "채팅 대상")
		UserResponse.ChatTargetProfile target,

		@Schema(description = "마지막 채팅 정보")
		MatchChatResponse.LastChat lastChat
	) {}
}
