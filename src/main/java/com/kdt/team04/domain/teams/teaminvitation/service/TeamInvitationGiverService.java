package com.kdt.team04.domain.teams.teaminvitation.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kdt.team04.common.exception.EntityNotFoundException;
import com.kdt.team04.common.exception.ErrorCode;
import com.kdt.team04.domain.teams.teaminvitation.model.InvitationStatus;
import com.kdt.team04.domain.teams.teaminvitation.repository.TeamInvitationRepository;

@Service
@Transactional(readOnly = true)
public class TeamInvitationGiverService {
	private final TeamInvitationRepository teamInvitationRepository;

	public TeamInvitationGiverService(
		TeamInvitationRepository teamInvitationRepository) {
		this.teamInvitationRepository = teamInvitationRepository;
	}

	public boolean existsTeamInvitation(Long teamId, InvitationStatus status) {
		return teamInvitationRepository.existsByIdAndStatus(teamId, status);
	}

	@Transactional
	public void accept(Long id) {
		teamInvitationRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.TEAM_INVITATION_NOT_FOUND))
			.accept();
	}

}
