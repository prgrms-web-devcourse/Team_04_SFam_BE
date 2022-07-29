package com.kdt.team04.domain.matches.match.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kdt.team04.domain.matches.match.entity.Match;
import com.kdt.team04.domain.team.SportsCategory;

public interface MatchRepository extends JpaRepository<Match, Long> ,CustomizedMatchRepository {
	Boolean existsByCreatedAtLessThanEqualAndIdLessThanAndSportsCategory(LocalDateTime cursorCreatedAt, Long cursorId,
		SportsCategory sportsCategory);
}
