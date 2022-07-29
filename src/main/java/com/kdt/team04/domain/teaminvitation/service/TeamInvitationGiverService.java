package com.kdt.team04.domain.teaminvitation.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kdt.team04.common.exception.EntityNotFoundException;
import com.kdt.team04.common.exception.ErrorCode;
import com.kdt.team04.domain.teaminvitation.entity.InvitationStatus;
import com.kdt.team04.domain.teaminvitation.repository.TeamInvitationRepository;

@Service
@Transactional(readOnly = true)
public class TeamInvitationGiverService {
	private final TeamInvitationRepository teamInvitationRepository;

	public TeamInvitationGiverService(
		TeamInvitationRepository teamInvitationRepository) {
		this.teamInvitationRepository = teamInvitationRepository;
	}

	public boolean existsTeamInvitation(Long teamId, Long targetId, InvitationStatus status) {
		return teamInvitationRepository.existsByTeamIdAndTargetIdAndStatus(teamId, targetId, status);
	}

	@Transactional
	public void accept(Long teamId, Long targetId) {
		teamInvitationRepository.findByTeamIdAndTargetId(teamId, targetId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.TEAM_INVITATION_NOT_FOUND))
			.accept();
	}

}
