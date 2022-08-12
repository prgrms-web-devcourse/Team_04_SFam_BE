package com.kdt.team04.domain.matches.review.controller;

import javax.validation.constraints.AssertTrue;

import com.kdt.team04.domain.teams.team.model.SportsCategory;

import io.swagger.v3.oas.annotations.media.Schema;

public record QueryMatchRecordRequest(
	@Schema(description = "스포츠 종목(값/설명) - BADMINTON/배드민턴, SOCCER/축구, BASEBALL/야구")
	SportsCategory sportsCategory,

	@Schema(description = "회원 ID(고유 PK)")
	Long userId,

	@Schema(description = "팀 ID(고유 PK)")
	Long teamId
) {

	@AssertTrue
	boolean isValidIdInput() {
		return userId != null || teamId != null;
	}
}
