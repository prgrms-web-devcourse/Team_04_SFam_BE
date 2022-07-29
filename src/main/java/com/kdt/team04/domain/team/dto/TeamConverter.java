package com.kdt.team04.domain.team.dto;

import java.util.List;

import org.springframework.stereotype.Component;

import com.kdt.team04.domain.matches.review.dto.MatchRecordResponse;
import com.kdt.team04.domain.matches.review.dto.MatchReviewResponse;
import com.kdt.team04.domain.team.entity.Team;
import com.kdt.team04.domain.teammember.dto.TeamMemberResponse;
import com.kdt.team04.domain.user.dto.UserResponse;
import com.kdt.team04.domain.user.entity.User;

@Component
public class TeamConverter {

	public Team toTeam(TeamResponse response, User leader) {
		return Team.builder()
			.id(response.id())
			.name(response.name())
			.description(response.description())
			.leader(leader)
			.build();
	}

	public TeamResponse toTeamResponse(Team team, UserResponse leader) {
		return TeamResponse.builder()
			.id(team.getId())
			.name(team.getName())
			.sportsCategory(team.getSportsCategory())
			.description(team.getDescription())
			.leader(leader)
			.build();
	}

	public TeamResponse toTeamResponse(Team team, UserResponse leader, List<TeamMemberResponse> teamMemberResponses,
		MatchRecordResponse.TotalCount recordCount, MatchReviewResponse.TotalCount review) {
		return TeamResponse.builder()
			.id(team.getId())
			.name(team.getName())
			.members(teamMemberResponses)
			.sportsCategory(team.getSportsCategory())
			.description(team.getDescription())
			.matchRecord(recordCount)
			.matchReview(review)
			.leader(leader)
			.build();
	}
}
