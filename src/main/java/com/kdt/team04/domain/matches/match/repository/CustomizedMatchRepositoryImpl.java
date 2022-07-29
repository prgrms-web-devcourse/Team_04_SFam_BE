package com.kdt.team04.domain.matches.match.repository;

import static com.kdt.team04.domain.matches.match.entity.QMatch.match;
import static com.querydsl.core.types.dsl.Expressions.asDateTime;
import static com.querydsl.core.types.dsl.Expressions.asNumber;
import static com.querydsl.core.types.dsl.MathExpressions.acos;
import static com.querydsl.core.types.dsl.MathExpressions.cos;
import static com.querydsl.core.types.dsl.MathExpressions.radians;
import static com.querydsl.core.types.dsl.MathExpressions.sin;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.kdt.team04.common.PageDto;
import com.kdt.team04.domain.matches.match.dto.MatchPagingCursor;
import com.kdt.team04.domain.matches.match.dto.MatchResponse;
import com.kdt.team04.domain.team.SportsCategory;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

public class CustomizedMatchRepositoryImpl implements CustomizedMatchRepository {

	private final JPAQueryFactory jpaQueryFactory;

	public CustomizedMatchRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
		this.jpaQueryFactory = jpaQueryFactory;
	}

	public PageDto.CursorResponse<MatchResponse.ListViewResponse, MatchPagingCursor> findByLocationPaging(
		Double latitude,
		Double longitude, PageDto.MatchCursorPageRequest pageRequest) {
		if (Objects.isNull(pageRequest)) {
			pageRequest = new PageDto.MatchCursorPageRequest(LocalDateTime.now(), Long.MAX_VALUE, 20, null, null);
		}
		Double distance = pageRequest.getDistance();
		Integer size = pageRequest.getSize();
		SportsCategory category = pageRequest.getCategory();
		Long id = pageRequest.getId();
		LocalDateTime createdAt = pageRequest.getCreatedAt();

		NumberExpression<Double> distanceExpression = asNumber(6371.0)
			.multiply(acos(cos(radians(asNumber(latitude))).multiply(cos(radians(match.location.latitude)))
				.multiply(cos(radians(match.location.longitude).subtract(radians(asNumber(longitude)))))
				.add(sin(radians(asNumber(latitude))).multiply(sin(radians(match.location.latitude))))));
		BooleanBuilder where = new BooleanBuilder();

		BooleanExpression categoryCondition = Optional.ofNullable(category)
			.map(match.sportsCategory::eq)
			.orElse(null);

		BooleanExpression distanceCondition = Optional.ofNullable(distance)
			.map(distanceExpression::lt)
			.orElse(distanceExpression.lt(100.0));

		List<MatchResponse.ListViewResponse> matches = jpaQueryFactory.select(
				Projections.constructor(MatchResponse.ListViewResponse.class,
					match.id,
					match.title,
					match.sportsCategory,
					match.matchType,
					match.content,
					distanceExpression.as("distance"),
					match.createdAt
				)
			)
			.from(match)
			.where(where.and(categoryCondition)
				.and(distanceCondition.and(match.createdAt.lt(asDateTime(createdAt)))
					.or(asDateTime(createdAt).eq(match.createdAt).and(asNumber(match.id).lt(id)))))
			.orderBy(match.createdAt.desc(), match.id.desc())
			.limit(size)
			.fetch();

		LocalDateTime nextCreatedAtCursor = matches.isEmpty() ? null : matches.get(matches.size() - 1).createdAt();
		Long nextIdCursor = matches.isEmpty() ? null : matches.get(matches.size() - 1).id();
		Boolean hasNext = hasNext(nextCreatedAtCursor, nextIdCursor, pageRequest.getCategory());

		return new PageDto.CursorResponse<>(matches, hasNext, new MatchPagingCursor(nextCreatedAtCursor, nextIdCursor));
	}

	private Boolean hasNext(LocalDateTime createdAtCursor, Long idCursor, SportsCategory sportsCategory) {
		if (createdAtCursor == null || idCursor == null) {
			return false;
		}

		BooleanExpression categoryCondition = Optional.ofNullable(sportsCategory)
			.map(match.sportsCategory::eq)
			.orElse(null);

		return jpaQueryFactory.selectFrom(match)
			.where(match.createdAt.loe(createdAtCursor)
				.and(match.id.lt(idCursor))
				.and(categoryCondition))
			.fetchFirst() != null;
	}

}
