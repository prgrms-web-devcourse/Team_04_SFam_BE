package com.kdt.team04.domain.matches.proposal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kdt.team04.domain.matches.proposal.entity.MatchProposal;

public interface MatchProposalRepository extends JpaRepository<MatchProposal, Long>, CustomMatchProposalRepository {

	void deleteAllByMatchId(Long id);

	@Query("SELECT mp FROM MatchProposal mp JOIN FETCH mp.user JOIN FETCH mp.match WHERE mp.match.id = :matchId")
	List<MatchProposal> findAllByMatchId(@Param("matchId") Long matchId);

	@Query("SELECT mp FROM MatchProposal mp JOIN FETCH mp.user WHERE mp.id = :id")
	Optional<MatchProposal> findProposalWithUserById(@Param("id") Long id);

	@Query("SELECT mp FROM MatchProposal mp JOIN FETCH mp.user LEFT JOIN FETCH mp.team WHERE mp.id = :id")
	Optional<MatchProposal> findProposalById(@Param("id") Long id);

	@Query("SELECT mp FROM MatchProposal mp INNER JOIN Match m ON mp.match.id = m.id WHERE mp.id = :id AND (m.user.id = :userId OR mp.user.id = :userId)")
	Optional<MatchProposal> findProposalWithMatchByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);

	@Query("SELECT mp FROM MatchProposal mp WHERE mp.match.id = :matchId AND mp.user.id = :userId")
	Optional<MatchProposal> findByMatchIdAndUserId(@Param("matchId") Long matchId, @Param("userId") Long userId);
}
