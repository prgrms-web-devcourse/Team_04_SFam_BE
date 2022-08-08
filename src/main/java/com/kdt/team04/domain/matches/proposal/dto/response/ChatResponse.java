package com.kdt.team04.domain.matches.proposal.dto.response;

import java.time.LocalDateTime;

import com.kdt.team04.domain.user.dto.response.ChatWriterProfileResponse;

import io.swagger.v3.oas.annotations.media.Schema;

public record ChatResponse(
	@Schema(description = "채팅 내용")
	String content,

	@Schema(description = "채팅 시간(yyyy-MM-dd HH:mm:ss)")
	LocalDateTime chattedAt,

	@Schema(description = "채팅 작성자")
	ChatWriterProfileResponse writer
) {
}