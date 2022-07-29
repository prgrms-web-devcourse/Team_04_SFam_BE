package com.kdt.team04.domain.matches.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kdt.team04.domain.matches.request.entity.MatchProposal;

public interface MatchProposalRepository extends JpaRepository<MatchProposal, Long> {
}
