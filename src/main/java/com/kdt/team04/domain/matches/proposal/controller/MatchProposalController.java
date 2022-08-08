package com.kdt.team04.domain.matches.proposal.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kdt.team04.common.ApiResponse;
import com.kdt.team04.common.config.resolver.AuthUser;
import com.kdt.team04.common.security.jwt.JwtAuthentication;
import com.kdt.team04.domain.matches.proposal.dto.request.CreateProposalRequest;
import com.kdt.team04.domain.matches.proposal.dto.request.ReactProposalRequest;
import com.kdt.team04.domain.matches.proposal.dto.response.ChatRoomResponse;
import com.kdt.team04.domain.matches.proposal.service.MatchProposalService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "매칭 신청 API")
@RestController
@RequestMapping("/api/matches/{matchId}/proposals")
public class MatchProposalController {

	private final MatchProposalService matchProposalService;

	public MatchProposalController(MatchProposalService matchProposalService) {
		this.matchProposalService = matchProposalService;
	}

	@Operation(summary = "대결 신청", description = "사용자는 대결을 신청할 수 있다.")
	@PostMapping
	public void propose(
		@AuthUser JwtAuthentication auth,
		@Parameter(description = "매칭 공고 ID") @PathVariable Long matchId,
		@RequestBody @Valid CreateProposalRequest request) {
		matchProposalService.create(auth.id(), matchId, request);
	}

	@Operation(summary = "신청 수락 및 거절", description = "대결 공고자는 대결 신청을 수락 또는 거절 할 수 있다.")
	@PatchMapping("/{id}")
	public void proposeReact(
		@Parameter(description = "매칭 공고 ID") @PathVariable Long matchId,
		@Parameter(description = "매칭 신청 ID") @PathVariable Long id,
		@RequestBody @Valid ReactProposalRequest request) {
		matchProposalService.react(matchId, id, request.status());
	}

	@Operation(summary = "신청 목록 조회", description = "해당 대결의 신청 목록이 조회된다.")
	@GetMapping
	public ApiResponse<List<ChatRoomResponse>> findAllChats(
		@AuthUser JwtAuthentication auth,
		@Parameter(description = "매칭 공고 ID") @PathVariable Long matchId
	) {
		List<ChatRoomResponse> proposals = matchProposalService.findAllProposalChats(
			matchId,
			auth.id()
		);

		return new ApiResponse<>(proposals);
	}
}
