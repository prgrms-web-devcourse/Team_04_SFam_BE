package com.kdt.team04.domain.team.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.kdt.team04.domain.team.SportsCategory;

import io.swagger.v3.oas.annotations.media.Schema;

public record CreateTeamRequest(

	@Schema(description = "팀 이름, 2자 이상 10자 이하", required = true)
	@NotBlank
	@Size(min = 2, max = 10)
	String name,

	@Schema(description = "팀 설명, 100자 이하", required = true)
	@NotBlank
	@Size(max = 100)
	String description,

	@Schema(description = "팀 종목(값/설명) - BADMINTON/배드민턴, SOCCER/축구, BASEBALL/야구", required = true)
	@NotNull
	SportsCategory sportsCategory) {
}
