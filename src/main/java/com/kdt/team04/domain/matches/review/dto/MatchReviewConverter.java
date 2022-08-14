package com.kdt.team04.domain.matches.review.dto;

import org.springframework.stereotype.Component;

import com.kdt.team04.domain.matches.match.model.entity.Match;
import com.kdt.team04.domain.matches.review.model.entity.MatchReview;
import com.kdt.team04.domain.matches.review.model.MatchReviewValue;
import com.kdt.team04.domain.teams.team.model.entity.Team;
import com.kdt.team04.domain.user.entity.User;

@Component
public class MatchReviewConverter {

	public MatchReview toTeamReview(
		Long matchId,
		MatchReviewValue review,
		Long userId,
		Long teamId,
		Long targetTeamId
	) {
		Match match = Match.builder().id(matchId).build();
		Team team = Team.builder().id(teamId).build();
		User user = User.builder().id(userId).build();
		Team target = Team.builder().id(targetTeamId).build();

		return MatchReview.builder()
			.match(match)
			.review(review)
			.team(team)
			.user(user)
			.targetTeam(target)
			.build();
	}

	public MatchReview toIndividualReview(
		Long matchId,
		MatchReviewValue review,
		Long userId,
		Long targetId
	) {
		Match match = Match.builder().id(matchId).build();
		User user = User.builder().id(userId).build();
		User target = User.builder().id(targetId).build();

		return MatchReview.builder()
			.match(match)
			.review(review)
			.user(user)
			.targetUser(target)
			.build();
	}
}
