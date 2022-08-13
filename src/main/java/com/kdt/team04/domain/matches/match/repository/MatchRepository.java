package com.kdt.team04.domain.matches.match.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kdt.team04.domain.matches.match.model.entity.Match;

public interface MatchRepository extends JpaRepository<Match, Long>, CustomMatchRepository {

	@Query("SELECT m FROM Match m JOIN FETCH m.user JOIN FETCH m.team WHERE m.id IN (:ids)")
	List<Match> findWithTeamAndUserByIds(@Param("ids") List<Long> ids);
}
