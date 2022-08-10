package com.kdt.team04.domain.teams.teaminvitation.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kdt.team04.domain.teams.teaminvitation.model.InvitationStatus;
import com.kdt.team04.domain.teams.teaminvitation.model.entity.TeamInvitation;

public interface TeamInvitationRepository extends JpaRepository<TeamInvitation, Long>, CustomTeamInvitationRepository {
	boolean existsByIdAndStatus(Long teamid, InvitationStatus status);

	Optional<TeamInvitation> findByIdAndTeamId(Long id, Long teamId);
}
