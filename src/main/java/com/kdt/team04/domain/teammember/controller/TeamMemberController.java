package com.kdt.team04.domain.teammember.controller;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kdt.team04.domain.teammember.dto.TeamMemberRequest;
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
	@Operation(summary = "팀원 등록", description = "팀 아이디와 등록 대상 유저 ID를 받아 팀원에 등록합니다.")
	public void registerMember(@PathVariable Long teamId, @RequestBody @Valid TeamMemberRequest.RegisterRequest registerRequest) {
		teamMemberService.registerTeamMember(teamId, registerRequest);
	}

}
