package com.kdt.team04.domain.teammember.controller;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kdt.team04.domain.teammember.dto.request.TeamMemberRegisterRequest;
import com.kdt.team04.domain.teammember.service.TeamMemberService;

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

	@PostMapping
	@Operation(summary = "팀원 등록", description = "팀에 해당 회원을 팀원으로 등록한다.")
	public void registerMember(@PathVariable Long teamId,
		@RequestBody @Valid TeamMemberRegisterRequest teamMemberRegisterRequest) {
		teamMemberService.registerTeamMember(teamId, teamMemberRegisterRequest);
	}

}
