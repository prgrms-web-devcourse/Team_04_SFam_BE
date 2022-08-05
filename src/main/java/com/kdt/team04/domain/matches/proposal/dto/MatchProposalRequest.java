package com.kdt.team04.domain.matches.proposal.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.kdt.team04.domain.matches.proposal.entity.MatchProposalStatus;

import io.swagger.v3.oas.annotations.media.Schema;

public record MatchProposalRequest() {
	public record ProposalCreate(
		@Schema(description = "신청자 팀 ID")
		Long teamId,

		@Schema(description = "신청 내용, 2자 이상 30자 이하", required = true)
		@NotBlank
		@Size(min = 2, max = 30)
		String content
	) {
	}

	public record ProposalReact(
		@Schema(description = "매칭 신청 상태(값/설명) - WAITING/대기중, APPROVED/수락, REFUSE/거절, FIXED/대상확정(경기종료)", required = true)
		@NotNull MatchProposalStatus status
	) {}
}
