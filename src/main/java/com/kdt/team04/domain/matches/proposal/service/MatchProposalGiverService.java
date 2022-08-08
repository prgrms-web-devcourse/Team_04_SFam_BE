package com.kdt.team04.domain.matches.proposal.service;

import java.text.MessageFormat;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kdt.team04.common.exception.BusinessException;
import com.kdt.team04.common.exception.EntityNotFoundException;
import com.kdt.team04.common.exception.ErrorCode;
import com.kdt.team04.domain.matches.match.dto.response.MatchAuthorResponse;
import com.kdt.team04.domain.matches.match.service.MatchGiverService;
import com.kdt.team04.domain.matches.proposal.dto.QueryMatchProposalResponse;
import com.kdt.team04.domain.matches.proposal.dto.QueryMatchProposalSimpleResponse;
import com.kdt.team04.domain.matches.proposal.dto.response.MatchChatViewMatchResponse;
import com.kdt.team04.domain.matches.proposal.dto.response.FixedProposalResponse;
import com.kdt.team04.domain.matches.proposal.entity.MatchProposal;
import com.kdt.team04.domain.matches.proposal.entity.MatchProposalStatus;
import com.kdt.team04.domain.matches.proposal.repository.MatchProposalRepository;
import com.kdt.team04.domain.team.dto.response.TeamSimpleResponse;
import com.kdt.team04.domain.team.entity.Team;
import com.kdt.team04.domain.user.dto.response.AuthorResponse;
import com.kdt.team04.domain.user.dto.response.ChatTargetProfileResponse;
import com.kdt.team04.domain.user.entity.User;

@Service
@Transactional(readOnly = true)
public class MatchProposalGiverService {

	private final MatchProposalRepository matchProposalRepository;
	private final MatchGiverService matchGiver;

	public MatchProposalGiverService(MatchProposalRepository matchProposalRepository, MatchGiverService matchGiver) {
		this.matchProposalRepository = matchProposalRepository;
		this.matchGiver = matchGiver;
	}

	public QueryMatchProposalSimpleResponse findSimpleProposalById(Long id) {
		QueryMatchProposalSimpleResponse matchProposal = matchProposalRepository.findSimpleProposalById(id)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.PROPOSAL_NOT_FOUND,
				MessageFormat.format("matchProposalId = {0}", id)));

		return matchProposal;
	}

	public MatchChatViewMatchResponse findChatMatchByProposalId(Long id, Long userId) {
		MatchProposal matchProposal = matchProposalRepository.findProposalWithUserById(id)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.PROPOSAL_NOT_FOUND,
				MessageFormat.format("matchProposalId = {0}", id)));

		MatchAuthorResponse match
			= matchGiver.findMatchAuthorById(matchProposal.getMatch().getId());

		if (isAuthorOrProposer(match, matchProposal, userId)) {
			throw new BusinessException(ErrorCode.PROPOSAL_ACCESS_DENIED,
				MessageFormat.format("matchId = {0}, proposalId = {1}, userId = {1}", match.id(), id, userId));
		}

		String targetNickname = Objects.equals(match.author().id(), userId) ?
			matchProposal.getUser().getNickname() :
			match.author().nickname();

		return new MatchChatViewMatchResponse(
			match.title(),
			match.status(),
			new ChatTargetProfileResponse(targetNickname)
		);
	}

	private boolean isAuthorOrProposer(MatchAuthorResponse match, MatchProposal proposal, Long userId) {
		return !Objects.equals(proposal.getUser().getId(), userId)
			&& !Objects.equals(match.author().id(), userId);
	}

	public QueryMatchProposalResponse findFixedProposalByMatchId(Long matchId) {
		QueryMatchProposalResponse matchProposal = matchProposalRepository.findFixedProposalByMatchId(matchId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.PROPOSAL_NOT_FOUND,
				MessageFormat.format("matchId = {0}", matchId)));

		return matchProposal;
	}

	@Transactional
	public FixedProposalResponse updateToFixed(Long id) {
		MatchProposal matchProposal = matchProposalRepository.findProposalById(id)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.PROPOSAL_NOT_FOUND,
				MessageFormat.format("matchProposalId = {0}", id)));

		if (matchProposal.getStatus() != MatchProposalStatus.APPROVED) {
			throw new BusinessException(ErrorCode.PROPOSAL_NOT_APPROVED,
				MessageFormat.format("proposerId = {0}, status = {1}", matchProposal.getId(),
					matchProposal.getStatus()));
		}

		matchProposal.updateStatus(MatchProposalStatus.FIXED);

		User user = matchProposal.getUser();
		AuthorResponse userResponse = new AuthorResponse(user.getId(), user.getNickname());

		Team team = matchProposal.getTeam();
		TeamSimpleResponse teamResponse =
			team == null ? null
				: new TeamSimpleResponse(team.getId(), team.getName(),
				team.getSportsCategory(), team.getLogoImageUrl());

		return new FixedProposalResponse(
			matchProposal.getId(),
			userResponse,
			teamResponse
		);
	}
}
