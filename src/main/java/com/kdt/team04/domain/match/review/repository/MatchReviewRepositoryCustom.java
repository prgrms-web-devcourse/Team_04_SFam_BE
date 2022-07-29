package com.kdt.team04.domain.match.review.repository;

import com.kdt.team04.domain.matches.review.dto.MatchReviewResponse;

public interface MatchReviewRepositoryCustom {
	MatchReviewResponse.TotalCount getTeamTotalCount(Long teamId);

	MatchReviewResponse.TotalCount getTeamTotalCountByUserId(Long userId);
}
