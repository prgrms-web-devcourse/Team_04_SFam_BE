package com.kdt.team04.domain.auth.dto.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;

public record SignUpRequest(
	@Schema(description = "회원 아이디", required = true)
	@Pattern(regexp = "^[a-z0-9_]*$")
	@NotBlank
	@Size(min = 6, max = 24)
	String username,

	@Schema(description = "회원 비밀번호", required = true)
	@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")
	@NotBlank
	String password,

	@Schema(description = "회원 닉네임", required = true)
	@NotBlank
	@Size(min = 2, max = 12)
	String nickname,

	@Schema(description = "회원 email")
	@Email
	String email
) {
}
