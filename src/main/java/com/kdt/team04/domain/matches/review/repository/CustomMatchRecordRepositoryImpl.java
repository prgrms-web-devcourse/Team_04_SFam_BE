package com.kdt.team04.domain.matches.review.repository;

import static com.kdt.team04.domain.matches.review.model.entity.QMatchRecord.matchRecord;

import org.springframework.stereotype.Repository;

import com.kdt.team04.domain.matches.review.dto.response.MatchRecordTotalResponse;
import com.kdt.team04.domain.matches.review.model.MatchRecordValue;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

@Repository
public class CustomMatchRecordRepositoryImpl implements CustomMatchRecordRepository {

	private final JPAQueryFactory queryFactory;

	public CustomMatchRecordRepositoryImpl(JPAQueryFactory queryFactory) {
		this.queryFactory = queryFactory;
	}

	@Override
	public MatchRecordTotalResponse getTeamTotalCount(Long teamId) {
		return queryFactory
			.select(Projections.constructor(MatchRecordTotalResponse.class,
				matchRecord.result.when(MatchRecordValue.WIN).then(1).otherwise(0).sum(),
				matchRecord.result.when(MatchRecordValue.DRAW).then(1).otherwise(0).sum(),
				matchRecord.result.when(MatchRecordValue.LOSE).then(1).otherwise(0).sum()
			))
			.from(matchRecord)
			.where(matchRecord.team.id.eq(teamId))
			.fetchOne();
	}
}
