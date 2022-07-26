package com.kdt.team04.domain.teaminvitation.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kdt.team04.common.ApiResponse;
import com.kdt.team04.common.exception.NotAuthenticationException;
import com.kdt.team04.common.security.jwt.JwtAuthentication;
import com.kdt.team04.domain.teaminvitation.dto.TeamInvitationResponse;
import com.kdt.team04.domain.teaminvitation.service.TeamInvitationService;

@RestController
@RequestMapping("/api/teams")
public class TeamInvitationController {

	private final TeamInvitationService teamInvitationService;

	public TeamInvitationController(TeamInvitationService teamInvitationService) {
		this.teamInvitationService = teamInvitationService;
	}

	@PostMapping("/invitations")
	public ApiResponse<TeamInvitationResponse.InviteResponse> invite(@AuthenticationPrincipal JwtAuthentication auth, TeamInvitationRequest request) {
		if (auth == null)
			throw new NotAuthenticationException("Not Authenticated");

		return new ApiResponse<>(teamInvitationService.invite(auth.id(), request));
	}
}
