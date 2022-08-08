package com.kdt.team04.domain.matches.match.dto.response;

import java.time.LocalDate;

import com.kdt.team04.domain.matches.match.entity.MatchStatus;
import com.kdt.team04.domain.matches.match.entity.MatchType;
import com.kdt.team04.domain.team.SportsCategory;
import com.kdt.team04.domain.team.dto.response.TeamSimpleResponse;
import com.kdt.team04.domain.user.dto.response.AuthorResponse;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record MatchResponse(
	@Schema(description = "매칭 ID(고유 PK)")
	Long id,

	@Schema(description = "매칭 글 제목")
	String title,

	@Schema(description = "매칭 상태(값/설명) - WAITING/대기중, IN_GAME/모집완료, END/경기종료")
	MatchStatus status,

	@Schema(description = "스포츠 종목(값/설명) - BADMINTON/배드민턴, SOCCER/축구, BASEBALL/야구")
	SportsCategory sportsCategory,

	@Schema(description = "매칭 글 작성자")
	AuthorResponse author,

	@Schema(description = "매칭 참여 팀")
	TeamSimpleResponse team,

	@Schema(description = "매칭 참여 인원")
	int participants,

	@Schema(description = "매칭 날짜")
	LocalDate matchDate,

	@Schema(description = "매칭 타입(값/설명) - TEAM_MATCH/팀전 | INDIVIDUAL_MATCH/개인전")
	MatchType matchType,

	@Schema(description = "매칭 글 내용")
	String content
) {
}
