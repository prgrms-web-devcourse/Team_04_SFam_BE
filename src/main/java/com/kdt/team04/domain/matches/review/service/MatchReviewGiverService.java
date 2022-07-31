package com.kdt.team04.domain.matches.review.service;

import org.springframework.stereotype.Service;

import com.kdt.team04.domain.matches.review.dto.MatchReviewResponse;
import com.kdt.team04.domain.matches.review.repository.MatchReviewRepository;

@Service
public class MatchReviewGiverService {

	private final MatchReviewRepository matchReviewRepository;

	public MatchReviewGiverService(MatchReviewRepository matchReviewRepository) {
		this.matchReviewRepository = matchReviewRepository;
	}

	public MatchReviewResponse.TotalCount findByTeamTotalReview(Long teamId) {
		return matchReviewRepository.getTeamTotalCount(teamId);
	}

	public MatchReviewResponse.TotalCount findTotalReviewByUserId(Long userId) {
		return matchReviewRepository.getTeamTotalCountByUserId(userId);
	}

	public void deleteByMatchId(Long matchId) {
		matchReviewRepository.deleteAllByMatchId(matchId);
	}
}
