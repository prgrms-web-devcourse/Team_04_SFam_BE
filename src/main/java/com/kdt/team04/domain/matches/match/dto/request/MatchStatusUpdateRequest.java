package com.kdt.team04.domain.matches.match.dto.request;

import javax.validation.constraints.NotNull;

import com.kdt.team04.domain.matches.match.entity.MatchStatus;

import io.swagger.v3.oas.annotations.media.Schema;

public record MatchStatusUpdateRequest(
	@Schema(description = "매칭 상태(값/설명) - WAITING/대기중, IN_GAME/모집완료, END/경기종료", required = true)
	@NotNull
	MatchStatus status
) {
}
