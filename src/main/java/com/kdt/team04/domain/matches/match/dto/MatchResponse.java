package com.kdt.team04.domain.matches.match.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.kdt.team04.domain.matches.match.entity.MatchStatus;
import com.kdt.team04.domain.matches.match.entity.MatchType;
import com.kdt.team04.domain.team.SportsCategory;
import com.kdt.team04.domain.team.dto.TeamResponse;
import com.kdt.team04.domain.user.dto.UserResponse;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record MatchResponse(
	@Schema(description = "매칭 글 제목")
	String title,
	@Schema(description = "매칭 상태")
	MatchStatus status,
	@Schema(description = "매칭 종목")
	SportsCategory sportsCategory,
	@Schema(description = "매칭 글 작성자")
	UserResponse.AuthorResponse author,
	@Schema(description = "매칭 참여 팀")
	TeamResponse.SimpleResponse team,
	@Schema(description = "매칭 참여 인원")
	int participants,
	@Schema(description = "매칭 날짜")
	LocalDate matchDate,
	@Schema(description = "매칭 타입")
	MatchType matchType,
	@Schema(description = "매칭 글 내용")
	String content
) {

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

