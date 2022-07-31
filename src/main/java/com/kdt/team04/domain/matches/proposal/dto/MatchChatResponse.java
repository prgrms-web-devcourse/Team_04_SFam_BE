package com.kdt.team04.domain.matches.proposal.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.kdt.team04.domain.user.dto.UserResponse;

import io.swagger.v3.oas.annotations.media.Schema;

public record MatchChatResponse() {

	public record Chat(
		String content,
		LocalDateTime chattedAt,
		UserResponse.ChatWriterProfile writer
	) { }

	public record Chats(
		MatchProposalResponse.ChatMatch match,
		List<Chat> chats
	) {}

	public record LastChat(
		@Schema(description = "마지막 채팅 내용")
		String content
	) {}
}
