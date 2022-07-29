package com.kdt.team04.domain.match.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record MatchReviewResponse(){
	public record TotalCount(
		@Schema(description = "최고에요 갯수")
		int bestCount,

		@Schema(description = "좋아요! 갯수")
		int likeCount,

		@Schema(description = "별로에요 갯수")
		int dislikeCount
	){ }
}
