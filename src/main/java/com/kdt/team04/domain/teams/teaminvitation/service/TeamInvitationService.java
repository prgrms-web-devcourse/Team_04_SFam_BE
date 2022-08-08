package com.kdt.team04.domain.teams.teaminvitation.service;

import java.text.MessageFormat;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kdt.team04.common.PageDto;
import com.kdt.team04.common.exception.BusinessException;
import com.kdt.team04.common.exception.EntityNotFoundException;
import com.kdt.team04.common.exception.ErrorCode;
import com.kdt.team04.domain.teams.team.dto.TeamConverter;
import com.kdt.team04.domain.teams.team.dto.response.TeamResponse;
import com.kdt.team04.domain.teams.team.model.entity.Team;
import com.kdt.team04.domain.teams.team.service.TeamService;
import com.kdt.team04.domain.teams.teaminvitation.dto.TeamInvitationCursor;
import com.kdt.team04.domain.teams.teaminvitation.dto.response.TeamInviteResponse;
import com.kdt.team04.domain.teams.teaminvitation.dto.response.TeamInvitationResponse;
import com.kdt.team04.domain.teams.teaminvitation.model.InvitationStatus;
import com.kdt.team04.domain.teams.teaminvitation.model.entity.TeamInvitation;
import com.kdt.team04.domain.teams.teaminvitation.repository.TeamInvitationRepository;
import com.kdt.team04.domain.teams.teammember.service.TeamMemberGiverService;
import com.kdt.team04.domain.user.UserConverter;
import com.kdt.team04.domain.user.dto.response.UserResponse;
import com.kdt.team04.domain.user.entity.User;
import com.kdt.team04.domain.user.service.UserService;

@Service
@Transactional(readOnly = true)
public class TeamInvitationService {

	private final TeamInvitationRepository teamInvitationRepository;
	private final UserService userService;
	private final TeamConverter teamConverter;
	private final UserConverter userConverter;
	private final TeamMemberGiverService teamMemberGiverService;
	private final TeamService teamService;

	public TeamInvitationService(TeamInvitationRepository teamInvitationRepository,
		UserService userService, TeamConverter teamConverter, UserConverter userConverter,
		TeamMemberGiverService teamMemberGiverService, TeamService teamService) {
		this.teamInvitationRepository = teamInvitationRepository;
		this.userService = userService;
		this.teamConverter = teamConverter;
		this.userConverter = userConverter;
		this.teamMemberGiverService = teamMemberGiverService;
		this.teamService = teamService;
	}

	public PageDto.CursorResponse<TeamInvitationResponse, TeamInvitationCursor> getInvitations(Long targetId,
		PageDto.TeamInvitationCursorPageRequest request) {
		return teamInvitationRepository.getInvitations(targetId, request);
	}

	@Transactional
	public TeamInviteResponse invite(Long myId, Long teamId, Long targetUserId) {
		if (teamMemberGiverService.existsTeamMember(teamId, targetUserId)) {
			throw new BusinessException(ErrorCode.ALREADY_TEAM_MEMBER,
				MessageFormat.format("teamId = {0}, userId = {1}", teamId, targetUserId));
		}

		TeamResponse teamResponse = teamService.findById(teamId);
		UserResponse targetResponse = userService.findById(targetUserId);
		Team team = teamConverter.toTeam(teamResponse, userConverter.toUser(teamResponse.leader()));

		if (!Objects.equals(team.getLeader().getId(), myId)) {
			throw new BusinessException(ErrorCode.NOT_TEAM_LEADER,
				MessageFormat.format("{0} is not team leader id {1} ", myId, team.getLeader().getId()));
		}

		User target = userConverter.toUser(targetResponse);
		TeamInvitation invitation = new TeamInvitation(team, target, InvitationStatus.WAITING);
		Long savedInvitationId = teamInvitationRepository.save(invitation).getId();

		return new TeamInviteResponse(savedInvitationId);
	}

	@Transactional
	public void refuse(Long teamId, Long invitationId) {
		teamInvitationRepository.findByIdAndTeamId(invitationId, teamId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.TEAM_INVITATION_NOT_FOUND,
				MessageFormat.format("{0} is not team invitation id {1}", teamId, invitationId)))
			.refuse();
	}

}
