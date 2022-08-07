package com.kdt.team04.domain.matches.proposal.dto.request;

import javax.validation.constraints.NotNull;

import com.kdt.team04.domain.matches.proposal.entity.MatchProposalStatus;

import io.swagger.v3.oas.annotations.media.Schema;

public record ProposalReactRequest(
	@Schema(description = "매칭 신청 상태(값/설명) - WAITING/대기중, APPROVED/수락, REFUSE/거절, FIXED/대상확정(경기종료)", required = true)
	@NotNull MatchProposalStatus status
) {}