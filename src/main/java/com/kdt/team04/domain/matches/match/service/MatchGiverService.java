package com.kdt.team04.domain.matches.match.service;

import java.text.MessageFormat;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kdt.team04.common.exception.BusinessException;
import com.kdt.team04.common.exception.EntityNotFoundException;
import com.kdt.team04.common.exception.ErrorCode;
import com.kdt.team04.domain.matches.match.dto.MatchConverter;
import com.kdt.team04.domain.matches.match.dto.response.MatchAuthorResponse;
import com.kdt.team04.domain.matches.match.dto.response.MatchResponse;
import com.kdt.team04.domain.matches.match.model.MatchStatus;
import com.kdt.team04.domain.matches.match.model.entity.Match;
import com.kdt.team04.domain.matches.match.repository.MatchRepository;
import com.kdt.team04.domain.teams.team.dto.response.TeamSimpleResponse;
import com.kdt.team04.domain.teams.team.model.entity.Team;
import com.kdt.team04.domain.user.dto.response.AuthorResponse;
import com.kdt.team04.domain.user.dto.response.UserResponse;
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
		AuthorResponse authorResponse = new AuthorResponse(author.id(), author.nickname(), author.profileImageUrl());

		// TODO TeamResponse 구문 추가 → 테스트 코드 검토 및 수정 필요 2022.08.12(금) midas
		if (foundMatch.getMatchType().isTeam()) {
			Team team = foundMatch.getTeam();
			TeamSimpleResponse teamResponse = new TeamSimpleResponse(team.getId(), team.getName(),
				team.getSportsCategory(), team.getLogoImageUrl());

			return matchConverter.toMatchResponse(foundMatch, authorResponse, teamResponse);
		}

		return matchConverter.toMatchResponse(foundMatch, authorResponse);
	}

	public MatchAuthorResponse findMatchAuthorById(Long id) {
		Match foundMatch = matchRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.MATCH_NOT_FOUND,
				MessageFormat.format("matchId = {0}", id)));

		UserResponse author = userService.findById(foundMatch.getUser().getId());
		AuthorResponse authorResponse = new AuthorResponse(author.id(), author.nickname(), author.profileImageUrl());

		return new MatchAuthorResponse(
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

		verifyAuthor(foundMatch, userId);

		UserResponse author = userService.findById(foundMatch.getUser().getId());
		AuthorResponse authorResponse = new AuthorResponse(author.id(), author.nickname(), author.profileImageUrl());

		foundMatch.updateStatus(MatchStatus.END);
		MatchResponse matchResponse = foundMatch.getMatchType().isTeam() ? toTeamMatch(foundMatch, authorResponse) :
			matchConverter.toMatchResponse(foundMatch, authorResponse);

		return matchResponse;
	}

	private void verifyAuthor(Match match, Long userId) {
		if (!Objects.equals(match.getUser().getId(), userId)) {
			throw new BusinessException(ErrorCode.MATCH_ACCESS_DENIED,
				MessageFormat.format("userId = {0}, matchId = {1}", userId, match.getId()));
		}
	}

	private MatchResponse toTeamMatch(Match match, AuthorResponse author) {
		Team team = match.getTeam();
		TeamSimpleResponse teamResponse = new TeamSimpleResponse(team.getId(), team.getName(),
			team.getSportsCategory(), team.getLogoImageUrl());

		return matchConverter.toMatchResponse(match, author, teamResponse);
	}

	public Double getDistance(Double latitude, Double longitude, Long matchId) {
		return matchRepository.getDistance(latitude, longitude, matchId);
	}
}
