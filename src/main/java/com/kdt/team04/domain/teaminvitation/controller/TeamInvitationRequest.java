package com.kdt.team04.domain.teaminvitation.controller;

import javax.validation.constraints.NotNull;

public record TeamInvitationRequest(
	@NotNull
	Long teamId,

	@NotNull
	Long targetUserId
) {
}
