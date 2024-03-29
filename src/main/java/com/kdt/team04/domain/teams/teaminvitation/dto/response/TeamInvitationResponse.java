package com.kdt.team04.domain.teams.teaminvitation.dto.response;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;

public record TeamInvitationResponse(
	@Schema(description = "팀 초대 ID(고유 PK)")
	Long invitationId,

	@Schema(description = "팀 ID(고유 PK)")
	Long teamId,

	@Schema(description = "팀 이름")
	String name,

	@Schema(description = "팀 로고 이미지 URL")
	String logoImageUrl,

	@Schema(description = "초대 날짜", pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
	LocalDateTime createdAt
) {
}