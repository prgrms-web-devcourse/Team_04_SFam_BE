package com.kdt.team04.domain.matches.match.service;

import java.text.MessageFormat;

import org.springframework.stereotype.Service;

import com.kdt.team04.common.exception.EntityNotFoundException;
import com.kdt.team04.common.exception.ErrorCode;
import com.kdt.team04.domain.matches.match.dto.MatchConverter;
import com.kdt.team04.domain.matches.match.dto.MatchResponse;
import com.kdt.team04.domain.matches.match.entity.Match;
import com.kdt.team04.domain.matches.match.entity.MatchType;
import com.kdt.team04.domain.matches.match.repository.MatchRepository;
import com.kdt.team04.domain.team.dto.TeamResponse;
import com.kdt.team04.domain.team.entity.Team;
import com.kdt.team04.domain.user.dto.UserResponse;
import com.kdt.team04.domain.user.service.UserService;

@Service
public class MatchGiverService {
	private final MatchRepository matchRepository;
	private final UserService userService;
	private final MatchConverter matchConverter;

	public MatchGiverService(MatchRepository matchRepository, UserService userService, MatchConverter matchConverter) {
		this.matchRepository = matchRepository;
		this.userService = userService;
		this.matchConverter = matchConverter;
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

}
