package com.kdt.team04.domain.matches.proposal.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kdt.team04.domain.user.dto.response.ChatTargetProfileResponse;

import io.swagger.v3.oas.annotations.media.Schema;

public record ChatRoomResponse(
	@Schema(description = "매칭 신청 ID(고유 PK)")
	Long id,

	@Schema(description = "매칭 신청 메시지")
	String content,

	@Schema(description = "채팅 대상")
	ChatTargetProfileResponse target,

	@Schema(description = "마지막 채팅 정보")
	LastChatResponse lastChat,

	//== 신청 목록 정렬용(신청 일자 | 마지막 채팅 일자 기준 내림차순) ==//
	@JsonIgnore
	LocalDateTime sortDate
) {
}
