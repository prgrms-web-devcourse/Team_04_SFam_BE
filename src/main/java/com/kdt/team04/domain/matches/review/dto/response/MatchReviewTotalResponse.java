package com.kdt.team04.domain.matches.review.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record MatchReviewTotalResponse(
	@Schema(description = "최고에요 갯수")
	int bestCount,

	@Schema(description = "좋아요! 갯수")
	int likeCount,

	@Schema(description = "별로에요 갯수")
	int dislikeCount
) {
}
