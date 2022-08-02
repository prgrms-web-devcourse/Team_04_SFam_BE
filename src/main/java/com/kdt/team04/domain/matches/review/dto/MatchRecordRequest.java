package com.kdt.team04.domain.matches.review.dto;

import javax.validation.constraints.NotNull;

import com.kdt.team04.domain.matches.review.entity.MatchRecordValue;

import io.swagger.v3.oas.annotations.media.Schema;

public record MatchRecordRequest(
	@NotNull
	@Schema(description = "매칭 신청 아이디")
	Long proposalId,

	@NotNull
	@Schema(description = "경기 결과")
	MatchRecordValue result
) {
}
