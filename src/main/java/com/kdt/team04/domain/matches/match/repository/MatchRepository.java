package com.kdt.team04.domain.matches.match.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kdt.team04.domain.matches.match.model.entity.Match;

public interface MatchRepository extends JpaRepository<Match, Long>, CustomMatchRepository {
}
