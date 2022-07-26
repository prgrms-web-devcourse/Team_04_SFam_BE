package com.kdt.team04.domain.teammember.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kdt.team04.domain.teammember.entity.TeamMember;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
}
