package com.kdt.team04.domain.user.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;

public record UpdateUserRequest(
	@Schema(description = "회원 닉네임")
	@NotBlank
	@Size(min = 2, max = 12)
	String nickname
) {
}
