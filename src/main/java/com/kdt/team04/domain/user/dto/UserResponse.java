package com.kdt.team04.domain.user.dto;

import java.util.List;

import com.kdt.team04.domain.matches.review.dto.MatchReviewResponse;
import com.kdt.team04.domain.team.dto.TeamResponse;
import com.kdt.team04.domain.user.entity.Location;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record UserResponse(
	@Schema(description = "회원 ID(고유 PK)")
	Long id,

	@Schema(description = "회원 아이디")
	String username,

	@Schema(description = "회원 비밀번호")
	String password,

	@Schema(description = "회원 닉네임")
	String nickname,

	@Schema(description = "회원 위치 정보")
	Location location,

	@Schema(description = "회원 프로필 url")
	String profileImageUrl
) {

	@Builder
	public record FindProfile(
		@Schema(description = "회원 닉네임")
		String nickname,

		@Schema(description = "회원 프로필 url")
		String profileImageUrl,

		@Schema(description = "후기 정보")
		MatchReviewResponse.TotalCount review,

		@Schema(description = "소속 팀 목록")
		List<TeamResponse.SimpleResponse> teams
	) {
	}

	public record UserFindResponse(
		@Schema(description = "회원 ID(고유 PK)")
		Long id,
		@Schema(description = "회원 아이디")
		String username,
		@Schema(description = "회원 닉네임")
		String nickname,
		@Schema(description = "회원 프로필 url")
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
		@Schema(description = "회원 ID(고유 PK)")
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
		@Schema(description = "채팅 작성자 ID(고유 PK)")
		Long id
	) {
	}
}
