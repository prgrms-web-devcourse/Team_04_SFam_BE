package com.kdt.team04.domain.teams.team.dto;

import java.util.List;

import org.springframework.stereotype.Component;

import com.kdt.team04.domain.matches.review.dto.response.MatchRecordTotalResponse;
import com.kdt.team04.domain.matches.review.dto.response.MatchReviewTotalResponse;
import com.kdt.team04.domain.teams.team.dto.response.TeamResponse;
import com.kdt.team04.domain.teams.team.model.entity.Team;
import com.kdt.team04.domain.teams.teammember.dto.response.TeamMemberResponse;
import com.kdt.team04.domain.user.dto.response.UserResponse;
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
			.logoImageUrl(team.getLogoImageUrl())
			.build();
	}

	public TeamResponse toTeamResponse(Team team, UserResponse leader, List<TeamMemberResponse> teamMemberResponses,
		MatchRecordTotalResponse recordCount, MatchReviewTotalResponse review) {
		return TeamResponse.builder()
			.id(team.getId())
			.name(team.getName())
			.members(teamMemberResponses)
			.sportsCategory(team.getSportsCategory())
			.description(team.getDescription())
			.matchRecord(recordCount)
			.matchReview(review)
			.leader(leader)
			.logoImageUrl(team.getLogoImageUrl())
			.build();
	}
}
