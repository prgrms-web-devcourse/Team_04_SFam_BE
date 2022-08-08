package com.kdt.team04.domain.teams.teaminvitation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record TeamInviteResponse(
	@Schema(description = "팀 초대 ID(고유 PK)")
	Long invitationId) {

}