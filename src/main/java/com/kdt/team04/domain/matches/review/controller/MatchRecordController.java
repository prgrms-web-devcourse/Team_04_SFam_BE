package com.kdt.team04.domain.matches.review.controller;

import javax.validation.Valid;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kdt.team04.common.exception.NotAuthenticationException;
import com.kdt.team04.common.security.jwt.JwtAuthentication;
import com.kdt.team04.domain.matches.review.dto.MatchRecordRequest;
import com.kdt.team04.domain.matches.review.service.MatchRecordService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "경기 결과 API")
@RestController
@RequestMapping("/api/matches/{id}/records")
public class MatchRecordController {

	private final MatchRecordService matchRecordService;

	public MatchRecordController(MatchRecordService matchRecordService) {
		this.matchRecordService = matchRecordService;
	}

	@Operation(summary = "경기 결과 등록", description = "경기 결과를 등록한다.")
	@PostMapping
	public void endGame(
		@AuthenticationPrincipal JwtAuthentication authentication,
		@PathVariable Long id,
		@Valid @RequestBody MatchRecordRequest request
	) {
		if (authentication == null) {
			throw new NotAuthenticationException("Not Authenticated");
		}

		matchRecordService.endGame(id, request.proposalId(), request.result(), authentication.id());
	}
}
