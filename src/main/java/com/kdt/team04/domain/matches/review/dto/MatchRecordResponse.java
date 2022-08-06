package com.kdt.team04.domain.matches.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record MatchRecordResponse() {
	public record TotalCount(

		@Schema(description = "승리 수")
		int win,

		@Schema(description = "무승부 수")
		int draw,

		@Schema(description = "패배 수")
		int lose
	) { }
}
