package com.kdt.team04.domain.match.review.repository;

import com.kdt.team04.domain.match.review.dto.MatchRecordResponse;

public interface MatchRecordRepositoryCustom {
	MatchRecordResponse.TotalCount getTotalCount(Long teamId);
}