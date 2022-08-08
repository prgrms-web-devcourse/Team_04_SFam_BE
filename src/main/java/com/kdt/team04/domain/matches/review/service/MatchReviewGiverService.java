package com.kdt.team04.domain.matches.review.service;

import org.springframework.stereotype.Service;

import com.kdt.team04.domain.matches.review.dto.response.MatchReviewTotalResponse;
import com.kdt.team04.domain.matches.review.repository.MatchReviewRepository;

@Service
public class MatchReviewGiverService {

	private final MatchReviewRepository matchReviewRepository;

	public MatchReviewGiverService(MatchReviewRepository matchReviewRepository) {
		this.matchReviewRepository = matchReviewRepository;
	}

	public MatchReviewTotalResponse findByTeamTotalReview(Long teamId) {
		return matchReviewRepository.getTeamTotalCount(teamId);
	}

	public MatchReviewTotalResponse findTotalReviewByUserId(Long userId) {
		return matchReviewRepository.getTeamTotalCountByUserId(userId);
	}
}
