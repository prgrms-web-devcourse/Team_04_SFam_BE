package com.kdt.team04.domain.teams.teammember.service;

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
	public void registerTeamMember(Long teamId, RegisterTeamMemberRequest teamMemberRegisterRequest) {
		if (existsTeamMember(teamId, teamMemberRegisterRequest.userId())) {
			throw new BusinessException(ErrorCode.ALREADY_TEAM_MEMBER);
		}

		if (!teamInvitationGiverService.existsTeamInvitation(
			teamId, teamMemberRegisterRequest.userId(), InvitationStatus.WAITING)
		) {
			throw new BusinessException(ErrorCode.INVALID_TEAM_INVITATION);
		}

		UserResponse userResponse = userService.findById(teamMemberRegisterRequest.userId());
		TeamResponse teamResponse = teamGiverService.findById(teamId);
		User user = converter.toUser(userResponse.id());
		Team team = converter.toTeam(teamResponse.id());

		TeamMember teamMember = new TeamMember(team, user, TeamMemberRole.MEMBER);
		teamMemberRepository.save(teamMember);

		teamInvitationGiverService.accept(teamId, teamMemberRegisterRequest.userId());
	}

}
