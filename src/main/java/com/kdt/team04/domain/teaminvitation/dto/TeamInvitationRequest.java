package com.kdt.team04.domain.teaminvitation.dto;

import javax.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;

public record TeamInvitationRequest(
	@NotNull
	@Schema(description = "팀 ID", required = true)
	Long teamId,

	@NotNull
	@Schema(description = "초대보낼 유저의 고유 ID", required = true)
	Long targetUserId
) {
}
