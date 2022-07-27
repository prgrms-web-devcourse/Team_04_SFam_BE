package com.kdt.team04.domain.team.dto;

import java.util.List;

import com.kdt.team04.domain.team.SportsCategory;
import com.kdt.team04.domain.teammember.dto.TeamMemberResponse;
import com.kdt.team04.domain.user.dto.UserResponse;

import lombok.Builder;

@Builder
public record TeamResponse(
	Long id,
	String teamName,
	String description,
	List<TeamMemberResponse> members,
	SportsCategory sportsCategory,
	UserResponse leader
) {
}
