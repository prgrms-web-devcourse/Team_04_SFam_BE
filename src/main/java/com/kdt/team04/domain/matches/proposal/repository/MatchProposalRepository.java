package com.kdt.team04.domain.matches.proposal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kdt.team04.domain.matches.proposal.entity.MatchProposal;

public interface MatchProposalRepository extends JpaRepository<MatchProposal, Long>, MatchProposalRepositoryCustom {

	@Query("SELECT mp FROM MatchProposal mp JOIN FETCH User u ON mp.user.id = u.id INNER JOIN Match m ON mp.match.id = m.id WHERE mp.match.id = :matchId")
	List<MatchProposal> findAllByMatchId(@Param("matchId") Long matchId);

	@Query("SELECT mp FROM MatchProposal mp JOIN FETCH User u ON mp.user.id = u.id WHERE mp.id = :id")
	Optional<MatchProposal> findMatchProposalById(@Param("id") Long id);
}
