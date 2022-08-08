package com.kdt.team04.domain.matches.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kdt.team04.domain.matches.review.model.entity.MatchRecord;

public interface MatchRecordRepository extends JpaRepository<MatchRecord, Long>, CustomMatchRecordRepository {
	void deleteAllByMatchId(Long matchId);
}
