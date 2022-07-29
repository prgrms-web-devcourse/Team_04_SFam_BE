package com.kdt.team04.domain.matches.request.repository;

import java.util.Optional;

import com.kdt.team04.domain.matches.request.dto.MatchProposalQueryDto;

public interface MatchProposalRepositoryCustom {
	Optional<MatchProposalQueryDto> findSimpleProposalById(Long id);
}
