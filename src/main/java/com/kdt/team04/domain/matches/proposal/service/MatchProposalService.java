package com.kdt.team04.domain.matches.proposal.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kdt.team04.common.exception.BusinessException;
import com.kdt.team04.common.exception.ErrorCode;
import com.kdt.team04.domain.matches.match.dto.MatchConverter;
import com.kdt.team04.domain.matches.match.dto.MatchResponse;
import com.kdt.team04.domain.matches.match.entity.Match;
import com.kdt.team04.domain.matches.match.entity.MatchStatus;
import com.kdt.team04.domain.matches.match.entity.MatchType;
import com.kdt.team04.domain.matches.match.service.MatchService;
import com.kdt.team04.domain.matches.proposal.dto.MatchChatResponse;
import com.kdt.team04.domain.matches.proposal.dto.MatchProposalRequest;
import com.kdt.team04.domain.matches.proposal.dto.MatchProposalResponse;
import com.kdt.team04.domain.matches.proposal.entity.MatchProposal;
import com.kdt.team04.domain.matches.proposal.entity.MatchProposalStatus;
import com.kdt.team04.domain.matches.proposal.repository.MatchProposalRepository;
import com.kdt.team04.domain.team.dto.TeamConverter;
import com.kdt.team04.domain.team.dto.TeamResponse;
import com.kdt.team04.domain.team.entity.Team;
import com.kdt.team04.domain.team.service.TeamGiverService;
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
	private final TeamGiverService teamGiverService;
	private final MatchConverter matchConverter;
	private final TeamConverter teamConverter;
	private final UserConverter userConverter;
	private final MatchChatService matchChatService;

	public MatchProposalService(MatchProposalRepository proposalRepository, MatchService matchService,
		UserService userService, TeamGiverService teamGiverService, MatchConverter matchConverter,
		TeamConverter teamConverter, UserConverter userConverter, MatchChatService matchChatService) {
		this.proposalRepository = proposalRepository;
		this.matchService = matchService;
		this.userService = userService;
		this.teamGiverService = teamGiverService;
		this.matchConverter = matchConverter;
		this.teamConverter = teamConverter;
		this.userConverter = userConverter;
		this.matchChatService = matchChatService;
	}

	@Transactional
	public Long create(Long proposerId, Long matchId, MatchProposalRequest.ProposalCreate request) {
		MatchResponse matchResponse = matchService.findById(matchId);

		if (matchResponse.status() != MatchStatus.WAITING) {
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

		TeamResponse teamResponse = teamGiverService.findById(matchResponse.team().id());
		Team team = teamConverter.toTeam(teamResponse, author);

		TeamResponse proposeTeamResponse = teamGiverService.findById(request.teamId());
		Team proposerTeam = teamConverter.toTeam(proposeTeamResponse, proposer);

		Match match = matchConverter.toMatch(matchResponse, author, team);

		return MatchProposal.builder()
			.match(match)
			.team(proposerTeam)
			.user(proposer)
			.content(request.content())
			.status(MatchProposalStatus.WAITING)
			.build();
	}

	public List<MatchProposalResponse.Chat> findAllProposals(Long matchId) {
		List<MatchProposal> matchProposals = proposalRepository.findAllByMatchId(matchId);
		List<Long> matchProposalIds = matchProposals.stream()
			.map(MatchProposal::getId)
			.toList();

		Map<Long, MatchChatResponse.LastChat> lastChats = matchChatService.findAllLastChats(matchProposalIds);
		List<MatchProposalResponse.Chat> proposals = matchProposals.stream()
			.map(proposal -> {
				MatchChatResponse.LastChat lastChat = lastChats.get(proposal.getId());
				UserResponse.ChatTargetProfile chatTargetProfile
					= new UserResponse.ChatTargetProfile(proposal.getUser().getNickname());

				return new MatchProposalResponse.Chat(
					proposal.getId(),
					proposal.getContent(),
					chatTargetProfile,
					lastChat
				);
			})
			.toList();

		return proposals;
	}
}
