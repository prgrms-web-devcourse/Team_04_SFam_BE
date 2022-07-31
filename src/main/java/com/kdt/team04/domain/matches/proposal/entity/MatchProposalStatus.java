package com.kdt.team04.domain.matches.proposal.entity;

public enum MatchProposalStatus {
	WAITING, APPROVED, REFUSE, FIXED;

	public boolean isApproved() {
		return this == MatchProposalStatus.APPROVED;
	}
}
