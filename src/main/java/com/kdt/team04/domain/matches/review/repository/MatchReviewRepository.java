package com.kdt.team04.domain.matches.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kdt.team04.domain.matches.review.entity.MatchReview;

public interface MatchReviewRepository extends JpaRepository<MatchReview, Long>, MatchReviewRepositoryCustom {
	void deleteAllByMatchId(Long matchId);
}
