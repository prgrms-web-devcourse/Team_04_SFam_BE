package com.kdt.team04.domain.matches.review.dto.request;

import javax.validation.constraints.NotNull;

import com.kdt.team04.domain.matches.review.model.MatchReviewValue;

import io.swagger.v3.oas.annotations.media.Schema;

public record CreateMatchReviewRequest(

	@Schema(description = "리뷰(값/설명) - BEST/최고에요, LIKE/좋아요, DISLIKE/별로에요")
	@NotNull
	MatchReviewValue review
) {
}
