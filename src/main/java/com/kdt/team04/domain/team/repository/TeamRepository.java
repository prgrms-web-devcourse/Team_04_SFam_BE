package com.kdt.team04.domain.team.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kdt.team04.domain.team.entity.Team;

public interface TeamRepository extends JpaRepository<Team, Long> {
}
