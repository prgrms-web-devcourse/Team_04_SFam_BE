package com.kdt.team04.domain.teams.teaminvitation.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kdt.team04.domain.teams.teaminvitation.model.InvitationStatus;
import com.kdt.team04.domain.teams.teaminvitation.model.entity.TeamInvitation;

public interface TeamInvitationRepository extends JpaRepository<TeamInvitation, Long>, CustomTeamInvitationRepository {
	boolean existsByTeamIdAndTargetIdAndStatus(Long teamId, Long targetId, InvitationStatus status);

	boolean existsByTeamIdAndTargetIdAndStatusIn(Long teamId, Long targetId, List<InvitationStatus> statusList);

	Optional<TeamInvitation> findByTeamIdAndTargetId(Long teamId, Long targetId);

	Optional<TeamInvitation> findByIdAndTeamId(Long id, Long teamId);
}
