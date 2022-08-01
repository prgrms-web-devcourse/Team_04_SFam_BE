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
import com.kdt.team04.domain.matches.review.dto.MatchReviewRequest;
import com.kdt.team04.domain.matches.review.service.MatchReviewService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/matches/{id}/review")
public class MatchReviewController {

	private final MatchReviewService matchReviewService;

	public MatchReviewController(MatchReviewService matchReviewService) {
		this.matchReviewService = matchReviewService;
	}

	@PostMapping
	@Operation(summary = "경기 후기 등록", description = "경기 종료 후 경기 후기를 등록한다.")
	public void review(
		@AuthenticationPrincipal JwtAuthentication authentication,
		@PathVariable Long id,
		@Valid @RequestBody MatchReviewRequest request
	) {
		if (authentication == null) {
			throw new NotAuthenticationException("Not Authenticated");
		}

		matchReviewService.review(id, request.review(), authentication.id());
	}
}