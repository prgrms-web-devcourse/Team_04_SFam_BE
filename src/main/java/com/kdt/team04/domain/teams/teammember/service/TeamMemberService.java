package com.kdt.team04.domain.teams.teammember.service;

import java.text.MessageFormat;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kdt.team04.common.exception.BusinessException;
import com.kdt.team04.common.exception.ErrorCode;
import com.kdt.team04.domain.teams.team.dto.response.TeamResponse;
import com.kdt.team04.domain.teams.team.model.entity.Team;
import com.kdt.team04.domain.teams.team.service.TeamGiverService;
import com.kdt.team04.domain.teams.teaminvitation.model.InvitationStatus;
import com.kdt.team04.domain.teams.teaminvitation.service.TeamInvitationGiverService;
import com.kdt.team04.domain.teams.teammember.dto.TeamMemberConverter;
import com.kdt.team04.domain.teams.teammember.dto.request.RegisterTeamMemberRequest;
import com.kdt.team04.domain.teams.teammember.model.entity.TeamMember;
import com.kdt.team04.domain.teams.teammember.model.TeamMemberRole;
import com.kdt.team04.domain.teams.teammember.repository.TeamMemberRepository;
import com.kdt.team04.domain.user.dto.response.UserResponse;
import com.kdt.team04.domain.user.entity.User;
import com.kdt.team04.domain.user.service.UserService;

@Service
@Transactional(readOnly = true)
public class TeamMemberService {

	private final TeamMemberRepository teamMemberRepository;
	private final UserService userService;
	private final TeamGiverService teamGiverService;
	private final TeamInvitationGiverService teamInvitationGiverService;
	private final TeamMemberConverter converter;

	public TeamMemberService(
		TeamMemberRepository teamMemberRepository,
		UserService userService,
		TeamGiverService teamGiverService,
		TeamInvitationGiverService teamInvitationGiverService,
		TeamMemberConverter converter) {
		this.teamMemberRepository = teamMemberRepository;
		this.userService = userService;
		this.teamGiverService = teamGiverService;
		this.teamInvitationGiverService = teamInvitationGiverService;
		this.converter = converter;
	}

	public boolean existsTeamMember(Long teamId, Long userId) {
		return teamMemberRepository.existsByTeamIdAndUserId(teamId, userId);
	}

	@Transactional
	public void registerTeamMember(Long myId, Long teamId, RegisterTeamMemberRequest request) {
		if (!Objects.equals(request.userId(), myId)) {
			throw new BusinessException(ErrorCode.NOT_AUTHENTICATED,
				MessageFormat.format("초대를 수락할 권한이 없습니다. myId = {0}, targetId {1}",
					myId, request.userId()));
		}

		if (existsTeamMember(teamId, request.userId())) {
			throw new BusinessException(ErrorCode.ALREADY_TEAM_MEMBER,
				MessageFormat.format("이미 팀원인 사용자입니다. targetId = {0}", request.userId()));
		}

		if (!teamInvitationGiverService.existsTeamInvitation(request.invitationId(), InvitationStatus.WAITING)) {
			throw new BusinessException(ErrorCode.INVALID_TEAM_INVITATION,
				MessageFormat.format("초대장이 대기상태가 아닙니다. invitationId = {0}", request.invitationId()));
		}

		UserResponse userResponse = userService.findById(request.userId());
		TeamResponse teamResponse = teamGiverService.findById(teamId);
		User user = converter.toUser(userResponse.id());
		Team team = converter.toTeam(teamResponse.id());

		TeamMember teamMember = new TeamMember(team, user, TeamMemberRole.MEMBER);
		teamMemberRepository.save(teamMember);

		teamInvitationGiverService.accept(request.invitationId());
	}

}
