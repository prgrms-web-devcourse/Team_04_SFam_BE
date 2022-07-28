package com.kdt.team04.domain.matches.match.service;

import java.text.MessageFormat;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kdt.team04.common.exception.BusinessException;
import com.kdt.team04.common.exception.ErrorCode;
import com.kdt.team04.domain.matches.match.dto.MatchRequest;
import com.kdt.team04.domain.matches.match.entity.Match;
import com.kdt.team04.domain.matches.match.entity.MatchType;
import com.kdt.team04.domain.matches.match.repository.MatchRepository;
import com.kdt.team04.domain.team.dto.TeamConverter;
import com.kdt.team04.domain.team.dto.TeamResponse;
import com.kdt.team04.domain.team.entity.Team;
import com.kdt.team04.domain.team.service.TeamGiverService;
import com.kdt.team04.domain.user.UserConverter;
import com.kdt.team04.domain.user.dto.UserResponse;
import com.kdt.team04.domain.user.entity.User;
import com.kdt.team04.domain.user.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Transactional(readOnly = true)
@Service
public class MatchService {

	private final MatchRepository matchRepository;
	private final UserService userService;
	private final TeamGiverService teamGiver;
	private final TeamConverter teamConverter;
	private final UserConverter userConverter;

	public MatchService(MatchRepository matchRepository, UserService userService, TeamGiverService teamGiver,
		TeamConverter teamConverter,
		UserConverter userConverter) {
		this.matchRepository = matchRepository;
		this.userService = userService;
		this.teamGiver = teamGiver;
		this.teamConverter = teamConverter;
		this.userConverter = userConverter;
	}

	@Transactional
	public Long create(Long userId, MatchRequest.MatchCreateRequest request) {
		Match match = null;

		if (request.matchType() == MatchType.TEAM_MATCH) {
			match = teamCreate(userId, request);
		} else if (request.matchType() == MatchType.INDIVIDUAL_MATCH) {
			match = individualCreate(userId, request);
		}

		Match savedMatch = matchRepository.save(match);

		return savedMatch.getId();
	}

	private Match individualCreate(Long userId, MatchRequest.MatchCreateRequest request) {
		UserResponse userResponse = userService.findById(userId);
		User user = userConverter.toUser(userResponse);

		return Match.builder()
			.title(request.title())
			.matchDate(request.matchDate())
			.matchType(request.matchType())
			.participants(1)
			.user(user)
			.sportsCategory(request.sportsCategory())
			.content(request.content())
			.build();
	}

	private Match teamCreate(Long userId, MatchRequest.MatchCreateRequest request) {
		TeamResponse teamResponse = teamGiver.findById(request.teamId());

		if (!Objects.equals(userId, teamResponse.leader().id())) {
			throw new BusinessException(ErrorCode.NOT_TEAM_LEADER,
				MessageFormat.format("teamId = {0} , userId = {1}", request.teamId(), userId));
		}

		User teamLeader = userConverter.toUser(teamResponse.leader());
		Team team = teamConverter.toTeam(teamResponse, teamLeader);

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
}
