package com.kdt.team04.domain.teammember.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kdt.team04.domain.team.dto.TeamResponse;
import com.kdt.team04.domain.team.entity.Team;
import com.kdt.team04.domain.team.service.TeamService;
import com.kdt.team04.domain.teammember.dto.TeamMemberConverter;
import com.kdt.team04.domain.teammember.dto.TeamMemberRequest;
import com.kdt.team04.domain.teammember.entity.TeamMember;
import com.kdt.team04.domain.teammember.entity.TeamMemberRole;
import com.kdt.team04.domain.teammember.repository.TeamMemberRepository;
import com.kdt.team04.domain.user.dto.UserResponse;
import com.kdt.team04.domain.user.entity.User;
import com.kdt.team04.domain.user.service.UserService;

@Service
@Transactional(readOnly = true)
public class TeamMemberService {

	private final TeamMemberRepository teamMemberRepository;
	private final UserService userService;
	private final TeamService teamService;
	private final TeamMemberConverter converter;

	public TeamMemberService(TeamMemberRepository teamMemberRepository,
		UserService userService, TeamService teamService, TeamMemberConverter converter) {
		this.teamMemberRepository = teamMemberRepository;
		this.userService = userService;
		this.teamService = teamService;
		this.converter = converter;
	}

	public boolean existsTeamMember(Long teamId, Long userId) {
		return teamMemberRepository.existsByTeamIdAndUserId(teamId, userId);
	}

	@Transactional
	public void registerTeamMember(TeamMemberRequest.RegisterRequest registerRequest) {
		UserResponse userResponse = userService.findById(registerRequest.userId());
		TeamResponse teamResponse = teamService.findById(registerRequest.teamId());
		User user = converter.toUser(userResponse);
		Team team = converter.toTeam(teamResponse);

		TeamMember teamMember = new TeamMember(team, user, TeamMemberRole.MEMBER);
		teamMemberRepository.save(teamMember);
	}

	@Transactional
	public void registerTeamLeader(TeamMemberRequest.RegisterRequest registerRequest) {
		UserResponse userResponse = userService.findById(registerRequest.userId());
		TeamResponse teamResponse = teamService.findById(registerRequest.teamId());
		User user = converter.toUser(userResponse);
		Team team = converter.toTeam(teamResponse);

		TeamMember teamMember = new TeamMember(team, user, TeamMemberRole.LEADER);
		teamMemberRepository.save(teamMember);
	}
}
