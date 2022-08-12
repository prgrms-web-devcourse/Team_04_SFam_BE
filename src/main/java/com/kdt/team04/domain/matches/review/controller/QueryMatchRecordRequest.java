package com.kdt.team04.domain.matches.review.controller;

import javax.validation.constraints.AssertTrue;

import com.kdt.team04.domain.teams.team.model.SportsCategory;

import io.swagger.v3.oas.annotations.Parameter;

public record QueryMatchRecordRequest(
	@Parameter(description = "스포츠 종목(값/설명) - BADMINTON/배드민턴, SOCCER/축구, BASEBALL/야구")
	SportsCategory sportsCategory,

	@Parameter(description = "회원 ID(고유 PK)")
	Long userId,

	@Parameter(description = "팀 ID(고유 PK)")
	Long teamId
) {

	@AssertTrue
	boolean isValidIdInput() {
		return userId != null || teamId != null;
	}
}
