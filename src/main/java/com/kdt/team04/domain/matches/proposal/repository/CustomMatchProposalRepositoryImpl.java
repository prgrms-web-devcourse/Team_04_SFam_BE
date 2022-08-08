package com.kdt.team04.domain.matches.proposal.repository;

import static com.kdt.team04.domain.matches.match.model.QMatch.match;
import static com.kdt.team04.domain.matches.proposal.entity.MatchProposalStatus.FIXED;
import static com.kdt.team04.domain.matches.proposal.entity.QMatchProposal.matchProposal;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.kdt.team04.domain.matches.proposal.dto.QQueryMatchProposalResponse;
import com.kdt.team04.domain.matches.proposal.dto.QQueryMatchProposalSimpleResponse;
import com.kdt.team04.domain.matches.proposal.dto.QueryMatchProposalResponse;
import com.kdt.team04.domain.matches.proposal.dto.QueryMatchProposalSimpleResponse;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

@Repository
public class CustomMatchProposalRepositoryImpl implements CustomMatchProposalRepository {

	private final JPAQueryFactory queryFactory;

	public CustomMatchProposalRepositoryImpl(JPAQueryFactory queryFactory) {
		this.queryFactory = queryFactory;
	}

	@Override
	public Optional<QueryMatchProposalSimpleResponse> findSimpleProposalById(Long id) {
		QueryMatchProposalSimpleResponse matchProposalDto = queryFactory
			.select(new QQueryMatchProposalSimpleResponse(
				Expressions.asNumber(id).as("id"),
				matchProposal.status,
				matchProposal.user.id,
				match.user.id,
				match.status
			))
			.from(matchProposal)
			.join(match).on(matchProposal.match.id.eq(match.id))
			.where(matchProposal.id.eq(id))
			.fetchOne();

		return Optional.ofNullable(matchProposalDto);
	}

	@Override
	public Optional<QueryMatchProposalResponse> findFixedProposalByMatchId(Long matchId) {
		QueryMatchProposalResponse matchProposalDto = queryFactory
			.select(new QQueryMatchProposalResponse(
				matchProposal.id,
				matchProposal.user.id,
				matchProposal.team.id,
				Expressions.constant(matchId),
				match.status,
				match.matchType,
				match.user.id,
				match.team.id
			))
			.from(matchProposal)
			.join(match).on(matchProposal.match.id.eq(match.id))
			.where(
				match.id.eq(matchId),
				matchProposal.status.eq(FIXED)
			)
			.fetchOne();

		return Optional.ofNullable(matchProposalDto);
	}
}
