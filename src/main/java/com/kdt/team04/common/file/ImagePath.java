package com.kdt.team04.common.file;

// https://sfam-bucket.s3.ap-northeast-2.amazonaws.com - prefix
// users/profile/11f55110-9732-483a-b10a-bb7bcb7ba728.jpeg - key
public enum ImagePath {
	USERS_PROFILES("users/profile/"),
	TEAMS_LOGO("teams/logo/");

	private String path;

	ImagePath(String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}
}
