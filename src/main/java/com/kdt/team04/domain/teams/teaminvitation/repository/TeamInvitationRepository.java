package com.kdt.team04.domain.teams.teaminvitation.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kdt.team04.domain.teams.teaminvitation.model.InvitationStatus;
import com.kdt.team04.domain.teams.teaminvitation.model.entity.TeamInvitation;

public interface TeamInvitationRepository extends JpaRepository<TeamInvitation, Long>, TeamInvitationRepositoryCustom {
	boolean existsByTeamIdAndTargetIdAndStatus(Long teamId, Long targetId, InvitationStatus status);

	Optional<TeamInvitation> findByTeamIdAndTargetId(Long teamId, Long targetId);

	Optional<TeamInvitation> findByIdAndTeamId(Long id, Long teamId);

}
