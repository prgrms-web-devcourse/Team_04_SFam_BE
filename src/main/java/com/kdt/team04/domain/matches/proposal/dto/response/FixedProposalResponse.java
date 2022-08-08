package com.kdt.team04.domain.matches.proposal.dto.response;

import com.kdt.team04.domain.teams.team.dto.response.TeamSimpleResponse;
import com.kdt.team04.domain.user.dto.response.AuthorResponse;

public record FixedProposalResponse(
	Long id,
	AuthorResponse proposer,
	TeamSimpleResponse proposerTeam
) {
}