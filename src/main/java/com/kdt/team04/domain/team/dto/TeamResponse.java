package com.kdt.team04.domain.team.dto;

import java.util.List;

import com.kdt.team04.domain.matches.review.dto.MatchRecordResponse;
import com.kdt.team04.domain.matches.review.dto.MatchReviewResponse;
import com.kdt.team04.domain.team.SportsCategory;
import com.kdt.team04.domain.teammember.dto.TeamMemberResponse;
import com.kdt.team04.domain.user.dto.UserResponse;

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
	MatchRecordResponse.TotalCount matchRecord,

	@Schema(description = "팀 리뷰")
	MatchReviewResponse.TotalCount matchReview,

	@Schema(description = "팀 리더 정보")
	UserResponse leader,

	@Schema(description = "팀 로고 이미지 url")
	String logoImageUrl
) {

	public record SimpleResponse(
		@Schema(description = "팀 아이디")
		Long id,

		@Schema(description = "팀명")
		String name,

		@Schema(description = "스포츠 종목(값/설명) - BADMINTON/배드민턴, SOCCER/축구, BASEBALL/야구")
		SportsCategory sportsCategory,

		@Schema(description = "팀 로고 이미지 url")
		String logoImageUrl
	) {
	}
}
