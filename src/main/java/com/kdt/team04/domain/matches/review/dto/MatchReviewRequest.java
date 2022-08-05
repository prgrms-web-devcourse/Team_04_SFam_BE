package com.kdt.team04.domain.matches.review.dto;

import javax.validation.constraints.NotNull;

import com.kdt.team04.domain.matches.review.entity.MatchReviewValue;

import io.swagger.v3.oas.annotations.media.Schema;

public record MatchReviewRequest(

	@Schema(description = "리뷰(값/설명) - BEST/최고에요, LIKE/좋아요, DISLIKE/별로에요")
	@NotNull
	MatchReviewValue review
) {
}
