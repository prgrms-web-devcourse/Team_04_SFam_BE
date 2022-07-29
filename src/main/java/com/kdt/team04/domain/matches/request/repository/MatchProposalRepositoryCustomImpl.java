package com.kdt.team04.domain.matches.request.repository;

import static com.kdt.team04.domain.matches.match.entity.QMatch.match;
import static com.kdt.team04.domain.matches.request.entity.QMatchProposal.matchProposal;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.kdt.team04.domain.matches.request.dto.MatchProposalQueryDto;
import com.kdt.team04.domain.matches.request.dto.QMatchProposalQueryDto;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

@Repository
public class MatchProposalRepositoryCustomImpl implements MatchProposalRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	public MatchProposalRepositoryCustomImpl(JPAQueryFactory queryFactory) {
		this.queryFactory = queryFactory;
	}

	@Override
	public Optional<MatchProposalQueryDto> findSimpleProposalById(Long id) {
		MatchProposalQueryDto matchProposalDto = queryFactory
			.select(new QMatchProposalQueryDto(
				Expressions.asNumber(id).as("id"),
				matchProposal.status,
				matchProposal.user.id,
				match.user.id
			))
			.from(matchProposal)
			.join(match).on(matchProposal.match.id.eq(match.id))
			.where(matchProposal.id.eq(id))
			.fetchOne();

		return Optional.ofNullable(matchProposalDto);
	}
}
