package com.kdt.team04.domain.matches.proposal.repository;

import java.util.List;

import com.kdt.team04.domain.matches.proposal.dto.QueryMatchChatPartitionByProposalIdResponse;

public interface CustomMatchChatRepository {

	List<QueryMatchChatPartitionByProposalIdResponse> findAllPartitionByProposalIdOrderByChattedAtDesc(List<Long> matchProposalIds);
}
