package com.kdt.team04.domain.matches.match.service;

import java.text.MessageFormat;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kdt.team04.common.PageDto;
import com.kdt.team04.common.exception.BusinessException;
import com.kdt.team04.common.exception.EntityNotFoundException;
import com.kdt.team04.common.exception.ErrorCode;
import com.kdt.team04.domain.matches.match.dto.MatchConverter;
import com.kdt.team04.domain.matches.match.dto.MatchPagingCursor;
import com.kdt.team04.domain.matches.match.dto.MatchRequest;
import com.kdt.team04.domain.matches.match.dto.MatchResponse;
import com.kdt.team04.domain.matches.match.entity.Match;
import com.kdt.team04.domain.matches.match.entity.MatchType;
import com.kdt.team04.domain.matches.match.repository.MatchRepository;
import com.kdt.team04.domain.team.dto.TeamConverter;
import com.kdt.team04.domain.team.dto.TeamResponse;
import com.kdt.team04.domain.team.entity.Team;
import com.kdt.team04.domain.team.service.TeamGiverService;
import com.kdt.team04.domain.teammember.service.TeamMemberGiverService;
import com.kdt.team04.domain.user.UserConverter;
import com.kdt.team04.domain.user.dto.UserResponse;
import com.kdt.team04.domain.user.entity.Location;
import com.kdt.team04.domain.user.entity.User;
import com.kdt.team04.domain.user.service.UserService;

@Transactional(readOnly = true)
@Service
public class MatchService {

	private final MatchRepository matchRepository;
	private final UserService userService;
	private final TeamGiverService teamGiver;
	private final TeamMemberGiverService teamMemberGiver;
	private final MatchConverter matchConverter;
	private final TeamConverter teamConverter;
	private final UserConverter userConverter;

	public MatchService(MatchRepository matchRepository, UserService userService, TeamGiverService teamGiver,
		TeamMemberGiverService teamMemberGiver, MatchConverter matchConverter, TeamConverter teamConverter,
		UserConverter userConverter) {
		this.matchRepository = matchRepository;
		this.userService = userService;
		this.teamGiver = teamGiver;
		this.teamMemberGiver = teamMemberGiver;
		this.matchConverter = matchConverter;
		this.teamConverter = teamConverter;
		this.userConverter = userConverter;
	}

	@Transactional
	public Long create(Long userId, MatchRequest.MatchCreateRequest request) {
		Match match = request.matchType() == MatchType.TEAM_MATCH ?
			teamMatchCreate(userId, request) : individualMatchCreate(userId, request);
		Match savedMatch = matchRepository.save(match);

		return savedMatch.getId();
	}

	private Match individualMatchCreate(Long userId, MatchRequest.MatchCreateRequest request) {
		if (request.participants() != 1) {
			throw new BusinessException(ErrorCode.INVALID_PARTICIPANTS,
				MessageFormat.format("userId = {0}, participants = {1}", userId, request.participants()));
		}

		UserResponse userResponse = userService.findById(userId);
		User user = userConverter.toUser(userResponse);

		if (user.getLocation() == null) {
			throw new BusinessException(ErrorCode.LOCATION_NOT_FOUND,
				MessageFormat.format("User id = {0} location is null", user.getId()));
		}

		return Match.builder()
			.title(request.title())
			.matchDate(request.matchDate())
			.matchType(request.matchType())
			.participants(request.participants())
			.user(user)
			.sportsCategory(request.sportsCategory())
			.content(request.content())
			.build();
	}

	private Match teamMatchCreate(Long userId, MatchRequest.MatchCreateRequest request) {
		if (request.teamId() == null) {
			throw new BusinessException(ErrorCode.METHOD_ARGUMENT_NOT_VALID, "teamId is null");
		}

		TeamResponse teamResponse = teamGiver.findById(request.teamId());
		verifyLeader(userId, request.teamId(), teamResponse.leader().id());
		verifyTeamMemberCount(request.participants(), request.teamId());

		User teamLeader = userConverter.toUser(teamResponse.leader());
		Team team = teamConverter.toTeam(teamResponse, teamLeader);

		if (teamLeader.getLocation() == null) {
			throw new BusinessException(ErrorCode.LOCATION_NOT_FOUND,
				MessageFormat.format("User id = {0} location is null", teamLeader.getId()));
		}

		return Match.builder()
			.title(request.title())
			.matchDate(request.matchDate())
			.matchType(request.matchType())
			.participants(request.participants())
			.user(teamLeader)
			.team(team)
			.sportsCategory(request.sportsCategory())
			.content(request.content())
			.build();
	}

	public PageDto.CursorResponse<MatchResponse.ListViewResponse, MatchPagingCursor> findMatches(Long myId,
		PageDto.MatchCursorPageRequest request) {
		UserResponse foundUser = userService.findById(myId);
		if (foundUser.location() == null) {
			throw new BusinessException(ErrorCode.LOCATION_NOT_FOUND,
				MessageFormat.format("User id = {0} location is null", myId));
		}
		Location location = foundUser.location();

		return matchRepository.findByLocationPaging(
			location.getLatitude(), location.getLongitude(), request);
	}

	public MatchResponse findById(Long id) {
		Match foundMatch = matchRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.MATCH_NOT_FOUND,
				MessageFormat.format("matchId = {0}", id)));

		UserResponse author = userService.findById(foundMatch.getUser().getId());
		UserResponse.AuthorResponse authorResponse = new UserResponse.AuthorResponse(author.id(), author.nickname());

		if (foundMatch.getMatchType() == MatchType.TEAM_MATCH) {
			Team team = foundMatch.getTeam();
			TeamResponse.SimpleResponse teamResponse = new TeamResponse.SimpleResponse(team.getId(), team.getName(),
				team.getSportsCategory());

			return matchConverter.toMatchResponse(foundMatch, authorResponse, teamResponse);
		}

		return matchConverter.toMatchResponse(foundMatch, authorResponse);
	}

	private void verifyLeader(Long userId, Long teamId, Long leaderId) {
		if (!Objects.equals(userId, leaderId)) {
			throw new BusinessException(ErrorCode.NOT_TEAM_LEADER,
				MessageFormat.format("teamId = {0} , userId = {1}", teamId, userId));
		}
	}

	private void verifyTeamMemberCount(int participants, Long teamId) {
		int teamMemberCount = teamMemberGiver.countByTeamId(teamId);

		if (teamMemberCount < participants) {
			throw new BusinessException(ErrorCode.INVALID_PARTICIPANTS,
				MessageFormat.format("TeamMemberCount = {0} participants = {1}",
					teamMemberCount, participants));
		}
	}
}
