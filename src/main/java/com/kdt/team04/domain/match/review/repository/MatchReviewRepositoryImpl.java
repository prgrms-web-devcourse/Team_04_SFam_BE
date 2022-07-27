package com.kdt.team04.domain.match.review.repository;

import org.springframework.stereotype.Repository;

import com.kdt.team04.domain.match.review.dto.MatchReviewResponse;
import com.kdt.team04.domain.match.review.entity.MatchReviewValue;
import com.kdt.team04.domain.match.review.entity.QMatchReview;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

@Repository
public class MatchReviewRepositoryImpl implements MatchReviewRepositoryCustom {

	private final JPAQueryFactory jpaQueryFactory;

	public MatchReviewRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
		this.jpaQueryFactory = jpaQueryFactory;
	}

	@Override
	public MatchReviewResponse.TotalCount getTotalCount(Long teamId) {
		return jpaQueryFactory.select(Projections.constructor(MatchReviewResponse.TotalCount.class,
				QMatchReview.matchReview.review.when(MatchReviewValue.BEST).then(1).otherwise(0),
				QMatchReview.matchReview.review.when(MatchReviewValue.LIKE).then(1).otherwise(0),
				QMatchReview.matchReview.review.when(MatchReviewValue.DISLIKE).then(1).otherwise(0)))
			.from(QMatchReview.matchReview)
			.where(QMatchReview.matchReview.id.eq(teamId))
			.fetchOne();
	}
}
