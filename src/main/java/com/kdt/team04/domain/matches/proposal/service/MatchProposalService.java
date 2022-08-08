package com.kdt.team04.domain.matches.proposal.service;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kdt.team04.common.exception.BusinessException;
import com.kdt.team04.common.exception.ErrorCode;
import com.kdt.team04.domain.matches.match.dto.MatchConverter;
import com.kdt.team04.domain.matches.match.dto.response.MatchAuthorResponse;
import com.kdt.team04.domain.matches.match.dto.response.MatchResponse;
import com.kdt.team04.domain.matches.match.model.entity.Match;
import com.kdt.team04.domain.matches.match.model.MatchType;
import com.kdt.team04.domain.matches.match.service.MatchGiverService;
import com.kdt.team04.domain.matches.proposal.dto.request.CreateProposalRequest;
import com.kdt.team04.domain.matches.proposal.dto.response.LastChatResponse;
import com.kdt.team04.domain.matches.proposal.dto.response.ChatRoomResponse;
import com.kdt.team04.domain.matches.proposal.dto.response.ProposalIdResponse;
import com.kdt.team04.domain.matches.proposal.dto.response.ProposalSimpleResponse;
import com.kdt.team04.domain.matches.proposal.entity.MatchProposal;
import com.kdt.team04.domain.matches.proposal.entity.MatchProposalStatus;
import com.kdt.team04.domain.matches.proposal.repository.MatchProposalRepository;
import com.kdt.team04.domain.teams.team.dto.TeamConverter;
import com.kdt.team04.domain.teams.team.dto.response.TeamResponse;
import com.kdt.team04.domain.teams.team.model.entity.Team;
import com.kdt.team04.domain.teams.team.service.TeamGiverService;
import com.kdt.team04.domain.teams.teammember.service.TeamMemberGiverService;
import com.kdt.team04.domain.user.UserConverter;
import com.kdt.team04.domain.user.dto.response.ChatTargetProfileResponse;
import com.kdt.team04.domain.user.dto.response.UserResponse;
import com.kdt.team04.domain.user.entity.User;
import com.kdt.team04.domain.user.service.UserService;

@Service
@Transactional(readOnly = true)
public class MatchProposalService {

	private final MatchProposalRepository proposalRepository;
	private final UserService userService;
	private final MatchChatService matchChatService;
	private final MatchGiverService matchGiver;
	private final TeamGiverService teamGiver;
	private final TeamMemberGiverService teamMemberGiver;
	private final MatchConverter matchConverter;
	private final TeamConverter teamConverter;
	private final UserConverter userConverter;

	public MatchProposalService(
		MatchProposalRepository proposalRepository,
		MatchGiverService matchGiver,
		UserService userService,
		TeamGiverService teamGiver,
		TeamMemberGiverService teamMemberGiver,
		MatchChatService matchChatService,
		MatchConverter matchConverter,
		TeamConverter teamConverter,
		UserConverter userConverter
	) {
		this.proposalRepository = proposalRepository;
		this.matchGiver = matchGiver;
		this.userService = userService;
		this.teamGiver = teamGiver;
		this.teamMemberGiver = teamMemberGiver;
		this.matchChatService = matchChatService;
		this.matchConverter = matchConverter;
		this.teamConverter = teamConverter;
		this.userConverter = userConverter;
	}

	@Transactional
	public Long create(Long proposerId, Long matchId, CreateProposalRequest request) {
		MatchResponse matchResponse = matchGiver.findById(matchId);

		if (matchResponse.status().isMatched()) {
			throw new BusinessException(ErrorCode.PROPOSAL_INVALID_CREATE_REQUEST, "already matched");
		}

		if (Objects.equals(matchResponse.author().id(), proposerId)) {
			throw new BusinessException(ErrorCode.PROPOSAL_INVALID_CREATE_REQUEST, MessageFormat.format(
				"proposalId = {0}, authorId = {1}",
				proposerId, matchResponse.author().id()));
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
		CreateProposalRequest request) {
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
		MatchResponse match = matchGiver.findById(matchId);
		MatchProposal proposal = proposalRepository.findById(id)
			.orElseThrow(() -> new BusinessException(ErrorCode.PROPOSAL_NOT_FOUND,
				MessageFormat.format("proposalId = {0}", id)));

		if (match.status().isMatched() || proposal.getStatus().isApproved()) {
			throw new BusinessException(ErrorCode.PROPOSAL_INVALID_REACT,
				MessageFormat.format("matchId = {0}, proposalId = {1}, proposalStatus = {2}, matchStatus = {3}",
					match.id(), id, status, match.status()));
		}

		proposal.updateStatus(status);

		return proposal.getStatus();
	}

	public List<ChatRoomResponse> findAllProposalChats(Long matchId, Long authorId) {
		MatchAuthorResponse matchAuthor = matchGiver.findMatchAuthorById(matchId);
		if (!Objects.equals(matchAuthor.author().id(), authorId)) {
			throw new BusinessException(ErrorCode.MATCH_ACCESS_DENIED,
				MessageFormat.format("Don't have permission to access match with matchId={0}, authorId={1}, userId={2}",
					matchId, matchAuthor.author().id(), authorId));
		}

		List<MatchProposal> matchProposals = proposalRepository.findAllByMatchId(matchId);
		if (matchProposals.isEmpty()) {
			throw new BusinessException(ErrorCode.PROPOSAL_NOT_FOUND,
				MessageFormat.format("Match proposal not found with matchId={0}, authorId={1}", matchId, authorId));
		}

		List<Long> matchProposalIds = matchProposals.stream()
			.map(MatchProposal::getId)
			.toList();

		Map<Long, LastChatResponse> lastChats = matchChatService.findAllLastChats(matchProposalIds);
		List<ChatRoomResponse> proposalChats = matchProposals.stream()
			.map(proposal -> {
				LastChatResponse chatLastResponse = lastChats.get(proposal.getId());
				ChatTargetProfileResponse chatTargetProfile
					= new ChatTargetProfileResponse(proposal.getUser().getNickname());

				return new ChatRoomResponse(
					proposal.getId(),
					proposal.getContent(),
					chatTargetProfile,
					chatLastResponse
				);
			})
			.toList();

		return proposalChats;
	}

	@Transactional
	public void deleteByMatches(Long matchId) {
		List<MatchProposal> foundProposals = proposalRepository.findAllByMatchId(matchId);
		List<ProposalIdResponse> proposalResponses = foundProposals.stream()
			.map(response -> new ProposalIdResponse(response.getId()))
			.toList();
		matchChatService.deleteAllByProposals(proposalResponses);
		proposalRepository.deleteAllByMatchId(matchId);
	}

	public Optional<ProposalSimpleResponse> findByMatchIdAndUserId(Long matchId, Long userId) {
		return proposalRepository.findByMatchIdAndUserId(matchId, userId)
			.map(proposal -> new ProposalSimpleResponse(
				proposal.getId(),
				proposal.getStatus(),
				proposal.getContent()
			));
	}
}
