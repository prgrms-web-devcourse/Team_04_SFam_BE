package com.kdt.team04.domain.teammember.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kdt.team04.domain.team.entity.Team;
import com.kdt.team04.domain.teammember.entity.TeamMember;
import com.kdt.team04.domain.user.entity.User;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long>, CustomizedTeamMemberRepository {
	boolean existsByTeamAndUser(Team team, User user);

	@Query("SELECT tm FROM TeamMember tm JOIN FETCH tm.team t JOIN FETCH tm.user u WHERE t.id = :teamId")
	List<TeamMember> findAllByTeamId(@Param("teamId") Long teamId);
}
