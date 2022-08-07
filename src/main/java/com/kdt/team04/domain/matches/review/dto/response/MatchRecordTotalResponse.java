package com.kdt.team04.domain.matches.review.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record MatchRecordTotalResponse(

	@Schema(description = "승리 수")
	int win,

	@Schema(description = "무승부 수")
	int draw,

	@Schema(description = "패배 수")
	int lose
) {
}
