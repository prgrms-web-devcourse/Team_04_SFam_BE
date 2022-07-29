package com.kdt.team04.domain.matches.match.dto;

import java.time.LocalDate;

import javax.validation.constraints.Future;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.kdt.team04.domain.matches.match.entity.MatchType;
import com.kdt.team04.domain.team.SportsCategory;

import io.swagger.v3.oas.annotations.media.Schema;

public record MatchRequest() {
	public record MatchCreateRequest(

		@NotBlank
		@Size(min = 2, max = 10)
		@Schema(description = "매칭 제목, 2자 이상 10자 이하", required = true)
		String title,

		@NotNull
		@Future
		@Schema(description = "매칭 날짜, 오늘 이후의 날짜만 가능", required = true)
		LocalDate matchDate,

		@NotNull
		@Schema(description = "매칭 타입, NOT NULL", required = true)
		MatchType matchType,

		@Schema(description = "팀 아이디")
		Long teamId,

		@Min(1) @Max(15)
		@Schema(description = "참가 인원, 1명 이상 15명 이하", required = true)
		int participants,

		@NotNull
		@Schema(description = "매칭 종목, NOT NULL", required = true)
		SportsCategory sportsCategory,

		@NotBlank
		@Size(min = 2, max = 100)
		@Schema(description = "매칭 내용, 2자 이상 100자 이하", required = true)
		String content) {
	}
}
