package com.kdt.team04.domain.matches.proposal.service;

import java.text.MessageFormat;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kdt.team04.common.exception.BusinessException;
import com.kdt.team04.common.exception.ErrorCode;
import com.kdt.team04.domain.matches.match.dto.MatchConverter;
import com.kdt.team04.domain.matches.match.dto.MatchResponse;
import com.kdt.team04.domain.matches.match.entity.Match;
import com.kdt.team04.domain.matches.match.entity.MatchType;
import com.kdt.team04.domain.matches.match.service.MatchService;
import com.kdt.team04.domain.matches.proposal.dto.MatchProposalRequest;
import com.kdt.team04.domain.matches.proposal.entity.MatchProposal;
import com.kdt.team04.domain.matches.proposal.entity.MatchProposalStatus;
import com.kdt.team04.domain.matches.proposal.repository.MatchProposalRepository;
import com.kdt.team04.domain.team.dto.TeamConverter;
import com.kdt.team04.domain.team.dto.TeamResponse;
import com.kdt.team04.domain.team.entity.Team;
import com.kdt.team04.domain.team.service.TeamGiverService;
import com.kdt.team04.domain.teammember.service.TeamMemberGiverService;
import com.kdt.team04.domain.user.UserConverter;
import com.kdt.team04.domain.user.dto.UserResponse;
import com.kdt.team04.domain.user.entity.User;
import com.kdt.team04.domain.user.service.UserService;

@Service
@Transactional(readOnly = true)
public class MatchProposalService {

	private final MatchProposalRepository proposalRepository;
	private final MatchService matchService;
	private final UserService userService;
	private final TeamGiverService teamGiver;
	private final TeamMemberGiverService teamMemberGiver;
	private final MatchConverter matchConverter;
	private final TeamConverter teamConverter;
	private final UserConverter userConverter;

	public MatchProposalService(MatchProposalRepository proposalRepository, MatchService matchService,
		UserService userService, TeamGiverService teamGiver, TeamMemberGiverService teamMemberGiver,
		MatchConverter matchConverter,
		TeamConverter teamConverter, UserConverter userConverter) {
		this.proposalRepository = proposalRepository;
		this.matchService = matchService;
		this.userService = userService;
		this.teamGiver = teamGiver;
		this.teamMemberGiver = teamMemberGiver;
		this.matchConverter = matchConverter;
		this.teamConverter = teamConverter;
		this.userConverter = userConverter;
	}

	@Transactional
	public Long create(Long proposerId, Long matchId, MatchProposalRequest.ProposalCreate request) {
		MatchResponse matchResponse = matchService.findById(matchId);

		if (matchResponse.status().isMatched()) {
			throw new BusinessException(ErrorCode.INVALID_CREATE_REQUEST, "already matched");
		}

		UserResponse authorResponse = userService.findById(matchResponse.author().id());
		User author = userConverter.toUser(authorResponse);

		UserResponse userResponse = userService.findById(proposerId);
		User proposer = userConverter.toUser(userResponse);

		MatchProposal matchProposal = matchResponse.matchType() == MatchType.TEAM_MATCH ?
			teamProposalCreate(author, proposer, matchResponse, request) :
			individualProposalCreate(author, proposer, matchResponse, request.content());

		MatchProposal createdProposal = proposalRepository.save(matchProposal);

		return createdProposal.getId();
	}

	private MatchProposal individualProposalCreate(User author, User proposer, MatchResponse matchResponse,
		String content) {
		Match match = matchConverter.toMatch(matchResponse, author);

		return MatchProposal.builder()
			.match(match)
			.user(proposer)
			.content(content)
			.status(MatchProposalStatus.WAITING)
			.build();
	}

	private MatchProposal teamProposalCreate(User author, User proposer, MatchResponse matchResponse,
		MatchProposalRequest.ProposalCreate request) {
		if (request.teamId() == null) {
			throw new BusinessException(ErrorCode.METHOD_ARGUMENT_NOT_VALID, "Request team is null");
		}

		TeamResponse teamResponse = teamGiver.findById(matchResponse.team().id());
		Team team = teamConverter.toTeam(teamResponse, author);
		Match match = matchConverter.toMatch(matchResponse, author, team);

		TeamResponse proposeTeamResponse = teamGiver.findById(request.teamId());
		teamGiver.verifyLeader(proposer.getId(), request.teamId(), proposeTeamResponse.leader().id());
		teamMemberGiver.hasEnoughMemberCount(match.getParticipants(), request.teamId());

		Team proposerTeam = teamConverter.toTeam(proposeTeamResponse, proposer);

		return MatchProposal.builder()
			.match(match)
			.team(proposerTeam)
			.user(proposer)
			.content(request.content())
			.status(MatchProposalStatus.WAITING)
			.build();
	}

	@Transactional
	public MatchProposalStatus react(Long matchId, Long id, MatchProposalStatus status) {
		MatchResponse match = matchService.findById(matchId);
		MatchProposal proposal = proposalRepository.findById(id)
			.orElseThrow(() -> new BusinessException(ErrorCode.MATCH_PROPOSAL_NOT_FOUND,
				MessageFormat.format("proposalId = {0}", id)));

		if (match.status().isMatched() || proposal.getStatus().isApproved()) {
			throw new BusinessException(ErrorCode.INVALID_REACT,
				MessageFormat.format("matchId = {0}, proposalId = {1}, proposalStatus = {2}, matchStatus = {3}",
					match.id(), id, status, match.status()));
		}

		proposal.updateStatus(status);

		return proposal.getStatus();
	}
}
