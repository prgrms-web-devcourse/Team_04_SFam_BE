package com.kdt.team04.domain.matches.proposal.dto;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.kdt.team04.domain.matches.proposal.entity.MatchChat;
import com.kdt.team04.domain.matches.proposal.entity.MatchProposal;
import com.kdt.team04.domain.user.entity.User;

@Component
public class MatchChatConverter {

	public MatchChat toMatchChat(Long matchProposalId, Long userId, Long targetId, String content, LocalDateTime chattedAt) {
		MatchProposal matchProposal = MatchProposal.builder()
			.id(matchProposalId)
			.build();

		User user = User.builder()
			.id(userId)
			.build();

		User target = User.builder()
			.id(targetId)
			.build();

		return MatchChat.builder()
			.proposal(matchProposal)
			.user(user)
			.target(target)
			.content(content)
			.chattedAt(chattedAt)
			.build();
	}
}
