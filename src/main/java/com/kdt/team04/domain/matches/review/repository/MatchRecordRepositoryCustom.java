package com.kdt.team04.domain.matches.review.repository;

import com.kdt.team04.domain.matches.review.dto.MatchRecordResponse;

public interface MatchRecordRepositoryCustom {
	MatchRecordResponse.TotalCount getTeamTotalCount(Long teamId);
}