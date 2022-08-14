package com.kdt.team04.domain.matches.match.dto.request;

import java.time.LocalDate;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kdt.team04.domain.matches.match.model.MatchType;
import com.kdt.team04.domain.teams.team.model.SportsCategory;

import io.swagger.v3.oas.annotations.media.Schema;

public record CreateMatchRequest(
	@Schema(description = "매칭 제목, 2자 이상 50자 이하", required = true)
	@NotBlank
	@Size(min = 2, max = 50)
	String title,

	@Schema(description = "매칭 날짜, 오늘 이후의 날짜만 가능", required = true, pattern = "yyyy-MM-dd")
	@NotNull
	@FutureOrPresent
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
	LocalDate matchDate,

	@Schema(description = "매칭 타입(값/설명) - TEAM_MATCH/팀전, INDIVIDUAL_MATCH/개인전", required = true)
	@NotNull
	MatchType matchType,

	@Schema(description = "팀 ID(고유 PK)")
	Long teamId,

	@Schema(description = "참가 인원, 1명 이상 15명 이하", required = true)
	@Min(1) @Max(15)
	int participants,

	@Schema(description = "매칭 종목", required = true)
	@NotNull
	SportsCategory sportsCategory,

	@Schema(description = "매칭 내용, 2자 이상 100자 이하", required = true)
	@NotBlank
	@Size(min = 2, max = 100)
	String content) {

}
