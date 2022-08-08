package com.kdt.team04.domain.matches.review.model;

public enum MatchRecordValue {
	WIN, LOSE, DRAW;

	public MatchRecordValue getReverseResult() {
		if (this == DRAW) {
			return DRAW;
		}

		return this == WIN ? LOSE : WIN;
	}
}
