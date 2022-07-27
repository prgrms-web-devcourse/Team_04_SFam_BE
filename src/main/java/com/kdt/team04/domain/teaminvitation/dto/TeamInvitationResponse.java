package com.kdt.team04.domain.teaminvitation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record TeamInvitationResponse() {

	public record InviteResponse(
		@Schema(description = "팀 초대 ID")
		Long invitationId) {

	}
}
