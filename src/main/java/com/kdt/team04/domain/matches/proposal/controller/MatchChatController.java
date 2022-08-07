package com.kdt.team04.domain.matches.proposal.controller;

import javax.validation.Valid;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kdt.team04.common.ApiResponse;
import com.kdt.team04.common.exception.NotAuthenticationException;
import com.kdt.team04.common.security.jwt.JwtAuthentication;
import com.kdt.team04.domain.matches.proposal.dto.request.MatchChatRequest;
import com.kdt.team04.domain.matches.proposal.dto.response.ChattingResponse;
import com.kdt.team04.domain.matches.proposal.service.MatchChatService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "매칭 채팅 API")
@RestController
@RequestMapping("/api/matches/proposals")
public class MatchChatController {

	private final MatchChatService matchChatService;

	public MatchChatController(MatchChatService matchChatService) {
		this.matchChatService = matchChatService;
	}

	@Operation(summary = "채팅 등록", description = "작성자와 승인된 신청자가 채팅을 등록한다.")
	@PostMapping("/{id}/chats")
	public void chat(
		@AuthenticationPrincipal JwtAuthentication authentication,
		@Parameter(description = "매칭 신청 ID") @PathVariable Long id,
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

	@Operation(summary = "채팅 조회", description = "채팅 기록을 조회할 수 있다.")
	@GetMapping("/{id}/chats")
	public ApiResponse<ChattingResponse> chat(
		@AuthenticationPrincipal JwtAuthentication authentication,
		@Parameter(description = "매칭 신청 ID") @PathVariable Long id
	) {
		if (authentication == null) {
			throw new NotAuthenticationException("Not Authenticated");
		}

		ChattingResponse chats = matchChatService.findChatsByProposalId(id, authentication.id());

		return new ApiResponse<>(chats);
	}
}
