package com.kdt.team04.domain.matches.proposal.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.kdt.team04.domain.matches.match.entity.MatchStatus;
import com.kdt.team04.domain.matches.match.entity.MatchType;
import com.querydsl.core.annotations.QueryProjection;

public class QueryMatchProposalResponse {
	private Long id;
	private Long proposerId;
	private Long proposerTeamId;

	private Long matchId;
	private MatchStatus matchStatus;
	private MatchType matchType;
	private Long authorId;
	private Long authorTeamId;

	@QueryProjection
	public QueryMatchProposalResponse(
		Long id,
		Long proposerId,
		Long proposerTeamId,
		Long matchId,
		MatchStatus matchStatus,
		MatchType matchType,
		Long authorId,
		Long authorTeamId
	) {
		this.id = id;
		this.proposerId = proposerId;
		this.proposerTeamId = proposerTeamId;
		this.matchId = matchId;
		this.matchStatus = matchStatus;
		this.matchType = matchType;
		this.authorId = authorId;
		this.authorTeamId = authorTeamId;
	}

	public Long getId() {
		return id;
	}

	public Long getProposerId() {
		return proposerId;
	}

	public Long getProposerTeamId() {
		return proposerTeamId;
	}

	public Long getMatchId() {
		return matchId;
	}

	public MatchStatus getMatchStatus() {
		return matchStatus;
	}

	public MatchType getMatchType() {
		return matchType;
	}

	public Long getAuthorId() {
		return authorId;
	}

	public Long getAuthorTeamId() {
		return authorTeamId;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
			.append("id", id)
			.append("proposerId", proposerId)
			.append("proposerTeamId", proposerTeamId)
			.append("matchId", matchId)
			.append("matchStatus", matchStatus)
			.append("matchType", matchType)
			.append("authorId", authorId)
			.append("authorTeamId", authorTeamId)
			.toString();
	}
}
