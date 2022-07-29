package com.kdt.team04.domain.team.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.kdt.team04.domain.team.entity.Team;

public interface TeamRepository extends JpaRepository<Team, Long> {

	@Query("SELECT t FROM Team t INNER JOIN TeamMember tm ON t.id = tm.team.id WHERE tm.user.id = :userId")
	List<Team> findAllByTeamMemberUserId(Long userId);
}
