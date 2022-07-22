package com.kdt.team04.domain.user.dto;

public record UserRequest() {

	public record CreateRequest(String username, String password, String nickname) {

	}
}
