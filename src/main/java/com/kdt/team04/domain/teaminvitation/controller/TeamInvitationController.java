package com.kdt.team04.domain.teaminvitation.controller;

import javax.validation.Valid;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kdt.team04.common.ApiResponse;
import com.kdt.team04.common.exception.NotAuthenticationException;
import com.kdt.team04.common.security.jwt.JwtAuthentication;
import com.kdt.team04.domain.teaminvitation.dto.TeamInvitationRequest;
import com.kdt.team04.domain.teaminvitation.dto.TeamInvitationResponse;
import com.kdt.team04.domain.teaminvitation.service.TeamInvitationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/teams")
@Tag(name = "팀원 초대 API")
public class TeamInvitationController {

	private final TeamInvitationService teamInvitationService;

	public TeamInvitationController(TeamInvitationService teamInvitationService) {
		this.teamInvitationService = teamInvitationService;
	}

	@PostMapping("/invitations")
	@Operation(summary = "팀원 초대", description = "팀 ID와 초대 대상 유저 ID를 받아 팀으로 초대합니다.")
	public ApiResponse<TeamInvitationResponse.InviteResponse> invite(@AuthenticationPrincipal JwtAuthentication auth, @RequestBody @Valid TeamInvitationRequest request) {
		if (auth == null)
			throw new NotAuthenticationException("Not Authenticated");

		return new ApiResponse<>(teamInvitationService.invite(auth.id(), request));
	}
}
