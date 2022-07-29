package com.kdt.team04.domain.matches.request.service;

import java.text.MessageFormat;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kdt.team04.common.exception.EntityNotFoundException;
import com.kdt.team04.common.exception.ErrorCode;
import com.kdt.team04.domain.matches.request.dto.MatchProposalQueryDto;
import com.kdt.team04.domain.matches.request.repository.MatchProposalRepository;

@Service
@Transactional(readOnly = true)
public class MatchProposalGiverService {

	private final MatchProposalRepository matchProposalRepository;

	public MatchProposalGiverService(MatchProposalRepository matchProposalRepository) {
		this.matchProposalRepository = matchProposalRepository;
	}

	public MatchProposalQueryDto findSimpleProposalById(Long id) {
		MatchProposalQueryDto matchProposal = matchProposalRepository.findSimpleProposalById(id)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.MATCH_PROPOSAL_NOT_FOUND,
				MessageFormat.format("matchProposalId = {0}", id)));

		return matchProposal;
	}
}
