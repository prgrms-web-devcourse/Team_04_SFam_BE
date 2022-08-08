package com.kdt.team04.domain.matches.proposal.dto.response;

import com.kdt.team04.domain.matches.proposal.entity.MatchProposalStatus;

public record ProposalSimpleResponse(
	Long id,
	MatchProposalStatus status,
	String content
) {
}
