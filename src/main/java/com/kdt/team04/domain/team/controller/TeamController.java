package com.kdt.team04.domain.team.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kdt.team04.common.ApiResponse;
import com.kdt.team04.common.security.jwt.JwtAuthentication;
import com.kdt.team04.domain.team.dto.TeamRequest;
import com.kdt.team04.domain.team.dto.TeamResponse;
import com.kdt.team04.domain.team.service.TeamService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Team API")
@RestController
@RequestMapping("/api/teams")
public class TeamController {

	private final TeamService teamService;

	public TeamController(TeamService teamService) {
		this.teamService = teamService;
	}

	@Operation(summary = "팀을 생성한다.", description = "새로운 팀을 생성할 수 있습니다.")
	@PostMapping
	public ApiResponse<TeamResponse> creating(@AuthenticationPrincipal JwtAuthentication jwtAuthentication,
		@RequestBody TeamRequest.CreateRequest request) {
		return new ApiResponse<>(
			teamService.create(jwtAuthentication.id(), request.name(), request.sportsCategory(),
				request.description()));
	}

	@Operation(summary = "팀 프로필 조회", description = "해당 id의 팀 프로필을 조회할 수 있습니다.")
	@GetMapping("/{id}")
	public ApiResponse<TeamResponse> getById(@PathVariable Long id) {
		return new ApiResponse<>(teamService.findById(id));
	}

}
