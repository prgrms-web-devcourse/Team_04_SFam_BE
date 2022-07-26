package com.kdt.team04.domain.teammember.controller;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kdt.team04.domain.teammember.dto.TeamMemberRequest;
import com.kdt.team04.domain.teammember.service.TeamMemberService;

@RestController
@RequestMapping("/api/teams/members")
public class TeamMemberController {

	private final TeamMemberService teamMemberService;

	public TeamMemberController(TeamMemberService teamMemberService) {
		this.teamMemberService = teamMemberService;
	}

	@PostMapping
	public void registerMember(@RequestBody @Valid TeamMemberRequest.RegisterRequest registerRequest) {
		teamMemberService.registerTeamMember(registerRequest);
	}

}
