package com.kdt.team04.domain.matches.proposal.repository;

import java.util.List;
import java.util.Optional;

import com.kdt.team04.domain.matches.proposal.dto.MatchProposalQueryDto;
import com.kdt.team04.domain.matches.proposal.dto.MatchProposalSimpleQueryDto;
import com.kdt.team04.domain.matches.proposal.dto.QueryProposalChatResponse;

public interface MatchProposalRepositoryCustom {
	Optional<MatchProposalSimpleQueryDto> findSimpleProposalById(Long id);
	Optional<MatchProposalQueryDto> findFixedProposalByMatchId(Long matchId);
	List<QueryProposalChatResponse> findAllProposalByUserId(Long userId);
}
