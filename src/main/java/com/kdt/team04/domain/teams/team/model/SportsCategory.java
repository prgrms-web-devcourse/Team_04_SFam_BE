package com.kdt.team04.domain.teams.team.model;

public enum SportsCategory {
	BADMINTON("배드민턴"),
	SOCCER("축구"),
	BASEBALL("야구");

	private final String name;

	SportsCategory(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
