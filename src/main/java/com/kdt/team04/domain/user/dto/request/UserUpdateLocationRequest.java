package com.kdt.team04.domain.user.dto.request;

import org.hibernate.validator.constraints.Range;

import io.swagger.v3.oas.annotations.media.Schema;

public record UserUpdateLocationRequest(
	@Schema(description = "사용자 위치 - 위도, -90 ~ 90")
	@Range(min = -90, max = 90)
	double latitude,

	@Schema(description = "사용자 위치 - 경도, -180 ~ 180")
	@Range(min = -180, max = 180)
	double longitude,

	@Schema(description = "조회 거리설정 - 5km ~ 40km")
	@Range(min = 5, max = 40)
	Integer searchDistance) {
}
