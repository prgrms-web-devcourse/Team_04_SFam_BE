package com.kdt.team04.domain.matches.proposal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kdt.team04.domain.matches.proposal.entity.MatchChat;
import com.kdt.team04.domain.matches.proposal.entity.MatchProposal;

public interface MatchChatRepository extends JpaRepository<MatchChat, Long> {
	void deleteAllByProposalIn(List<MatchProposal> proposals);
}
