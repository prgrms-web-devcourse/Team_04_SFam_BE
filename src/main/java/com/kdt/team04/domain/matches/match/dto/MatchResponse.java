package com.kdt.team04.domain.matches.match.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.kdt.team04.domain.matches.match.entity.MatchStatus;
import com.kdt.team04.domain.matches.match.entity.MatchType;
import com.kdt.team04.domain.team.SportsCategory;
import com.kdt.team04.domain.team.dto.TeamResponse;
import com.kdt.team04.domain.user.dto.UserResponse;
import com.kdt.team04.domain.user.entity.Location;

public record MatchResponse(
	Long id,
	String title,
	SportsCategory category,
	MatchType matchType,
	LocalDate matchDate,
	String content,
	MatchStatus status,
	UserResponse userResponse,
	TeamResponse teamResponse,
	Location location,
	Double distance
) {
	public MatchResponse {
	}

	public record ListViewResponse(
		Long id,
		String title,
		SportsCategory category,
		MatchType matchType,
		String content,
		Double distance,
		LocalDateTime createdAt
	) {

	}

}

