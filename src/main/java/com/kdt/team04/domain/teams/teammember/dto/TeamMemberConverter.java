package com.kdt.team04.domain.teams.teammember.dto;

import org.springframework.stereotype.Component;

import com.kdt.team04.domain.teams.team.model.entity.Team;
import com.kdt.team04.domain.teams.teammember.dto.response.TeamMemberResponse;
import com.kdt.team04.domain.teams.teammember.model.entity.TeamMember;
import com.kdt.team04.domain.user.entity.User;

@Component
public class TeamMemberConverter {

	public User toUser(Long userId) {
		return User.builder()
			.id(userId)
			.build();
	}

	public Team toTeam(Long teamId) {
		return Team.builder()
			.id(teamId)
			.build();
	}

	public TeamMemberResponse toTeamMemberResponse(TeamMember teamMember) {
		return new TeamMemberResponse(teamMember.getUser().getId(), teamMember.getUser().getNickname(),
			teamMember.getRole());
	}
}
