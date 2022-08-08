package com.kdt.team04.domain.teams.team.dto.response;

import java.util.List;

import com.kdt.team04.domain.matches.review.dto.response.MatchRecordTotalResponse;
import com.kdt.team04.domain.matches.review.dto.response.MatchReviewTotalResponse;
import com.kdt.team04.domain.teams.team.model.SportsCategory;
import com.kdt.team04.domain.teams.teammember.dto.response.TeamMemberResponse;
import com.kdt.team04.domain.user.dto.response.UserResponse;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record TeamResponse(
	@Schema(description = "팀 ID(고유 PK)")
	Long id,

	@Schema(description = "팀 이름")
	String name,

	@Schema(description = "팀 설명")
	String description,

	@Schema(description = "팀 종목(값/설명) - BADMINTON/배드민턴, SOCCER/축구, BASEBALL/야구", required = true)
	SportsCategory sportsCategory,

	@Schema(description = "팀원 정보")
	List<TeamMemberResponse> members,

	@Schema(description = "팀 전적")
	MatchRecordTotalResponse matchRecord,

	@Schema(description = "팀 리뷰")
	MatchReviewTotalResponse matchReview,

	@Schema(description = "팀 리더 정보")
	UserResponse leader,

	@Schema(description = "팀 로고 이미지 url")
	String logoImageUrl
) {
}
