package com.kdt.team04.domain.teams.teammember.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kdt.team04.domain.teams.teammember.model.entity.TeamMember;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {

	boolean existsByTeamIdAndUserId(Long teamId, Long userId);

	@Query("SELECT tm FROM TeamMember tm JOIN FETCH tm.team t JOIN FETCH tm.user u WHERE t.id = :teamId")
	List<TeamMember> findAllByTeamId(@Param("teamId") Long teamId);

	int countAllByTeamId(Long teamId);
}
