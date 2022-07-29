package com.kdt.team04.domain.matches.review.repository;

import static com.kdt.team04.domain.matches.review.entity.QMatchRecord.matchRecord;

import org.springframework.stereotype.Repository;

import com.kdt.team04.domain.matches.review.dto.MatchRecordResponse;
import com.kdt.team04.domain.matches.review.entity.MatchRecordValue;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

@Repository
public class MatchRecordRepositoryImpl implements MatchRecordRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	public MatchRecordRepositoryImpl(JPAQueryFactory queryFactory) {
		this.queryFactory = queryFactory;
	}

	@Override
	public MatchRecordResponse.TotalCount getTeamTotalCount(Long teamId) {
		return queryFactory
			.select(Projections.constructor(MatchRecordResponse.TotalCount.class,
				matchRecord.result.when(MatchRecordValue.WIN).then(1).otherwise(0).sum(),
				matchRecord.result.when(MatchRecordValue.DRAW).then(1).otherwise(0).sum(),
				matchRecord.result.when(MatchRecordValue.LOSE).then(1).otherwise(0).sum()
			))
			.from(matchRecord)
			.where(matchRecord.team.id.eq(teamId))
			.fetchOne();
	}
}
