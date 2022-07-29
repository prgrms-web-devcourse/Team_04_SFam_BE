package com.kdt.team04.domain.matches.request.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.kdt.team04.domain.matches.request.entity.MatchProposalStatus;
import com.querydsl.core.annotations.QueryProjection;

public class MatchProposalQueryDto {
	Long id;
	MatchProposalStatus status;
	Long matchProposerId;
	Long matchAuthorId;

	@QueryProjection
	public MatchProposalQueryDto(Long id, MatchProposalStatus status, Long matchProposerId, Long matchAuthorId) {
		this.id = id;
		this.status = status;
		this.matchProposerId = matchProposerId;
		this.matchAuthorId = matchAuthorId;
	}

	public Long getId() {
		return id;
	}

	public MatchProposalStatus getStatus() {
		return status;
	}

	public Long getMatchProposerId() {
		return matchProposerId;
	}

	public Long getMatchAuthorId() {
		return matchAuthorId;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
			.append("id", id)
			.append("status", status)
			.append("matchProposerId", matchProposerId)
			.append("matchAuthorId", matchAuthorId)
			.toString();
	}
}
