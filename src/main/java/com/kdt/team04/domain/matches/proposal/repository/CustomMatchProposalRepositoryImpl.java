package com.kdt.team04.domain.matches.proposal.repository;

import static com.kdt.team04.domain.matches.match.model.entity.QMatch.match;
import static com.kdt.team04.domain.matches.proposal.entity.MatchProposalStatus.FIXED;
import static com.kdt.team04.domain.matches.proposal.entity.QMatchProposal.matchProposal;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.qlrm.mapper.JpaResultMapper;
import org.springframework.stereotype.Repository;

import com.kdt.team04.domain.matches.proposal.dto.QQueryMatchProposalResponse;
import com.kdt.team04.domain.matches.proposal.dto.QQueryMatchProposalSimpleResponse;
import com.kdt.team04.domain.matches.proposal.dto.QueryMatchProposalResponse;
import com.kdt.team04.domain.matches.proposal.dto.QueryMatchProposalSimpleResponse;
import com.kdt.team04.domain.matches.proposal.dto.QueryProposalChatResponse;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

@Repository
public class CustomMatchProposalRepositoryImpl implements CustomMatchProposalRepository {

	private final EntityManager em;
	private final JPAQueryFactory queryFactory;

	public CustomMatchProposalRepositoryImpl(EntityManager em, JPAQueryFactory queryFactory) {
		this.em = em;
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

	public List<QueryProposalChatResponse> findAllProposalByUserId(Long userId) {
		String sql = "SELECT"
			+ "  mp.id,"
			+ "  mp.content,"
			+ "  (CASE WHEN m.user_id = :userId THEN proposer.id ELSE author.id END) as target_id,"
			+ "  (CASE WHEN m.user_id = :userId THEN proposer.nickname ELSE author.nickname END) as target_nickname,"
			+ "  (CASE WHEN m.user_id = :userId THEN proposer.profile_image_url ELSE author.profile_image_url END) as profile_image_url,"
			+ "  mc.content as last_chat,"
			+ "  m.id as match_id"
			+ " FROM match_proposal mp"
			+ " INNER JOIN users proposer on mp.user_id = proposer.id"
			+ " INNER JOIN matches m ON mp.match_id = m.id"
			+ " INNER JOIN users author on m.user_id = author.id"
			+ " LEFT JOIN ("
			+ "  SELECT ROW_NUMBER() OVER (PARTITION BY match_proposal_id ORDER BY id DESC) AS rn, match_proposal_id, content, chatted_at"
			+ "  FROM match_chat mc"
			+ "  WHERE mc.user_id = :userId OR mc.target_id = :userId"
			+ " ) mc ON mc.match_proposal_id = mp.id"
			+ " WHERE (m.user_id = :userId OR mp.user_id = :userId)"
			+ " AND (mc.rn IS NULL OR mc.rn = 1)"
			+ " ORDER BY (CASE WHEN mc.rn IS NULL THEN mp.created_at ELSE mc.chatted_at END) DESC";

		JpaResultMapper jpaResultMapper = new JpaResultMapper();
		Query nativeQuery = em.createNativeQuery(sql)
			.setParameter("userId", userId);

		return jpaResultMapper.list(
			nativeQuery,
			QueryProposalChatResponse.class
		);
	}

	@Override
	public boolean existsByMatchIdAndUserId(Long matchId, Long userId) {
		Integer exists = queryFactory
			.selectOne()
			.from(matchProposal)
			.where(
				matchProposal.match.id.eq(matchId),
				matchProposal.user.id.eq(userId)
			).fetchFirst();

		return exists != null;
	}
}
