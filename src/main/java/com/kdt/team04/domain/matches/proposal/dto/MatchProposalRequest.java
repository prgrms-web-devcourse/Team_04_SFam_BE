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

		@Schema(description = "신청 내용, 2자 이상 30자 이하")
		@NotBlank
		@Size(min = 2, max = 30)
		String content
	) {
	}

	public record ProposalReact(@NotNull MatchProposalStatus status) {}
}
