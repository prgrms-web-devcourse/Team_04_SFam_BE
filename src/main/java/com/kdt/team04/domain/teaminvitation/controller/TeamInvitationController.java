package com.kdt.team04.domain.teaminvitation.controller;

import javax.validation.Valid;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kdt.team04.common.ApiResponse;
import com.kdt.team04.common.PageDto;
import com.kdt.team04.common.exception.NotAuthenticationException;
import com.kdt.team04.common.security.jwt.JwtAuthentication;
import com.kdt.team04.domain.teaminvitation.dto.TeamInvitationRequest;
import com.kdt.team04.domain.teaminvitation.dto.TeamInvitationResponse;
import com.kdt.team04.domain.teaminvitation.service.TeamInvitationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "팀원 초대 API")
@RestController
@RequestMapping("/api/teams")
public class TeamInvitationController {
	private final TeamInvitationService teamInvitationService;

	public TeamInvitationController(TeamInvitationService teamInvitationService) {
		this.teamInvitationService = teamInvitationService;
	}

	@Operation(summary = "초대 목록 조회", description = "자신이 초대받은 초대 목록을 조회한다.")
	@GetMapping("/invitations")
	public ApiResponse<PageDto.CursorResponse> getInvitations(
		@AuthenticationPrincipal JwtAuthentication auth,
		@Valid PageDto.TeamInvitationCursorPageRequest request
	) {
		if (auth == null) {
			throw new NotAuthenticationException("Not Authenticated");
		}

		PageDto.CursorResponse result = teamInvitationService.getInvitations(auth.id(), request);

		return new ApiResponse<>(result);
	}

	@PostMapping("/{teamId}/invitations")
	@Operation(summary = "팀원 초대", description = "팀 ID와 초대 대상 회원 ID를 받아 팀으로 초대한다.")
	public ApiResponse<TeamInvitationResponse.InviteResponse> invite(
		@AuthenticationPrincipal JwtAuthentication auth,
		@Parameter(description = "팀 ID", required = true) @PathVariable Long teamId,
		@RequestBody @Valid TeamInvitationRequest request
	) {
		if (auth == null)
			throw new NotAuthenticationException("Not Authenticated");

		return new ApiResponse<>(teamInvitationService.invite(auth.id(), teamId, request.targetUserId()));
	}

	@PatchMapping("/{teamId}/invitation/{invitationId}")
	@Operation(summary = "초대 거절", description = "팀 ID와 초대 ID를 받아 초대를 거절한다.")
	public void refuse(
		@Parameter(description = "팀 ID") @PathVariable Long teamId,
		@Parameter(description = "팀 초대 ID") @PathVariable Long invitationId
	) {
		teamInvitationService.refuse(teamId, invitationId);
	}

}
