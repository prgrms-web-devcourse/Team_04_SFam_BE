package com.kdt.team04.domain.match.review.service;

import org.springframework.stereotype.Service;

import com.kdt.team04.domain.match.review.dto.MatchRecordResponse;
import com.kdt.team04.domain.match.review.repository.MatchRecordRepository;

@Service
public class MatchRecordGiverService {

	private final MatchRecordRepository matchRecordRepository;

	public MatchRecordGiverService(MatchRecordRepository matchRecordRepository) {
		this.matchRecordRepository = matchRecordRepository;
	}

	public MatchRecordResponse.TotalCount findByTeamTotalRecord(Long id) {
		return matchRecordRepository.getTeamTotalCount(id);
	}
}