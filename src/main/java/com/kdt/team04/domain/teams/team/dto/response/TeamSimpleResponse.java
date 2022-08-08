package com.kdt.team04.domain.teams.team.dto.response;

import com.kdt.team04.domain.teams.team.model.SportsCategory;

import io.swagger.v3.oas.annotations.media.Schema;

public record TeamSimpleResponse(
	@Schema(description = "팀 아이디")
	Long id,

	@Schema(description = "팀명")
	String name,

	@Schema(description = "스포츠 종목(값/설명) - BADMINTON/배드민턴, SOCCER/축구, BASEBALL/야구")
	SportsCategory sportsCategory,

	@Schema(description = "팀 로고 이미지 url")
	String logoImageUrl
) {
}
