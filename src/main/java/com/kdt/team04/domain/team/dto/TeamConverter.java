package com.kdt.team04.domain.team.dto;

import org.springframework.stereotype.Component;

import com.kdt.team04.domain.team.entity.Team;

@Component
public class TeamConverter {

	public Team toTeam(TeamResponse response) {
		return Team.builder()
			.id(response.id())
			.teamName(response.teamName())
			.description(response.description())
			.build();
	}

	public TeamResponse toTeamResponse(Team team) {
		return TeamResponse.builder()
			.id(team.getId())
			.teamName(team.getTeamName())
			.sportsCategory(team.getSportsCategory())
			.description(team.getDescription())
			.createdAt(team.getCreatedAt())
			.updatedAt(team.getUpdatedAt())
			.build();
	}
}
