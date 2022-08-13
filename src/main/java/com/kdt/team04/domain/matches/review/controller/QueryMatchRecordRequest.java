package com.kdt.team04.domain.matches.review.controller;

import com.kdt.team04.domain.teams.team.model.SportsCategory;

import io.swagger.v3.oas.annotations.Parameter;

public class QueryMatchRecordRequest {
	@Parameter(description = "스포츠 종목(값/설명) - BADMINTON/배드민턴, SOCCER/축구, BASEBALL/야구")
	private SportsCategory sportsCategory;

	@Parameter(description = "회원 ID(고유 PK)")
	private Long userId;

	@Parameter(description = "팀 ID(고유 PK)")
	private Long teamId;

	public QueryMatchRecordRequest(SportsCategory sportsCategory, Long userId, Long teamId) {
		this.sportsCategory = sportsCategory;
		this.userId = userId;
		this.teamId = teamId;
	}

	public SportsCategory getSportsCategory() {
		return sportsCategory;
	}

	public Long getUserId() {
		return userId;
	}

	public Long getTeamId() {
		return teamId;
	}
}
