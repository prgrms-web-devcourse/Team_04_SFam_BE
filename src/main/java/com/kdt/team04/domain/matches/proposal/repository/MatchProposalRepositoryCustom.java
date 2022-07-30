package com.kdt.team04.domain.matches.proposal.repository;

import java.util.Optional;

import com.kdt.team04.domain.matches.proposal.dto.MatchProposalQueryDto;

public interface MatchProposalRepositoryCustom {
	Optional<MatchProposalQueryDto> findSimpleProposalById(Long id);
}
