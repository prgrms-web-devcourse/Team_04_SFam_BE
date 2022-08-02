package com.kdt.team04.domain.matches.match.service;

import java.text.MessageFormat;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kdt.team04.common.exception.BusinessException;
import com.kdt.team04.common.exception.EntityNotFoundException;
import com.kdt.team04.common.exception.ErrorCode;
import com.kdt.team04.domain.matches.match.dto.MatchConverter;
import com.kdt.team04.domain.matches.match.dto.MatchResponse;
import com.kdt.team04.domain.matches.match.entity.Match;
import com.kdt.team04.domain.matches.match.entity.MatchStatus;
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

	public MatchGiverService(
		MatchRepository matchRepository,
		UserService userService,
		MatchConverter matchConverter
	) {
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

	public MatchResponse.MatchAuthorResponse findMatchAuthorById(Long id) {
		Match foundMatch = matchRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.MATCH_NOT_FOUND,
				MessageFormat.format("matchId = {0}", id)));

		UserResponse author = userService.findById(foundMatch.getUser().getId());
		UserResponse.AuthorResponse authorResponse = new UserResponse.AuthorResponse(author.id(), author.nickname());

		return new MatchResponse.MatchAuthorResponse(
			foundMatch.getId(),
			foundMatch.getTitle(),
			foundMatch.getStatus(),
			authorResponse
		);
	}

	@Transactional
	public MatchResponse endGame(Long id, Long userId) {
		Match foundMatch = matchRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.MATCH_NOT_FOUND,
				MessageFormat.format("matchId = {0}", id)));

		if (!Objects.equals(foundMatch.getStatus(), MatchStatus.IN_GAME)) {
			throw new BusinessException(ErrorCode.MATCH_NOT_IN_GAME,
				MessageFormat.format("matchId = {0} , userId = {1}", id, userId));
		}

		if (!Objects.equals(foundMatch.getUser().getId(), userId)) {
			throw new BusinessException(ErrorCode.MATCH_ACCESS_DENIED,
				MessageFormat.format("matchId = {0} , userId = {1}", id, userId));
		}

		UserResponse author = userService.findById(foundMatch.getUser().getId());
		UserResponse.AuthorResponse authorResponse = new UserResponse.AuthorResponse(author.id(), author.nickname());

		foundMatch.updateStatus(MatchStatus.END);

		if (foundMatch.getMatchType() == MatchType.TEAM_MATCH) {
			Team team = foundMatch.getTeam();
			TeamResponse.SimpleResponse teamResponse = new TeamResponse.SimpleResponse(team.getId(), team.getName(),
				team.getSportsCategory());

			return matchConverter.toMatchResponse(foundMatch, authorResponse, teamResponse);
		}

		return matchConverter.toMatchResponse(foundMatch, authorResponse);
	}
}
