package com.kdt.team04.domain.teams.teaminvitation.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;

public record TeamInvitationCursor(
	@Schema(description = "다음 생성일 커서", pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@NotNull
	LocalDateTime createdAt,

	@Schema(description = "다음 ID 커서")
	@NotNull
	Long id) {

}
