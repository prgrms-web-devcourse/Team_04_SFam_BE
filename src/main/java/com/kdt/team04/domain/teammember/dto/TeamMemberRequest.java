package com.kdt.team04.domain.teammember.dto;

import javax.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;

public class TeamMemberRequest {

	public record RegisterRequest(
		@Schema(description = "팀 아이디", required = true)
		@NotNull(message = "팀 아이디는 필수입니다.")
		Long teamId,

		@Schema(description = "팀원 아이디", required = true)
		@NotNull(message = "유저 아이디는 필수입니다.")
		Long userId) {
	}

}
