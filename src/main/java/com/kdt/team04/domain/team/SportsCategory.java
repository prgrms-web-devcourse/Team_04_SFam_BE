package com.kdt.team04.domain.team;

public enum SportsCategory {
	BADMINTON("배드민턴");

	private final String name;

	SportsCategory(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
