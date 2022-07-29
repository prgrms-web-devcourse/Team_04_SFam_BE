package com.kdt.team04.domain.matches.match.controller;

import javax.validation.Valid;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kdt.team04.common.ApiResponse;
import com.kdt.team04.common.PageDto;
import com.kdt.team04.common.exception.NotAuthenticationException;
import com.kdt.team04.common.security.jwt.JwtAuthentication;
import com.kdt.team04.domain.matches.match.dto.MatchPagingCursor;
import com.kdt.team04.domain.matches.match.dto.MatchRequest;
import com.kdt.team04.domain.matches.match.dto.MatchResponse;
import com.kdt.team04.domain.matches.match.service.MatchService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/matches")
public class MatchController {

	private final MatchService matchService;

	public MatchController(MatchService matchService) {
		this.matchService = matchService;
	}

	@Operation(summary = "매치 공고 생성", description = "사용자는 매칭 공고를 작성할 수 있습니다. ")
	@PostMapping
	public void post(@AuthenticationPrincipal JwtAuthentication jwtAuthentication,
		@RequestBody @Valid MatchRequest.MatchCreateRequest request) {
		if (jwtAuthentication == null) {
			throw new NotAuthenticationException("Not Authenticated");
		}

		matchService.create(jwtAuthentication.id(), request);
	}

	@GetMapping
	public ApiResponse<PageDto.CursorResponse<MatchResponse.ListViewResponse, MatchPagingCursor>> getMatches(
		@AuthenticationPrincipal JwtAuthentication auth, @Valid PageDto.MatchCursorPageRequest pageRequest) {
		PageDto.CursorResponse<MatchResponse.ListViewResponse, MatchPagingCursor> matches = matchService.findMatches(
			auth.id(), pageRequest);

		return new ApiResponse<>(matches);
	}

	@Operation(summary = "매치 공고 상세 조회", description = "매칭 공고의 세부 정보를 조회할 수 있다.")
	@GetMapping("/{id}")
	public ApiResponse<MatchResponse> getById(@PathVariable Long id) {
		return new ApiResponse<>(matchService.findById(id));
	}
}
