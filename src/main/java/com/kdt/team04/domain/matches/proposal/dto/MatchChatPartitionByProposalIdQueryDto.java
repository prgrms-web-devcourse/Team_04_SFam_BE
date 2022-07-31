package com.kdt.team04.domain.matches.proposal.dto;

import java.math.BigInteger;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class MatchChatPartitionByProposalIdQueryDto {
	private final Long rowNumber;
	private final Long matchProposalId;
	private final String lastChat;

	public MatchChatPartitionByProposalIdQueryDto(BigInteger rowNumber, BigInteger matchProposalId, String lastChat) {
		this.rowNumber = rowNumber.longValue();
		this.matchProposalId = matchProposalId.longValue();
		this.lastChat = lastChat;
	}

	public Long getRowNumber() {
		return rowNumber;
	}

	public Long getMatchProposalId() {
		return matchProposalId;
	}

	public String getLastChat() {
		return lastChat;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
			.append("rowNumber", rowNumber)
			.append("matchProposalId", matchProposalId)
			.append("lastChat", lastChat)
			.toString();
	}
}
