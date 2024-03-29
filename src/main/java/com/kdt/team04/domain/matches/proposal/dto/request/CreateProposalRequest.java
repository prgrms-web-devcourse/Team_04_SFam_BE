package com.kdt.team04.domain.matches.proposal.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;

public record CreateProposalRequest(
	@Schema(description = "신청자 팀 ID")
	Long teamId,

	@Schema(description = "신청 내용, 2자 이상 30자 이하", required = true)
	@NotBlank
	@Size(min = 2, max = 30)
	String content
) {
}