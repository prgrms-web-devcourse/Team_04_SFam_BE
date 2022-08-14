package com.kdt.team04.domain.matches.review.repository;

import com.kdt.team04.domain.matches.review.dto.response.MatchReviewTotalResponse;

public interface CustomMatchReviewRepository {
	MatchReviewTotalResponse getTeamTotalCount(Long teamId);

	MatchReviewTotalResponse getTeamTotalCountByUserId(Long userId);

	boolean existsByMatchIdAndUserId(Long matchId, Long userId);

	boolean existsByMatchIdAndTeamId(Long matchId, Long teamId);
}
