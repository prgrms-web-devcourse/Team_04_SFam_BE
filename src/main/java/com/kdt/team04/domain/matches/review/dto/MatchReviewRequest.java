package com.kdt.team04.domain.matches.review.dto;

import javax.validation.constraints.NotNull;

import com.kdt.team04.domain.matches.review.entity.MatchReviewValue;

import io.swagger.v3.oas.annotations.media.Schema;

public record MatchReviewRequest(

	@NotNull
	@Schema(description = "리뷰 (최고에요 | 좋아요 | 별로에요)")
	MatchReviewValue review
) {
}
