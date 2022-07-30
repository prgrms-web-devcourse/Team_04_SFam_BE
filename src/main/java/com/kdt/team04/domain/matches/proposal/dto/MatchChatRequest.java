package com.kdt.team04.domain.matches.proposal.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;

public record MatchChatRequest(

	@NotNull
	@Schema(description = "채팅 대상")
	Long targetId,

	@NotBlank
	@Schema(description = "채팅 내용")
	String content,

	@NotNull
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@Schema(description = "채팅 시간")
	LocalDateTime chattedAt
) {
}
