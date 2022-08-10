package com.kdt.team04.domain.matches.proposal.repository;

import java.util.List;
import java.util.Optional;

import com.kdt.team04.domain.matches.proposal.dto.QueryMatchProposalResponse;
import com.kdt.team04.domain.matches.proposal.dto.QueryMatchProposalSimpleResponse;
import com.kdt.team04.domain.matches.proposal.dto.QueryProposalChatResponse;

public interface CustomMatchProposalRepository {
	Optional<QueryMatchProposalSimpleResponse> findSimpleProposalById(Long id);
	Optional<QueryMatchProposalResponse> findFixedProposalByMatchId(Long matchId);
	List<QueryProposalChatResponse> findAllProposalByUserId(Long userId);
	boolean existsByMatchIdAndUserId(Long matchId, Long userId);
}
