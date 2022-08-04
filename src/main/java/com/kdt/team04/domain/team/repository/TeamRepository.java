package com.kdt.team04.domain.team.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kdt.team04.domain.team.entity.Team;

public interface TeamRepository extends JpaRepository<Team, Long> {

	List<Team> findAllByLeaderId(Long leaderId);

	@Query("SELECT t FROM Team t INNER JOIN TeamMember tm ON t.id = tm.team.id WHERE tm.user.id = :userId")
	List<Team> findAllByTeamMemberUserId(@Param("userId") Long userId);

	Optional<Team> findByIdAndLeaderId(Long id, Long leaderId);
}
