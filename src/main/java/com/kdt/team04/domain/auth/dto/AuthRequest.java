package com.kdt.team04.domain.auth.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;

public record AuthRequest() {
	public record SignInRequest(
		@Schema(description = "회원 id", required = true)
		@Pattern(regexp = "^[a-z0-9_]*$")
		@NotBlank
		@Size(min = 6, max = 24)
		String username,

		@Schema(description = "회원 password", required = true)
		@NotBlank
		String password) {
	}

	public record SignUpRequest(
		@Schema(description = "회원 id", required = true)
		@Pattern(regexp = "^[a-z0-9_]*$")
		@NotBlank
		@Size(min = 6, max = 24)
		String username,

		@Schema(description = "회원 password", required = true)
		@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")
		@NotBlank
		String password,

		@Schema(description = "회원 nickname", required = true)
		@NotBlank
		@Size(min = 2, max = 16)
		String nickname,

		@Schema(description = "회원 email")
		@Email
		String email
	) {
	}
}
