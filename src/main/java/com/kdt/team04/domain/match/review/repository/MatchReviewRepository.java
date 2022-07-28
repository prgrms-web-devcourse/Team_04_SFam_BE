package com.kdt.team04.domain.match.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kdt.team04.domain.match.review.entity.MatchReview;

public interface MatchReviewRepository extends JpaRepository<MatchReview, Long>, MatchReviewRepositoryCustom {
}
