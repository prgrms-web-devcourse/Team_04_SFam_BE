package com.kdt.team04.domain.matches.review.repository;

import com.kdt.team04.domain.matches.review.dto.MatchReviewResponse;

public interface MatchReviewRepositoryCustom {
	MatchReviewResponse.TotalCount getTeamTotalCount(Long teamId);

	MatchReviewResponse.TotalCount getTeamTotalCountByUserId(Long userId);

	boolean existsByMatchIdAndUserId(Long matchId, Long userId);
}
