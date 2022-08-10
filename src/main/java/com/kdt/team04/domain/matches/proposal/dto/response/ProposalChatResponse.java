package com.kdt.team04.domain.matches.proposal.dto.response;

import com.kdt.team04.domain.matches.proposal.entity.MatchProposalStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record ProposalChatResponse(
	@Schema(description = "신청 Id(고유 PK)")
	Long id,

	@Schema(description = "신청 상태(값/설명) - WAITING/대기중, APPROVED/수락, REFUSE/거절, FIXED/대상확정(경기종료)")
	MatchProposalStatus status,

	@Schema(description = "신청 내용")
	String content,

	@Schema(description = "사용자가 매칭 공고글 작성자인지 여부")
	boolean isMatchAuthor
) {
}
