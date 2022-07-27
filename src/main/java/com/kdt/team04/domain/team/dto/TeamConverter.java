package com.kdt.team04.domain.team.dto;

import java.util.List;

import org.springframework.stereotype.Component;

import com.kdt.team04.domain.match.review.dto.MatchRecordResponse;
import com.kdt.team04.domain.match.review.dto.MatchReviewResponse;
import com.kdt.team04.domain.team.entity.Team;
import com.kdt.team04.domain.teammember.dto.TeamMemberResponse;
import com.kdt.team04.domain.user.dto.UserResponse;
import com.kdt.team04.domain.user.entity.User;

@Component
public class TeamConverter {

	public User toUser(UserResponse userResponse) {
		return new User(userResponse.id(), userResponse.password(), userResponse.username(), userResponse.nickname());
	}

	public UserResponse toUserResponse(User user) {
		return new UserResponse(user.getId(), user.getUsername(), user.getPassword(), user.getNickname());
	}

	public Team toTeam(TeamResponse response) {
		return Team.builder()
			.id(response.id())
			.name(response.teamName())
			.description(response.description())
			.leader(toUser(response.leader()))
			.build();
	}

	public TeamResponse toTeamResponse(Team team) {
		return TeamResponse.builder()
			.id(team.getId())
			.teamName(team.getName())
			.sportsCategory(team.getSportsCategory())
			.description(team.getDescription())
			.leader(toUserResponse(team.getLeader()))
			.build();
	}

	public TeamResponse toTeamResponse(Team team, List<TeamMemberResponse> teamMemberResponses,
		MatchRecordResponse.TotalCount recordCount, MatchReviewResponse.TotalCount review) {
		return TeamResponse.builder()
			.id(team.getId())
			.teamName(team.getName())
			.members(teamMemberResponses)
			.sportsCategory(team.getSportsCategory())
			.description(team.getDescription())
			.matchRecord(recordCount)
			.matchReview(review)
			.leader(toUserResponse(team.getLeader()))
			.build();
	}
}
