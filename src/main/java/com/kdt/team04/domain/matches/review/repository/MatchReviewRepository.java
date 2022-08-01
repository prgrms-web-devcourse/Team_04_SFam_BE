package com.kdt.team04.domain.matches.review.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kdt.team04.domain.matches.review.entity.MatchReview;

public interface MatchReviewRepository extends JpaRepository<MatchReview, Long>, MatchReviewRepositoryCustom {

}
