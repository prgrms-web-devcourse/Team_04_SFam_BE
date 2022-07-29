package com.kdt.team04.domain.team.dto;

import com.kdt.team04.domain.team.SportsCategory;

import io.swagger.v3.oas.annotations.media.Schema;

public record TeamRequest() {

	public record CreateRequest(
		@Schema(description = "팀 이름", required = true)
		String name,

		@Schema(description = "팀 설명", required = true)
		String description,

		@Schema(description = "팀 종목", required = true)
		SportsCategory sportsCategory) {
	}
}
