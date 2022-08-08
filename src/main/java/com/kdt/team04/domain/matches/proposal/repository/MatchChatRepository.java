package com.kdt.team04.domain.matches.proposal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kdt.team04.domain.matches.proposal.entity.MatchChat;
import com.kdt.team04.domain.matches.proposal.entity.MatchProposal;

public interface MatchChatRepository extends JpaRepository<MatchChat, Long>, CustomMatchChatRepository {
	void deleteAllByProposalIn(List<MatchProposal> proposals);

	@Query("SELECT mc FROM MatchChat mc WHERE mc.proposal.id = :proposalId")
	List<MatchChat> findAllByProposalId(@Param("proposalId") Long proposalId);
}
