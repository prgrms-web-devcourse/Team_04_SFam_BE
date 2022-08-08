package com.kdt.team04.common.file;

public enum ImagePath implements Path{
	USERS_PROFILES("users/profile/"),
	TEAMS_LOGO("teams/logo/");

	private String path;

	ImagePath(String path) {
		this.path = path;
	}

	@Override
	public String getPath() {
		return path;
	}
}
