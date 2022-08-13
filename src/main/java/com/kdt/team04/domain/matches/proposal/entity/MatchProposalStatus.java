package com.kdt.team04.domain.matches.proposal.entity;

public enum MatchProposalStatus {
	WAITING, APPROVED, REFUSE, FIXED;

	public boolean isWaiting() {
		return this == MatchProposalStatus.WAITING;
	}

	public boolean isApproved() {
		return this == MatchProposalStatus.APPROVED;
	}

	public boolean isFixed() {
		return this == MatchProposalStatus.FIXED;
	}
}
