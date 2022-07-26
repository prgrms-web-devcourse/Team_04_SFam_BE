package com.kdt.team04.domain.teammember.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kdt.team04.domain.team.entity.Team;
import com.kdt.team04.domain.teammember.entity.TeamMember;
import com.kdt.team04.domain.user.entity.User;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long>, CustomizedTeamMemberRepository {

	boolean existsByTeamAndUser(Team team, User user);
}
