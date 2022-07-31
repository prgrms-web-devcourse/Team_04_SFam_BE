package com.kdt.team04.domain.matches.proposal.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kdt.team04.common.ApiResponse;
import com.kdt.team04.common.exception.NotAuthenticationException;
import com.kdt.team04.common.security.jwt.JwtAuthentication;
import com.kdt.team04.domain.matches.proposal.dto.MatchProposalRequest;
import com.kdt.team04.domain.matches.proposal.dto.MatchProposalResponse;
import com.kdt.team04.domain.matches.proposal.service.MatchProposalService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/matches/{matchId}/proposals")
public class MatchProposalController {

	private final MatchProposalService matchProposalService;

	public MatchProposalController(MatchProposalService matchProposalService) {
		this.matchProposalService = matchProposalService;
	}

	@PostMapping
	@Operation(summary = "대결 신청", description = "사용자는 대결을 신청할 수 있다.")
	public void propose(@PathVariable Long matchId, @AuthenticationPrincipal JwtAuthentication jwtAuthentication,
		@RequestBody @Valid MatchProposalRequest.ProposalCreate request) {
		if (jwtAuthentication == null) {
			throw new NotAuthenticationException("Not Authenticated");
		}

		matchProposalService.create(jwtAuthentication.id(), matchId, request);
	}

	@PatchMapping("/{id}")
	@Operation(summary = "신청 수락 및 거절", description = "대결 공고자는 대결 신청을 수락 또는 거절 할 수 있다.")
	public void proposeReact(@PathVariable Long matchId, @PathVariable Long id,
		@RequestBody @Valid MatchProposalRequest.ProposalReact request) {
		matchProposalService.react(matchId, id, request.status());
	}

	@GetMapping
	@Operation(summary = "신청 목록 조회", description = "해당 대결의 신청 목록이 조회된다.")
	public ApiResponse<List<MatchProposalResponse.Chat>> findAllChats(
		@AuthenticationPrincipal JwtAuthentication jwtAuthentication,
		@PathVariable Long matchId
	) {
		if (jwtAuthentication == null) {
			throw new NotAuthenticationException("Not Authenticated");
		}

		List<MatchProposalResponse.Chat> proposals = matchProposalService.findAllProposals(
			matchId,
			jwtAuthentication.id()
		);

		return new ApiResponse<>(proposals);
	}
}