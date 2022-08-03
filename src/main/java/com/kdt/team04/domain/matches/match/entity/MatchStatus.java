package com.kdt.team04.domain.matches.match.entity;

public enum MatchStatus {
	WAITING, IN_GAME, END;

	public boolean isMatched() {
		return this != MatchStatus.WAITING;
	}

	public boolean isEnded() {
		return this == MatchStatus.END;
	}
}
