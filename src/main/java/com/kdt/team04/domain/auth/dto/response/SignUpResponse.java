package com.kdt.team04.domain.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record SignUpResponse(
	@Schema(description = "회원 고유 PK")
	Long id
) {

}