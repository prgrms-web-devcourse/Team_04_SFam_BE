package com.kdt.team04.domain.teaminvitation.dto.request;

import javax.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;

public record TeamInvitationRequest(
	@Schema(description = "초대 보낼 회원 ID(고유 PK)", required = true)
	@NotNull
	Long targetUserId
) {
}
