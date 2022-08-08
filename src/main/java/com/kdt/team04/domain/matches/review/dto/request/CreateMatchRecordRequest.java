package com.kdt.team04.domain.matches.review.dto.request;

import javax.validation.constraints.NotNull;

import com.kdt.team04.domain.matches.review.entity.MatchRecordValue;

import io.swagger.v3.oas.annotations.media.Schema;

public record CreateMatchRecordRequest(
	@Schema(description = "매칭 신청 ID(고유 PK)", required = true)
	@NotNull
	Long proposalId,

	@Schema(description = "경기 결과(값/설명) - WIN/승리, LOSE/패배, DRAW/무승부", required = true)
	@NotNull
	MatchRecordValue result
) {
}