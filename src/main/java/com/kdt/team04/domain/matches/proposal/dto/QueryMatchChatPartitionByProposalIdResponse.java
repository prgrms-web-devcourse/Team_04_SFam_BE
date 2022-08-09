package com.kdt.team04.domain.matches.proposal.dto;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class QueryMatchChatPartitionByProposalIdResponse {
	private final Long rowNumber;
	private final Long matchProposalId;
	private final String lastChat;
	private final LocalDateTime lastChatDate;

	public QueryMatchChatPartitionByProposalIdResponse(
		BigInteger rowNumber,
		BigInteger matchProposalId,
		String lastChat,
		Timestamp lastChatDate
	) {
		this.rowNumber = rowNumber.longValue();
		this.matchProposalId = matchProposalId.longValue();
		this.lastChat = lastChat;
		this.lastChatDate = lastChatDate.toLocalDateTime();
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

	public LocalDateTime getLastChatDate() {
		return lastChatDate;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
			.append("rowNumber", rowNumber)
			.append("matchProposalId", matchProposalId)
			.append("lastChat", lastChat)
			.append("lastChatDate", lastChatDate)
			.toString();
	}
}
