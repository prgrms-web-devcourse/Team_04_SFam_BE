package com.kdt.team04.domain.matches.proposal.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kdt.team04.domain.user.dto.response.ChatWriterProfileResponse;

import io.swagger.v3.oas.annotations.media.Schema;

public record ChatItemResponse(
	@Schema(description = "채팅 내용")
	String content,

	@Schema(description = "채팅 시간", pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
	LocalDateTime chattedAt,

	@Schema(description = "채팅 작성자")
	ChatWriterProfileResponse writer
) {
}