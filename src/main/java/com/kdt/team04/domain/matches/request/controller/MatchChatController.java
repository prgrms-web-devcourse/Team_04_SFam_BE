package com.kdt.team04.domain.matches.request.controller;

import javax.validation.Valid;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kdt.team04.common.exception.NotAuthenticationException;
import com.kdt.team04.common.security.jwt.JwtAuthentication;
import com.kdt.team04.domain.matches.request.dto.MatchChatRequest;
import com.kdt.team04.domain.matches.request.service.MatchChatService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/matches/proposals")
public class MatchChatController {

	private final MatchChatService matchChatService;

	public MatchChatController(MatchChatService matchChatService) {
		this.matchChatService = matchChatService;
	}

	@PostMapping("/{id}/chats")
	@Operation(summary = "채팅 등록", description = "작성자와 승인된 신청자가 채팅을 등록한다.")
	public void chat(
		@AuthenticationPrincipal JwtAuthentication authentication,
		@PathVariable Long id,
		@Valid @RequestBody MatchChatRequest request
	) {
		if (authentication == null) {
			throw new NotAuthenticationException("Not Authenticated");
		}

		matchChatService.chat(
			id,
			authentication.id(),
			request.targetId(),
			request.content(),
			request.chattedAt()
		);
	}
}
