package com.kdt.team04.domain.matches.match.controller;

import javax.validation.Valid;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
import com.kdt.team04.domain.matches.match.dto.request.MatchCreateRequest;
import com.kdt.team04.domain.matches.match.dto.request.MatchStatusUpdateRequest;
import com.kdt.team04.domain.matches.match.dto.response.MatchListViewResponse;
import com.kdt.team04.domain.matches.match.dto.response.MatchResponse;
import com.kdt.team04.domain.matches.match.service.MatchService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "매칭 공고 API")
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
		@RequestBody @Valid MatchCreateRequest request) {
		if (jwtAuthentication == null) {
			throw new NotAuthenticationException("Not Authenticated");
		}

		matchService.create(jwtAuthentication.id(), request);
	}

	@Operation(summary = "매치 공고 리스트 조회", description = "매칭 상태별, 종목별 공고 리스트를 최신글 순으로 커서방식 페이징한다.")
	@GetMapping
	public ApiResponse<PageDto.CursorResponse<MatchListViewResponse, MatchPagingCursor>> getWithCursorPaging(
		@AuthenticationPrincipal JwtAuthentication auth, @Valid PageDto.MatchCursorPageRequest pageRequest) {
		PageDto.CursorResponse<MatchListViewResponse, MatchPagingCursor> matches = matchService.findMatches(
			auth.id(), pageRequest);

		return new ApiResponse<>(matches);
	}

	@Operation(summary = "매치 공고 상세 조회", description = "매칭 공고의 세부 정보를 조회할 수 있다.")
	@GetMapping("/{id}")
	public ApiResponse<MatchResponse> getById(
		@Parameter(description = "매칭 공고 ID") @PathVariable Long id
	) {
		return new ApiResponse<>(matchService.findById(id));
	}

	@Operation(summary = "매칭 공고 삭제", description = "매칭 공고를 삭제할 수 있다.")
	@DeleteMapping("/{id}")
	public void delete(
		@Parameter(description = "매칭 공고 ID") @PathVariable Long id,
		@AuthenticationPrincipal JwtAuthentication auth
	) {
		if (auth == null) {
			throw new NotAuthenticationException("Not Authenticated");
		}

		matchService.delete(auth.id(), id);
	}

	@Operation(summary = "매치 모집 완료 및 취소", description = "매치 공고를 모집 완료 또는 모집 중으로 상태를 변경한다.")
	@PatchMapping("/{id}")
	public void updateStatus(
		@AuthenticationPrincipal JwtAuthentication authentication,
		@Parameter(description = "매칭 공고 ID") @PathVariable Long id,
		@Valid @RequestBody MatchStatusUpdateRequest request
	) {
		if (authentication == null) {
			throw new NotAuthenticationException("Not Authenticated");
		}

		matchService.updateStatusExceptEnd(id, authentication.id(), request.status());
	}
}
