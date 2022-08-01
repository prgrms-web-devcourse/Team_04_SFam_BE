package com.kdt.team04.domain.matches.proposal.repository;

import static com.kdt.team04.domain.matches.match.entity.QMatch.match;
import static com.kdt.team04.domain.matches.proposal.entity.MatchProposalStatus.FIXED;
import static com.kdt.team04.domain.matches.proposal.entity.QMatchProposal.matchProposal;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.kdt.team04.domain.matches.proposal.dto.MatchProposalQueryDto;
import com.kdt.team04.domain.matches.proposal.dto.MatchProposalSimpleQueryDto;
import com.kdt.team04.domain.matches.proposal.dto.QMatchProposalQueryDto;
import com.kdt.team04.domain.matches.proposal.dto.QMatchProposalSimpleQueryDto;
import com.kdt.team04.domain.matches.proposal.entity.MatchProposalStatus;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

@Repository
public class MatchProposalRepositoryCustomImpl implements MatchProposalRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	public MatchProposalRepositoryCustomImpl(JPAQueryFactory queryFactory) {
		this.queryFactory = queryFactory;
	}

	@Override
	public Optional<MatchProposalSimpleQueryDto> findSimpleProposalById(Long id) {
		MatchProposalSimpleQueryDto matchProposalDto = queryFactory
			.select(new QMatchProposalSimpleQueryDto(
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
	public Optional<MatchProposalQueryDto> findFixedProposalByMatchId(Long matchId) {
		MatchProposalQueryDto matchProposalDto = queryFactory
			.select(new QMatchProposalQueryDto(
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
