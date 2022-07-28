package com.kdt.team04.domain.matches.review.dto;

public record MatchReviewResponse(){
	public record TotalCount(int bestCount, int likeCount, int dislikeCount){
	}
}
