package com.kdt.team04.domain.match.review.dto;

public record MatchReviewResponse(){
	public record TotalCount(int bestCount, int likeCount, int dislikeCount){
	}
}
