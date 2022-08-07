package com.kdt.team04.domain.auth.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;

public record SignInRequest(
	@Schema(description = "회원 아이디", required = true)
	@Pattern(regexp = "^[a-z0-9_]*$")
	@NotBlank
	@Size(min = 6, max = 24)
	String username,

	@Schema(description = "회원 비밀번호", required = true)
	@NotBlank
	String password) {
}
