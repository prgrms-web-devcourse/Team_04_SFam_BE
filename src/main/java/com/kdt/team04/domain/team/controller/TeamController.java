package com.kdt.team04.domain.team.controller;

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
import org.springframework.web.multipart.MultipartFile;

import com.google.common.net.MediaType;
import com.kdt.team04.common.ApiResponse;
import com.kdt.team04.common.exception.BusinessException;
import com.kdt.team04.common.exception.ErrorCode;
import com.kdt.team04.common.exception.NotAuthenticationException;
import com.kdt.team04.common.security.jwt.JwtAuthentication;
import com.kdt.team04.domain.team.dto.request.TeamCreateRequest;
import com.kdt.team04.domain.team.dto.response.TeamResponse;
import com.kdt.team04.domain.team.dto.response.TeamSimpleResponse;
import com.kdt.team04.domain.team.service.TeamService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "팀 API")
@RestController
@RequestMapping("/api/teams")
public class TeamController {

	private final TeamService teamService;

	public TeamController(TeamService teamService) {
		this.teamService = teamService;
	}

	@Operation(summary = "팀 생성", description = "새로운 팀을 생성한다.")
	@PostMapping
	public void create(
		@AuthenticationPrincipal JwtAuthentication jwtAuthentication,
		@RequestBody @Valid TeamCreateRequest requestDto
	) {
		if (jwtAuthentication == null) {
			throw new NotAuthenticationException("Not Authenticated");
		}

		teamService.create(jwtAuthentication.id(), requestDto);
	}

	@Operation(summary = "팀 프로필 조회", description = "해당 ID의 팀 프로필을 조회한다.")
	@GetMapping("/{id}")
	public ApiResponse<TeamResponse> getById(
		@Parameter(description = "팀 ID") @PathVariable Long id
	) {
		TeamResponse team = teamService.findById(id);

		return new ApiResponse<>(team);
	}

	@Operation(summary = "해당 회원이 리더인 팀 조회", description = "해당 ID의 회원이 리더인 팀을 조회한다.")
	@GetMapping("/me/leader")
	public ApiResponse<List<TeamSimpleResponse>> getByLeaderId(
		@AuthenticationPrincipal JwtAuthentication jwtAuthentication
	) {
		if (jwtAuthentication == null) {
			throw new NotAuthenticationException("Not Authenticated");
		}

		List<TeamSimpleResponse> teams = teamService.findByLeaderId(jwtAuthentication.id());

		return new ApiResponse<>(teams);
	}

	@Operation(summary = "팀 로고 이미지 업데이트", description = "로고 이미지 파일을 받아 팀 로고 이미지를 업데이트 한다.")
	@PatchMapping("/{id}/logo")
	public void uploadLogo(
		@Parameter(description = "팀 ID") @PathVariable Long id,
		@AuthenticationPrincipal JwtAuthentication auth,
		MultipartFile file
	) {
		if (auth == null) {
			throw new NotAuthenticationException("not authenticated");
		}
		if (file.isEmpty() || !file.getContentType().startsWith(MediaType.ANY_IMAGE_TYPE.type())) {
			throw new BusinessException(ErrorCode.INVALID_FILE_TYPE,
				"파일이 첨부되지 않았거나 지원하지 않는 타입입니다.");
		}

		teamService.uploadLogo(id, auth.id(), file);
	}

}
