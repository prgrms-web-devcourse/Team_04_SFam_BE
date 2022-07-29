package com.kdt.team04.domain.user.dto;

import javax.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;

public record UserRequest() {

	public record CreateRequest(String username, String password, String nickname) {

	}

	public record UpdateLocationRequest(
		@Schema(description = "사용자 위치 - 위도, -90 ~ 90")
		@Size(min = -90, max =90)
		double latitude,

		@Schema(description = "사용자 위치 - 경도, -180 ~ 180")
		@Size(min = -180, max =180)
		double longitude) {
	}
}
