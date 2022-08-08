package com.kdt.team04.domain.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record UpdateLocationResponse(
	@Schema(description = "사용자 위치 - 위도")
	double latitude,

	@Schema(description = "사용자 위치 - 경도")
	double longitude) {
}
