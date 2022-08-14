package com.kdt.team04.domain.user.dto.response;

import java.util.List;

import com.kdt.team04.domain.matches.review.dto.response.MatchReviewTotalResponse;
import com.kdt.team04.domain.teams.team.dto.response.TeamSimpleResponse;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record FindProfileResponse(
	@Schema(description = "회원 닉네임")
	String nickname,

	@Schema(description = "회원 프로필 url")
	String profileImageUrl,

	@Schema(description = "지역 이름 (XX동)")
	String localName,

	@Schema(description = "후기 정보")
	MatchReviewTotalResponse review,

	@Schema(description = "소속 팀 목록")
	List<TeamSimpleResponse> teams
) {
}
