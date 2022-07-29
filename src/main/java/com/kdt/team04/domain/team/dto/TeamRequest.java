package com.kdt.team04.domain.team.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.kdt.team04.domain.team.SportsCategory;

import io.swagger.v3.oas.annotations.media.Schema;

public record TeamRequest() {

	public record CreateRequest(

		@NotBlank
		@Size(min = 2, max = 10)
		@Schema(description = "팀 이름, 2자 이상 10자 이하", required = true)
		String name,

		@NotBlank
		@Size(max = 100)
		@Schema(description = "팀 설명, 100자 이하", required = true)
		String description,

		@NotNull
		@Schema(description = "팀 종목, NOT NULL", required = true)
		SportsCategory sportsCategory) {
	}
}
