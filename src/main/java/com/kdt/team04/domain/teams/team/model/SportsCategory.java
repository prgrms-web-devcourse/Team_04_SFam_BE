package com.kdt.team04.domain.teams.team.model;

public enum SportsCategory {
	BADMINTON("배드민턴"),
	SOCCER("축구"),
	BASEBALL("야구"),
	BASKETBALL("농구"),
	TENNIS("테니스"),
	GOLF("골프"),
	BILLIARD("당구"),
	JOKGU("족구"),
	BOWLING("볼링"),
	ARM_WRESTLING("팔씨름"),
	PING_PONG("탁구");


	private final String name;

	SportsCategory(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
