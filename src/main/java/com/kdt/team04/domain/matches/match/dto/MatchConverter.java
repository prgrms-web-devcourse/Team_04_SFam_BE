package com.kdt.team04.domain.matches.match.dto;

import org.springframework.stereotype.Component;

import com.kdt.team04.domain.matches.match.entity.Match;
import com.kdt.team04.domain.team.dto.TeamResponse;
import com.kdt.team04.domain.user.dto.UserResponse;

@Component
public class MatchConverter {
	public MatchResponse toMatchResponse(Match match, UserResponse.AuthorResponse user) {
		return MatchResponse.builder()
			.title(match.getTitle())
			.status(match.getStatus())
			.sportsCategory(match.getSportsCategory())
			.author(user)
			.participants(match.getParticipants())
			.matchDate(match.getMatchDate())
			.matchType(match.getMatchType())
			.content(match.getContent())
			.build();
	}

	public MatchResponse toMatchResponse(Match match, UserResponse.AuthorResponse user,
		TeamResponse.SimpleResponse team) {
		return MatchResponse.builder()
			.title(match.getTitle())
			.status(match.getStatus())
			.sportsCategory(match.getSportsCategory())
			.author(user)
			.team(team)
			.participants(match.getParticipants())
			.matchDate(match.getMatchDate())
			.matchType(match.getMatchType())
			.content(match.getContent())
			.build();
	}
}
