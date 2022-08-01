package com.kdt.team04.domain.matches.proposal.service;

import java.text.MessageFormat;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kdt.team04.common.exception.EntityNotFoundException;
import com.kdt.team04.common.exception.ErrorCode;
import com.kdt.team04.domain.matches.proposal.dto.MatchProposalSimpleQueryDto;
import com.kdt.team04.domain.matches.proposal.repository.MatchProposalRepository;

@Service
@Transactional(readOnly = true)
public class MatchProposalGiverService {

	private final MatchProposalRepository matchProposalRepository;

	public MatchProposalGiverService(MatchProposalRepository matchProposalRepository) {
		this.matchProposalRepository = matchProposalRepository;
	}

	public MatchProposalSimpleQueryDto findSimpleProposalById(Long id) {
		MatchProposalSimpleQueryDto matchProposal = matchProposalRepository.findSimpleProposalById(id)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.MATCH_PROPOSAL_NOT_FOUND,
				MessageFormat.format("matchProposalId = {0}", id)));

		return matchProposal;
	}
}
