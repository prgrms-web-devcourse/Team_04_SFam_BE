package com.kdt.team04.domain.teams.teammember.dto.request;

import javax.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;

public record RegisterTeamMemberRequest(
	@Schema(description = "팀원 ID(고유 PK)", required = true)
	@NotNull(message = "유저 아이디는 필수입니다.")
	Long userId) {
}

