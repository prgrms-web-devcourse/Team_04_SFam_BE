package com.kdt.team04.domain.matches.review.dto;

import org.springframework.stereotype.Component;

import com.kdt.team04.domain.matches.match.model.entity.Match;
import com.kdt.team04.domain.matches.review.model.entity.MatchRecord;
import com.kdt.team04.domain.matches.review.model.MatchRecordValue;
import com.kdt.team04.domain.teams.team.model.entity.Team;
import com.kdt.team04.domain.user.entity.User;

@Component
public class MatchRecordConverter {

	public MatchRecord toRecord(Long matchId, Long userId, Long teamId, MatchRecordValue result) {
		Match match = Match.builder().id(matchId).build();
		User user = User.builder().id(userId).build();
		Team team = Team.builder().id(teamId).build();

		return MatchRecord.builder()
			.match(match)
			.user(user)
			.team(team)
			.result(result)
			.build();
	}

	public MatchRecord toRecord(Long matchId, Long userId, MatchRecordValue result) {
		Match match = Match.builder().id(matchId).build();
		User user = User.builder().id(userId).build();

		return MatchRecord.builder()
			.match(match)
			.user(user)
			.result(result)
			.build();
	}
}
