package com.kdt.team04.domain.team.dto;

import java.util.List;

import com.kdt.team04.domain.match.review.dto.MatchRecordResponse;
import com.kdt.team04.domain.match.review.dto.MatchReviewResponse;
import com.kdt.team04.domain.team.SportsCategory;
import com.kdt.team04.domain.teammember.dto.TeamMemberResponse;
import com.kdt.team04.domain.user.dto.UserResponse;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record TeamResponse(
	Long id,
	String teamName,
	String description,
	SportsCategory sportsCategory,
	List<TeamMemberResponse> members,
	MatchRecordResponse.TotalCount matchRecord,
	MatchReviewResponse.TotalCount matchReview,
	UserResponse leader
) {

	public record SimpleResponse(
		@Schema(description = "팀 아이디")
		Long id,

		@Schema(description = "팀명")
		String teamName,

		@Schema(description = "스포츠 종목")
		SportsCategory sportsCategory
	) { }
}
