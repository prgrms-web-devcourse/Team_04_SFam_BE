package com.kdt.team04.domain.team;

public enum Category {
	BADMINTON("배드민턴");

	private final String name;

	Category(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
