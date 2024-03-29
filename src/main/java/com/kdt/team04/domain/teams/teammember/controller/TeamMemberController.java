package com.kdt.team04.domain.teams.teammember.controller;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kdt.team04.common.config.resolver.AuthUser;
import com.kdt.team04.common.security.jwt.JwtAuthentication;
import com.kdt.team04.domain.teams.teammember.dto.request.RegisterTeamMemberRequest;
import com.kdt.team04.domain.teams.teammember.service.TeamMemberService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "팀원 관리 API")
@RestController
@RequestMapping("/api/teams/{teamId}/members")
public class TeamMemberController {

	private final TeamMemberService teamMemberService;

	public TeamMemberController(TeamMemberService teamMemberService) {
		this.teamMemberService = teamMemberService;
	}

	@Operation(summary = "초대 수락 및 팀원 등록", description = "초대 수락 후 팀에 해당 회원을 팀원으로 등록한다.")
	@PostMapping
	public void registerMember(@PathVariable Long teamId,
		@AuthUser JwtAuthentication auth,
		@RequestBody @Valid RegisterTeamMemberRequest teamMemberRegisterRequest) {
		teamMemberService.registerTeamMember(auth.id(), teamId, teamMemberRegisterRequest);
	}
}
