package com.kdt.team04.domain.matches.proposal.dto;

import java.math.BigInteger;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.kdt.team04.domain.matches.proposal.dto.response.LastChatResponse;
import com.kdt.team04.domain.user.dto.response.ChatTargetProfileResponse;

import io.swagger.v3.oas.annotations.media.Schema;

public class QueryProposalChatResponse {
	@Schema(description = "매칭 신청 ID(고유 PK)")
	private final Long id;

	@Schema(description = "매칭 신청 메시지")
	private final String content;

	@Schema(description = "채팅 대상")
	private final ChatTargetProfileResponse target;

	@Schema(description = "마지막 채팅 정보")
	private final LastChatResponse lastChat;

	public QueryProposalChatResponse(
		BigInteger id,
		String content,
		BigInteger targetId,
		String targetNickname,
		String targetProfileImageUrl,
		String lastChat
	) {
		this.id = id.longValue();
		this.content = content;
		this.target = new ChatTargetProfileResponse(targetId, targetNickname, targetProfileImageUrl);
		this.lastChat = new LastChatResponse(lastChat);
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

	public LastChatResponse getLastChat() {
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
