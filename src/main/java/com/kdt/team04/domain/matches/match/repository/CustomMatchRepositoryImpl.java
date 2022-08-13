package com.kdt.team04.domain.matches.match.repository;

import static com.kdt.team04.domain.matches.match.model.entity.QMatch.match;
import static com.kdt.team04.domain.user.entity.QUser.user;
import static com.querydsl.core.types.dsl.Expressions.asDate;
import static com.querydsl.core.types.dsl.Expressions.asNumber;
import static com.querydsl.core.types.dsl.MathExpressions.acos;
import static com.querydsl.core.types.dsl.MathExpressions.cos;
import static com.querydsl.core.types.dsl.MathExpressions.radians;
import static com.querydsl.core.types.dsl.MathExpressions.sin;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.kdt.team04.common.PageDto;
import com.kdt.team04.common.SearchDateType;
import com.kdt.team04.domain.matches.match.dto.MatchPagingCursor;
import com.kdt.team04.domain.matches.match.dto.response.MatchListViewResponse;
import com.kdt.team04.domain.matches.match.model.MatchStatus;
import com.kdt.team04.domain.teams.team.model.SportsCategory;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

public class CustomMatchRepositoryImpl implements CustomMatchRepository {

	private final JPAQueryFactory jpaQueryFactory;

	public CustomMatchRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
		this.jpaQueryFactory = jpaQueryFactory;
	}

	public PageDto.CursorResponse<MatchListViewResponse, MatchPagingCursor> findByLocationPaging(
		Double latitude, Double longitude, PageDto.MatchCursorPageRequest pageRequest) {
		Double distance = pageRequest.getDistance();
		Integer size = pageRequest.getSize();
		SportsCategory category = pageRequest.getCategory();
		SearchDateType searchDateType = pageRequest.getSearchDateType();
		Long id = switch (searchDateType) {
			case MATCH_DATE -> (pageRequest.getId() == null) ? 0L : pageRequest.getId();
			case CREATED_AT -> (pageRequest.getId() == null) ? Long.MAX_VALUE : pageRequest.getId();
		};
		LocalDateTime createdAt = pageRequest.getCreatedAt();
		LocalDate matchDate = pageRequest.getMatchDate();
		MatchStatus status = pageRequest.getStatus();
		Long userId = pageRequest.getUserId();

		NumberExpression<Double> distanceExpression = asNumber(6371.0)
			.multiply(acos(cos(radians(asNumber(latitude))).multiply(cos(radians(match.location.latitude)))
				.multiply(cos(radians(match.location.longitude).subtract(radians(asNumber(longitude)))))
				.add(sin(radians(asNumber(latitude))).multiply(sin(radians(match.location.latitude))))));

		BooleanBuilder conditions = new BooleanBuilder();

		BooleanExpression distanceCondition = Optional.ofNullable(distance)
			.map(distanceExpression::lt)
			.orElse(distanceExpression.lt(40.0));

		BooleanExpression distanceOrUserIdCondition = Optional.ofNullable(userId)
			.map(match.user.id::eq)
			.orElse(distanceCondition);

		OrderSpecifier<?> dateOrderSpecifier = null;
		OrderSpecifier<?> matchIdOrderSpecifier = null;
		BooleanExpression cursorCondition = switch (searchDateType) {
			case MATCH_DATE -> {
				dateOrderSpecifier = match.matchDate.asc();
				matchIdOrderSpecifier = match.id.asc();
				yield Optional.ofNullable(matchDate)
					.map(cursor -> match.matchDate.gt(cursor)
						.or(asDate(cursor)
							.eq(match.matchDate)
							.and(asNumber(match.id).gt(id))
						)
					)
					.orElse(null);
			}
			case CREATED_AT -> {
				dateOrderSpecifier = match.createdAt.desc();
				matchIdOrderSpecifier = match.id.desc();
				yield Optional.ofNullable(createdAt)
					.map(cursor -> match.createdAt.lt(cursor)
						.or(asDate(cursor)
							.eq(match.createdAt)
							.and(asNumber(match.id).lt(id))
						)
					)
					.orElse(null);
			}
		};

		List<MatchListViewResponse> matches = jpaQueryFactory.select(
				Projections.constructor(MatchListViewResponse.class,
					match.id,
					match.title,
					match.sportsCategory,
					match.matchType,
					match.content,
					match.user.id,
					match.user.nickname,
					distanceExpression.as("distance"),
					match.matchDate,
					match.createdAt
				)
			)
			.from(match)
			.leftJoin(match.user, user)
			.where(conditions.and(distanceOrUserIdCondition)
				.and(categoryEq(category))
				.and(statusEq(status))
				.and(cursorCondition))
			.orderBy(dateOrderSpecifier, matchIdOrderSpecifier)
			.limit(size)
			.fetch();

		LocalDateTime nextCreatedAtCursor = matches.isEmpty() ? null : matches.get(matches.size() - 1).createdAt();
		LocalDate nextMatchDateCursor = matches.isEmpty() ? null : matches.get(matches.size() - 1).matchDate();
		Long nextIdCursor = matches.isEmpty() ? null : matches.get(matches.size() - 1).id();
		Boolean hasNext = switch (searchDateType) {
			case MATCH_DATE -> hasNext(nextMatchDateCursor, nextIdCursor, category, status);
			case CREATED_AT -> hasNext(nextCreatedAtCursor, nextIdCursor, category, status);
		};

		return new PageDto.CursorResponse<>(matches, hasNext,
			new MatchPagingCursor(nextCreatedAtCursor, nextMatchDateCursor, nextIdCursor));
	}

	private Boolean hasNext(LocalDate matchDateCursor, Long idCursor, SportsCategory sportsCategory,
		MatchStatus status) {
		if (matchDateCursor == null || idCursor == null) {
			return false;
		}

		BooleanBuilder conditions = new BooleanBuilder();

		return jpaQueryFactory.selectFrom(match)
			.where(conditions.and(categoryEq(sportsCategory))
				.and(statusEq(status))
				.and(match.matchDate
					.loe(matchDateCursor)
					.and(match.id.gt(idCursor)
					)
				)
			)
			.fetchFirst() != null;
	}

	private Boolean hasNext(LocalDateTime createdAtCursor, Long idCursor, SportsCategory sportsCategory,
		MatchStatus status) {
		if (createdAtCursor == null || idCursor == null) {
			return false;
		}

		BooleanBuilder conditions = new BooleanBuilder();

		return jpaQueryFactory.selectFrom(match)
			.where(conditions.and(categoryEq(sportsCategory))
				.and(statusEq(status))
				.and(match.createdAt
					.loe(createdAtCursor)
					.and(match.id.lt(idCursor)
					)
				)
			)
			.fetchFirst() != null;
	}

	private BooleanExpression categoryEq(SportsCategory sportsCategory) {
		return Optional.ofNullable(sportsCategory)
			.map(match.sportsCategory::eq)
			.orElse(null);
	}

	private BooleanExpression statusEq(MatchStatus status) {
		return Optional.ofNullable(status)
			.map(match.status::eq)
			.orElse(null);
	}

	@Override
	public Double getDistance(Double latitude, Double longitude, Long matchId) {
		NumberExpression<Double> distanceExpression = asNumber(6371.0)
			.multiply(acos(cos(radians(asNumber(latitude))).multiply(cos(radians(match.location.latitude)))
				.multiply(cos(radians(match.location.longitude).subtract(radians(asNumber(longitude)))))
				.add(sin(radians(asNumber(latitude))).multiply(sin(radians(match.location.latitude))))));

		return jpaQueryFactory.select(distanceExpression)
			.from(match)
			.where(match.id.eq(matchId))
			.fetchOne();
	}
}