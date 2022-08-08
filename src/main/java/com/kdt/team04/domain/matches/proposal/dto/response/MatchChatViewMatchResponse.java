package com.kdt.team04.domain.matches.proposal.dto.response;

import com.kdt.team04.domain.matches.match.model.MatchStatus;
import com.kdt.team04.domain.user.dto.response.ChatTargetProfileResponse;

import io.swagger.v3.oas.annotations.media.Schema;

public record MatchChatViewMatchResponse(
	@Schema(description = "매칭 공고 제목")
	String title,

	@Schema(description = "매칭 상태(값/설명) - WAITING/대기중, IN_GAME/모집완료, END/경기종료")
	MatchStatus status,

	@Schema(description = "채팅 상대 정보")
	ChatTargetProfileResponse targetProfile
) {
}
