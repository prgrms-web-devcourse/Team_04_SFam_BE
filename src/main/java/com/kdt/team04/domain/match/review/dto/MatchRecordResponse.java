package com.kdt.team04.domain.match.review.dto;

public record MatchRecordResponse() {
	public record TotalCount(int win, int draw, int lose) {

	}
}
