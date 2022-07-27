package com.kdt.team04.domain.teammember.dto;

import com.kdt.team04.domain.teammember.entity.TeamMemberRole;

public record TeamMemberResponse(
	Long userId,
	String nickname,
	TeamMemberRole role
) {

}
