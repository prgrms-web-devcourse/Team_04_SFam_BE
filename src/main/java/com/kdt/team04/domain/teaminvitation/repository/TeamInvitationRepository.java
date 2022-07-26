package com.kdt.team04.domain.teaminvitation.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kdt.team04.domain.teaminvitation.entity.TeamInvitation;

public interface TeamInvitationRepository extends JpaRepository<TeamInvitation, Long> {
}
