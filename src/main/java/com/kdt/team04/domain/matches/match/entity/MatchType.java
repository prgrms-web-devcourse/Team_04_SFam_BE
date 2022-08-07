package com.kdt.team04.domain.matches.match.entity;

public enum MatchType {
	TEAM_MATCH, INDIVIDUAL_MATCH;

	public boolean isTeam() {
		return this == MatchType.TEAM_MATCH;
	}

}
