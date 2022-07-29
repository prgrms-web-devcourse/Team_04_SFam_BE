package com.kdt.team04.domain.teaminvitation.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kdt.team04.domain.teaminvitation.entity.InvitationStatus;
import com.kdt.team04.domain.teaminvitation.entity.TeamInvitation;

public interface TeamInvitationRepository extends JpaRepository<TeamInvitation, Long> {
	boolean existsByTeamIdAndTargetIdAndStatus(Long teamId, Long targetId, InvitationStatus status);

	Optional<TeamInvitation> findByTeamIdAndTargetId(Long teamId, Long targetId);
}
