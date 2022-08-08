package com.kdt.team04.domain.teams.teaminvitation.repository;

import static com.kdt.team04.domain.teaminvitation.model.QTeamInvitation.teamInvitation;
import static com.querydsl.core.types.dsl.Expressions.asDateTime;
import static com.querydsl.core.types.dsl.Expressions.asNumber;

import java.time.LocalDateTime;
import java.util.List;

import com.kdt.team04.common.PageDto;
import com.kdt.team04.domain.teams.teaminvitation.dto.TeamInvitationCursor;
import com.kdt.team04.domain.teams.teaminvitation.dto.response.TeamInvitationResponse;
import com.kdt.team04.domain.teams.teaminvitation.model.InvitationStatus;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

public class TeamInvitationRepositoryCustomImpl implements TeamInvitationRepositoryCustom {

	private final JPAQueryFactory jpaQueryFactory;

	public TeamInvitationRepositoryCustomImpl(JPAQueryFactory jpaQueryFactory) {
		this.jpaQueryFactory = jpaQueryFactory;
	}

	@Override
	public PageDto.CursorResponse<TeamInvitationResponse, TeamInvitationCursor> getInvitations(Long targetId,
		PageDto.TeamInvitationCursorPageRequest request) {
		Long lastId = request.getId();
		LocalDateTime createdAt = request.getCreatedAt();

		BooleanBuilder builder = new BooleanBuilder();

		BooleanExpression fixCondition = teamInvitation.target.id.eq(targetId)
			.and(teamInvitation.status.eq(request.getStatus()));

		BooleanExpression cursorCondition = (createdAt == null || lastId == null) ? null
			: teamInvitation
			.createdAt.lt(asDateTime(createdAt))
			.or(
				asDateTime(createdAt).eq(teamInvitation.createdAt)
					.and(asNumber(teamInvitation.id).lt(lastId))
			);

		List<TeamInvitationResponse> responses = jpaQueryFactory
			.select(
				Projections.constructor(TeamInvitationResponse.class,
					teamInvitation.id,
					teamInvitation.team.id,
					teamInvitation.team.name,
					teamInvitation.createdAt)
			)
			.from(teamInvitation)
			.where(builder.and(fixCondition)
				.and(cursorCondition))
			.orderBy(teamInvitation.createdAt.desc(), teamInvitation.id.desc())
			.limit(request.getSize())
			.fetch();

		LocalDateTime nextCreateAtCursor = responses.isEmpty() ? null
			: responses.get(responses.size() - 1).createdAt();

		Long nextCursorId = responses.isEmpty() ? null
			: responses.get(responses.size() - 1).invitationId();

		Boolean hasNext = hasNext(nextCreateAtCursor, nextCursorId, targetId);

		return new PageDto.CursorResponse<>(responses, hasNext,
			new TeamInvitationCursor(nextCreateAtCursor, nextCursorId));
	}

	private Boolean hasNext(LocalDateTime createdAt, Long lastId, Long targetId) {
		if (createdAt == null || lastId == null) {
			return false;
		}

		BooleanExpression condition = teamInvitation.target.id.eq(targetId)
			.and(teamInvitation.status.eq(InvitationStatus.WAITING))
			.and(teamInvitation.createdAt.loe(createdAt))
			.and(teamInvitation.id.lt(lastId));

		return jpaQueryFactory.selectFrom(teamInvitation)
			.where(condition)
			.fetchFirst() != null;
	}

}
