package com.kdt.team04.domain.matches.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kdt.team04.domain.matches.review.model.entity.MatchReview;

public interface MatchReviewRepository extends JpaRepository<MatchReview, Long>, CustomMatchReviewRepository {
	void deleteAllByMatchId(Long matchId);
}
