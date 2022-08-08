package com.kdt.team04.domain.matches.proposal.repository;

import java.util.Optional;

import com.kdt.team04.domain.matches.proposal.dto.QueryMatchProposalResponse;
import com.kdt.team04.domain.matches.proposal.dto.QueryMatchProposalSimpleResponse;

public interface MatchProposalRepositoryCustom {
	Optional<QueryMatchProposalSimpleResponse> findSimpleProposalById(Long id);
	Optional<QueryMatchProposalResponse> findFixedProposalByMatchId(Long matchId);
}
