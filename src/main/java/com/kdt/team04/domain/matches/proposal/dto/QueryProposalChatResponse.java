package com.kdt.team04.domain.matches.proposal.dto;

import java.math.BigInteger;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.kdt.team04.domain.matches.proposal.dto.response.ChatLastResponse;
import com.kdt.team04.domain.user.dto.response.ChatTargetProfileResponse;

public class QueryProposalChatResponse {
	private final Long id;
	private final String content;
	private final ChatTargetProfileResponse target;
	private final ChatLastResponse lastChat;

	public QueryProposalChatResponse(
		BigInteger id,
		String content,
		String targetNickname,
		String lastChat
	) {
		this.id = id.longValue();
		this.content = content;
		this.target = new ChatTargetProfileResponse(targetNickname);
		this.lastChat = new ChatLastResponse(lastChat);
	}

	public Long getId() {
		return id;
	}

	public String getContent() {
		return content;
	}

	public ChatTargetProfileResponse getTarget() {
		return target;
	}

	public ChatLastResponse getLastChat() {
		return lastChat;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
			.append("id", id)
			.append("content", content)
			.append("target", target)
			.append("lastChat", lastChat)
			.toString();
	}
}
