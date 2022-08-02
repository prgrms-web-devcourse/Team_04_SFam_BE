package com.kdt.team04.domain.matches.proposal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kdt.team04.domain.matches.proposal.entity.MatchProposal;

public interface MatchProposalRepository extends JpaRepository<MatchProposal, Long>, MatchProposalRepositoryCustom {

	@Query("SELECT mp FROM MatchProposal mp JOIN FETCH mp.user JOIN FETCH mp.match WHERE mp.match.id = :matchId")
	List<MatchProposal> findAllByMatchId(@Param("matchId") Long matchId);

	@Query("SELECT mp FROM MatchProposal mp JOIN FETCH mp.user WHERE mp.id = :id")
	Optional<MatchProposal> findProposalWithUserById(@Param("id") Long id);
}
