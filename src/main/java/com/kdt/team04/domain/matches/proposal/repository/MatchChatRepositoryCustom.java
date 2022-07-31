package com.kdt.team04.domain.matches.proposal.repository;

import java.util.List;

import com.kdt.team04.domain.matches.proposal.dto.MatchChatPartitionByProposalIdQueryDto;

public interface MatchChatRepositoryCustom {

	List<MatchChatPartitionByProposalIdQueryDto> findAllPartitionByProposalIdOrderByChattedAtDesc(List<Long> matchProposalIds);
}
