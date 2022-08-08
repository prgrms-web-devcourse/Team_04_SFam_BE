package com.kdt.team04.domain.matches.proposal.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.qlrm.mapper.JpaResultMapper;
import org.springframework.stereotype.Repository;

import com.kdt.team04.domain.matches.proposal.dto.QueryMatchChatPartitionByProposalIdResponse;

@Repository
public class CustomMatchChatRepositoryImpl implements CustomMatchChatRepository {

	private final EntityManager em;

	public CustomMatchChatRepositoryImpl(EntityManager em) {
		this.em = em;
	}

	@Override
	public List<QueryMatchChatPartitionByProposalIdResponse> findAllPartitionByProposalIdOrderByChattedAtDesc(List<Long> matchProposalIds) {
		String sql =
			"SELECT ROW_NUMBER() OVER (PARTITION BY match_proposal_id ORDER BY id DESC), match_proposal_id, content" +
			" FROM match_chat mc" +
			" WHERE mc.match_proposal_id IN (:matchProposalIds)"
			;

		JpaResultMapper jpaResultMapper = new JpaResultMapper();
		Query nativeQuery = em.createNativeQuery(sql)
			.setParameter("matchProposalIds", matchProposalIds);

		return jpaResultMapper.list(
			nativeQuery,
			QueryMatchChatPartitionByProposalIdResponse.class
		);
	}

}
