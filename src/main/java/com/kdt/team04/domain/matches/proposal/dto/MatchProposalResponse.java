package com.kdt.team04.domain.matches.proposal.dto;

import com.kdt.team04.domain.team.dto.TeamResponse;
import com.kdt.team04.domain.matches.match.entity.MatchStatus;
import com.kdt.team04.domain.user.dto.UserResponse;

import io.swagger.v3.oas.annotations.media.Schema;

public record MatchProposalResponse() {

	public record FixedProposal(
		Long id,
		UserResponse.AuthorResponse proposer,
		TeamResponse.SimpleResponse proposerTeam
	) {}

	public record Chat(
		@Schema(description = "매칭 신청 ID(고유 PK)")
		Long id,

		@Schema(description = "매칭 신청 메시지")
		String content,

		@Schema(description = "채팅 대상")
		UserResponse.ChatTargetProfile target,

		@Schema(description = "마지막 채팅 정보")
		MatchChatResponse.LastChat lastChat
	) {}

	public record ChatMatch(
		@Schema(description = "매칭 공고 제목")
		String title,

		@Schema(description = "매칭 상태(값/설명) - WAITING/대기중, IN_GAME/모집완료, END/경기종료")
		MatchStatus status,

		@Schema(description = "채팅 상대 정보")
		UserResponse.ChatTargetProfile targetProfile
	) {}

	public record SimpleProposal(
		Long id
	) {}
}
