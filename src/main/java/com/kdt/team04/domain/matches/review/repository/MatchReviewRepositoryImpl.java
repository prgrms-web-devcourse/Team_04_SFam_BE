package com.kdt.team04.domain.matches.review.repository;

import static com.kdt.team04.domain.matches.review.entity.QMatchReview.matchReview;

import org.springframework.stereotype.Repository;

import com.kdt.team04.domain.matches.review.dto.MatchReviewResponse;
import com.kdt.team04.domain.matches.review.entity.MatchReviewValue;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

@Repository
public class MatchReviewRepositoryImpl implements MatchReviewRepositoryCustom {

	private final JPAQueryFactory jpaQueryFactory;

	public MatchReviewRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
		this.jpaQueryFactory = jpaQueryFactory;
	}

	@Override
	public MatchReviewResponse.TotalCount getTeamTotalCount(Long teamId) {
		return jpaQueryFactory
			.select(Projections.constructor(MatchReviewResponse.TotalCount.class,
				matchReview.review.when(MatchReviewValue.BEST).then(1).otherwise(0).sum(),
				matchReview.review.when(MatchReviewValue.LIKE).then(1).otherwise(0).sum(),
				matchReview.review.when(MatchReviewValue.DISLIKE).then(1).otherwise(0).sum()
			))
			.from(matchReview)
			.where(matchReview.targetTeam.id.eq(teamId))
			.fetchOne();
	}
}