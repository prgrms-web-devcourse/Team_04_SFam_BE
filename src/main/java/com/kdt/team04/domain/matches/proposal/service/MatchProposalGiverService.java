package com.kdt.team04.domain.matches.proposal.service;

import java.text.MessageFormat;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kdt.team04.common.exception.BusinessException;
import com.kdt.team04.common.exception.EntityNotFoundException;
import com.kdt.team04.common.exception.ErrorCode;
import com.kdt.team04.domain.matches.match.dto.MatchResponse;
import com.kdt.team04.domain.matches.match.service.MatchService;
import com.kdt.team04.domain.matches.proposal.dto.MatchProposalQueryDto;
import com.kdt.team04.domain.matches.proposal.dto.MatchProposalResponse;
import com.kdt.team04.domain.matches.proposal.entity.MatchProposal;
import com.kdt.team04.domain.matches.proposal.repository.MatchProposalRepository;
import com.kdt.team04.domain.user.dto.UserResponse;

@Service
@Transactional(readOnly = true)
public class MatchProposalGiverService {

	private final MatchProposalRepository matchProposalRepository;
	private final MatchService matchService;

	public MatchProposalGiverService(MatchProposalRepository matchProposalRepository, MatchService matchService) {
		this.matchProposalRepository = matchProposalRepository;
		this.matchService = matchService;
	}

	public MatchProposalQueryDto findSimpleProposalById(Long id) {
		MatchProposalQueryDto matchProposal = matchProposalRepository.findSimpleProposalById(id)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.MATCH_PROPOSAL_NOT_FOUND,
				MessageFormat.format("matchProposalId = {0}", id)));

		return matchProposal;
	}

	public MatchProposalResponse.ChatMatch findChatMatchByProposalId(Long id, Long userId) {
		MatchProposal matchProposal = matchProposalRepository.findProposalWithUserById(id)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.MATCH_PROPOSAL_NOT_FOUND,
				MessageFormat.format("matchProposalId = {0}", id)));

		MatchResponse.MatchAuthorResponse match
			= matchService.findMatchAuthorById(matchProposal.getMatch().getId());

		if (isAuthorOrProposer(match, matchProposal, userId)) {
			throw new BusinessException(ErrorCode.MATCH_PROPOSAL_ACCESS_DENIED,
				MessageFormat.format("matchId = {0}, proposalId = {1}, userId = {1}", match.id(), id, userId));
		}

		String targetNickname = Objects.equals(match.author().id(), userId) ?
			matchProposal.getUser().getNickname() :
			match.author().nickname();

		return new MatchProposalResponse.ChatMatch(
			match.title(),
			match.status(),
			new UserResponse.ChatTargetProfile(targetNickname)
		);
	}

	private boolean isAuthorOrProposer(MatchResponse.MatchAuthorResponse match, MatchProposal proposal, Long userId) {
		return !Objects.equals(proposal.getUser().getId(), userId)
			&& !Objects.equals(match.author().id(), userId);
	}
}
