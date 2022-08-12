package com.kdt.team04.domain.teams.team.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.kdt.team04.domain.teams.team.model.SportsCategory;
import com.querydsl.core.annotations.QueryProjection;

import io.swagger.v3.oas.annotations.media.Schema;

public class QueryTeamLeaderResponse {
	@Schema(description = "팀 아이디")
	private final Long id;

	@Schema(description = "팀명")
	private final String name;

	@Schema(description = "스포츠 종목(값/설명) - BADMINTON/배드민턴, SOCCER/축구, BASEBALL/야구")
	private final SportsCategory sportsCategory;

	@Schema(description = "팀 로고 이미지 url")
	private final String logoImageUrl;

	@Schema(description = "팀원 수")
	private final Long memberCount;

	@QueryProjection
	public QueryTeamLeaderResponse(
		Long id,
		String name,
		SportsCategory sportsCategory,
		String logoImageUrl,
		Long memberCount
	) {
		this.id = id;
		this.name = name;
		this.sportsCategory = sportsCategory;
		this.logoImageUrl = logoImageUrl;
		this.memberCount = memberCount;
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public SportsCategory getSportsCategory() {
		return sportsCategory;
	}

	public String getLogoImageUrl() {
		return logoImageUrl;
	}

	public Long getMemberCount() {
		return memberCount;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
			.append("id", id)
			.append("name", name)
			.append("sportsCategory", sportsCategory)
			.append("logoImageUrl", logoImageUrl)
			.append("memberCount", memberCount)
			.toString();
	}
}
