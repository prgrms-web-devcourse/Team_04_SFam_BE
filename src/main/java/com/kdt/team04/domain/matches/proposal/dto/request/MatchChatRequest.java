package com.kdt.team04.domain.matches.proposal.dto.request;

import java.time.LocalDateTime;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;

public record MatchChatRequest(

	@Schema(description = "채팅 대상", required = true)
	@NotNull
	Long targetId,

	@Schema(description = "채팅 내용", required = true)
	@NotBlank
	String content,

	@Schema(description = "채팅 시간", required = true, pattern = "yyyy-MM-dd HH:mm:ss")
	@NotNull
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
	LocalDateTime chattedAt
) {
}