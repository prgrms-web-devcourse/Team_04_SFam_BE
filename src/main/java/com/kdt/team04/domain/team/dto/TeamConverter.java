package com.kdt.team04.domain.team.dto;

import org.springframework.stereotype.Component;

import com.kdt.team04.domain.team.entity.Team;
import com.kdt.team04.domain.user.UserConverter;
import com.kdt.team04.domain.user.dto.UserResponse;
import com.kdt.team04.domain.user.entity.User;

@Component
public class TeamConverter {

	private final UserConverter userConverter;

	public TeamConverter(UserConverter userConverter) {
		this.userConverter = userConverter;
	}

	public User toUser(UserResponse userResponse) {
		return new User(userResponse.id(), userResponse.password(), userResponse.username(), userResponse.nickname());
	}


	public Team toTeam(TeamResponse response) {
		return Team.builder()
			.id(response.id())
			.teamName(response.teamName())
			.description(response.description())
			.leader(userConverter.toUser(response.leader()))
			.build();
	}

	public TeamResponse toTeamResponse(Team team) {
		return TeamResponse.builder()
			.id(team.getId())
			.teamName(team.getTeamName())
			.sportsCategory(team.getSportsCategory())
			.description(team.getDescription())
			.leader(userConverter.toUserResponse(team.getLeader()))
			.createdAt(team.getCreatedAt())
			.updatedAt(team.getUpdatedAt())
			.build();
	}

}
