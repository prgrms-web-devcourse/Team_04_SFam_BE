package com.kdt.team04.domain.matches.proposal.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.kdt.team04.domain.user.dto.UserResponse;

import io.swagger.v3.oas.annotations.media.Schema;

public record MatchChatResponse() {

	public record Chat(
		@Schema(description = "채팅 내용")
		String content,

		@Schema(description = "채팅 시간(yyyy-MM-dd HH:mm:ss)")
		LocalDateTime chattedAt,

		@Schema(description = "채팅 작성자")
		UserResponse.ChatWriterProfile writer
	) { }

	public record Chatting(
		@Schema(description = "매칭 공고 정보")
		MatchProposalResponse.ChatMatch match,

		@Schema(description = "채팅 내용들")
		List<Chat> chats
	) {}

	public record LastChat(
		@Schema(description = "마지막 채팅 내용")
		String content
	) {}
}
