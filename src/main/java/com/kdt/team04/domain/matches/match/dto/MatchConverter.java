package com.kdt.team04.domain.matches.match.dto;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.kdt.team04.domain.matches.match.dto.response.MatchResponse;
import com.kdt.team04.domain.matches.match.model.entity.Match;
import com.kdt.team04.domain.matches.proposal.dto.response.ProposalSimpleResponse;
import com.kdt.team04.domain.teams.team.dto.response.TeamSimpleResponse;
import com.kdt.team04.domain.teams.team.model.entity.Team;
import com.kdt.team04.domain.user.dto.response.AuthorResponse;
import com.kdt.team04.domain.user.entity.User;

@Component
public class MatchConverter {
	public MatchResponse toMatchResponse(Match match, AuthorResponse user) {
		return MatchResponse.builder()
			.id(match.getId())
			.title(match.getTitle())
			.status(match.getStatus())
			.sportsCategory(match.getSportsCategory())
			.author(user)
			.participants(match.getParticipants())
			.matchDate(match.getMatchDate())
			.matchType(match.getMatchType())
			.content(match.getContent())
			.location(match.getLocation())
			.build();
	}

	public MatchResponse toMatchResponse(
		Match match,
		AuthorResponse user,
		Optional<ProposalSimpleResponse> proposer
	) {
		return MatchResponse.builder()
			.id(match.getId())
			.title(match.getTitle())
			.status(match.getStatus())
			.sportsCategory(match.getSportsCategory())
			.author(user)
			.participants(match.getParticipants())
			.matchDate(match.getMatchDate())
			.matchType(match.getMatchType())
			.content(match.getContent())
			.proposer(proposer)
			.location(match.getLocation())
			.build();
	}

	public MatchResponse toMatchResponse(
		Match match,
		AuthorResponse user,
		TeamSimpleResponse team
	) {
		return MatchResponse.builder()
			.id(match.getId())
			.title(match.getTitle())
			.status(match.getStatus())
			.sportsCategory(match.getSportsCategory())
			.author(user)
			.team(team)
			.participants(match.getParticipants())
			.matchDate(match.getMatchDate())
			.matchType(match.getMatchType())
			.content(match.getContent())
			.location(match.getLocation())
			.build();
	}

	public MatchResponse toMatchResponse(
		Match match,
		AuthorResponse user,
		TeamSimpleResponse team,
		Optional<ProposalSimpleResponse> proposer
	) {
		return MatchResponse.builder()
			.id(match.getId())
			.title(match.getTitle())
			.status(match.getStatus())
			.sportsCategory(match.getSportsCategory())
			.author(user)
			.team(team)
			.participants(match.getParticipants())
			.matchDate(match.getMatchDate())
			.matchType(match.getMatchType())
			.content(match.getContent())
			.proposer(proposer)
			.location(match.getLocation())
			.build();
	}

	public Match toMatch(MatchResponse matchResponse, User user) {
		return Match.builder()
			.id(matchResponse.id())
			.title(matchResponse.title())
			.status(matchResponse.status())
			.sportsCategory(matchResponse.sportsCategory())
			.user(user)
			.content(matchResponse.content())
			.matchDate(matchResponse.matchDate())
			.matchType(matchResponse.matchType())
			.participants(matchResponse.participants())
			.location(matchResponse.location())
			.build();

	}

	public Match toMatch(MatchResponse matchResponse, User user, Team team) {
		return Match.builder()
			.id(matchResponse.id())
			.title(matchResponse.title())
			.status(matchResponse.status())
			.sportsCategory(matchResponse.sportsCategory())
			.user(user)
			.team(team)
			.content(matchResponse.content())
			.matchDate(matchResponse.matchDate())
			.matchType(matchResponse.matchType())
			.participants(matchResponse.participants())
			.location(matchResponse.location())
			.build();

	}
}
