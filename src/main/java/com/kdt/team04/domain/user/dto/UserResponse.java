package com.kdt.team04.domain.user.dto;

import java.util.List;

import com.kdt.team04.domain.matches.review.dto.MatchReviewResponse;
import com.kdt.team04.domain.team.dto.TeamResponse;
import com.kdt.team04.domain.user.entity.Location;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

public record UserResponse(
	Long id,
	String username,
	String password,
	String nickname,
	Location location,
	String profileImageUrl

) {

	@Builder
	public record FindProfile(
		@Schema(description = "회원 닉네임")
		String nickname,

		@Schema(description = "유저 프로필 url")
		String profileImageUrl,

		@Schema(description = "후기 정보")
		MatchReviewResponse.TotalCount review,

		@Schema(description = "소속 팀 목록")
		List<TeamResponse.SimpleResponse> teams
	) {
	}

	public record UserFindResponse(
		@Schema(description = "회원 고유 PK")
		Long id,
		@Schema(description = "회원 id")
		String username,
		@Schema(description = "회원 닉네임")
		String nickname,
		@Schema(description = "유저 프로필 url")
		String profileImageUrl
	) {
	}

	public record UpdateLocationResponse(
		@Schema(description = "사용자 위치 - 위도")
		double latitude,

		@Schema(description = "사용자 위치 - 경도")
		double longitude) {
	}

	public record AuthorResponse(
		@Schema(description = "회원 고유 PK")
		Long id,
		@Schema(description = "회원 닉네임")
		String nickname) {
	}

	public record ChatTargetProfile(
		@Schema(description = "채팅 상대 닉네임")
		String nickname
	) {
	}

	public record ChatWriterProfile(
		@Schema(description = "채팅 작성자")
		Long id
	) {
	}
}
