package com.kdt.team04.domain.matches.proposal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kdt.team04.domain.matches.proposal.entity.MatchProposal;

public interface MatchProposalRepository extends JpaRepository<MatchProposal, Long>, MatchProposalRepositoryCustom {
	void deleteAllByMatchId(Long id);

	List<MatchProposal> findByMatchId(Long matchId);
}
