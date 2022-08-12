package com.kdt.team04.domain.teams.team.repository;

import static com.kdt.team04.domain.teams.team.model.entity.QTeam.team;
import static com.kdt.team04.domain.teams.teammember.model.entity.QTeamMember.teamMember;

import java.util.List;

import com.kdt.team04.domain.teams.team.dto.QQueryTeamLeaderResponse;
import com.kdt.team04.domain.teams.team.dto.QueryTeamLeaderResponse;
import com.querydsl.jpa.impl.JPAQueryFactory;

public class CustomTeamRepositoryImpl implements CustomTeamRepository {

	private final JPAQueryFactory queryFactory;

	public CustomTeamRepositoryImpl(JPAQueryFactory queryFactory) {
		this.queryFactory = queryFactory;
	}

	@Override
	public List<QueryTeamLeaderResponse> findTeamLeaderByLeaderId(Long leaderId) {
		return queryFactory
			.select(new QQueryTeamLeaderResponse(
				team.id,
				team.name,
				team.sportsCategory,
				team.logoImageUrl,
				teamMember.id.count()
			))
			.from(team)
			.join(teamMember).on(team.id.eq(teamMember.team.id))
			.where(team.leader.id.eq(leaderId))
			.groupBy(team.id, team.name, team.sportsCategory, team.logoImageUrl)
			.fetch();
	}
}
