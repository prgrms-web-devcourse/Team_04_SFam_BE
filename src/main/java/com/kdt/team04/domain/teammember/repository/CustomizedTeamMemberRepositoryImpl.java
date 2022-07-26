package com.kdt.team04.domain.teammember.repository;

import static com.kdt.team04.domain.team.entity.QTeam.team;
import static com.kdt.team04.domain.teammember.entity.QTeamMember.teamMember;
import static com.kdt.team04.domain.user.entity.QUser.user;

import com.querydsl.jpa.impl.JPAQueryFactory;

public class CustomizedTeamMemberRepositoryImpl implements CustomizedTeamMemberRepository {

	private final JPAQueryFactory jpaQueryFactory;

	public CustomizedTeamMemberRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
		this.jpaQueryFactory = jpaQueryFactory;
	}

	@Override
	public boolean existsByTeamIdAndMemberId(Long teamId, Long userId) {
		return jpaQueryFactory.selectFrom(teamMember)
			.join(teamMember.user, user).fetchJoin()
			.join(teamMember.team, team).fetchJoin()
			.where(team.id.eq(teamId).and(user.id.eq(userId)))
			.fetchFirst() != null;
	}
}
