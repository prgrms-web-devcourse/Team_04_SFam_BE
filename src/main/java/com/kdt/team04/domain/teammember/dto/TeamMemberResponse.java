package com.kdt.team04.domain.teammember.dto;

import com.kdt.team04.domain.teammember.entity.TeamMemberRole;

import io.swagger.v3.oas.annotations.media.Schema;

public record TeamMemberResponse(
	@Schema(description = "팀원의 아이디")
	Long userId,
	@Schema(description = "팀원의 닉네임")
	String nickname,
	@Schema(description = "팀원의 권한")
	TeamMemberRole role
) {

}
