package com.kdt.team04.domain.teammember.dto;

import com.kdt.team04.domain.teammember.entity.TeamMemberRole;

import io.swagger.v3.oas.annotations.media.Schema;

public record TeamMemberResponse(
	@Schema(description = "팀원 ID(고유 PK)")
	Long userId,

	@Schema(description = "팀원 닉네임")
	String nickname,

	@Schema(description = "팀원 권한(값/설명) - LEADER/팀장, MEMBER/팀원")
	TeamMemberRole role
) {

}
