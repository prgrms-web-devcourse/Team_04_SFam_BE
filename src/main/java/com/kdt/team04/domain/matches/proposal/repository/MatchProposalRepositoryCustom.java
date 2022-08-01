package com.kdt.team04.domain.matches.proposal.repository;

import java.util.Optional;

import com.kdt.team04.domain.matches.proposal.dto.MatchProposalSimpleQueryDto;

public interface MatchProposalRepositoryCustom {
	Optional<MatchProposalSimpleQueryDto> findSimpleProposalById(Long id);
}
