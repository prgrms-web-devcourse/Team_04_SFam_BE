package com.kdt.team04.domain.matches.proposal.service;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kdt.team04.common.exception.BusinessException;
import com.kdt.team04.common.exception.ErrorCode;
import com.kdt.team04.domain.matches.match.entity.MatchStatus;
import com.kdt.team04.domain.matches.proposal.dto.MatchChatConverter;
import com.kdt.team04.domain.matches.proposal.dto.MatchProposalQueryDto;
import com.kdt.team04.domain.matches.proposal.dto.MatchProposalResponse;
import com.kdt.team04.domain.matches.proposal.entity.MatchChat;
import com.kdt.team04.domain.matches.proposal.entity.MatchProposal;
import com.kdt.team04.domain.matches.proposal.entity.MatchProposalStatus;
import com.kdt.team04.domain.matches.proposal.repository.MatchChatRepository;

@Service
@Transactional(readOnly = true)
public class MatchChatService {

	private final MatchChatRepository matchChatRepository;
	private final MatchProposalGiverService matchProposalGiver;
	private final MatchChatConverter matchChatConverter;

	public MatchChatService(
		MatchChatRepository matchChatRepository,
		MatchProposalGiverService matchProposalGiver,
		MatchChatConverter matchChatConverter) {
		this.matchChatRepository = matchChatRepository;
		this.matchProposalGiver = matchProposalGiver;
		this.matchChatConverter = matchChatConverter;
	}

	@Transactional
	public void chat(Long proposalId, Long writerId, Long targetId, String content, LocalDateTime chattedAt) {
		MatchProposalQueryDto matchProposalDto = matchProposalGiver.findSimpleProposalById(proposalId);

		if (matchProposalDto.getStatus() != MatchProposalStatus.APPROVED) {
			throw new BusinessException(
				ErrorCode.MATCH_PROPOSAL_NOT_APPROVED,
				MessageFormat.format("proposalId = {0}", proposalId));
		}

		if (matchProposalDto.getMatchStatus() != MatchStatus.WAITING
			&& matchProposalDto.getStatus() != MatchProposalStatus.FIXED
		) {
			throw new BusinessException(ErrorCode.ANOTHER_MATCH_PROPOSAL_ALREADY_FIXED,
				MessageFormat.format("proposalId = {0}", proposalId));
		}

		checkCorrectChatPartner(matchProposalDto.getMatchProposerId(), matchProposalDto.getMatchAuthorId(), writerId,
			targetId);

		MatchChat matchChat = matchChatConverter.toMatchChat(matchProposalDto.getId(), writerId, targetId, content,
			chattedAt);
		matchChatRepository.save(matchChat);
	}

	private void checkCorrectChatPartner(Long proposerId, Long matchAuthorId, Long writerId, Long targetId) {
		if (
			(Objects.equals(proposerId, writerId) && Objects.equals(matchAuthorId, targetId))
				|| (Objects.equals(matchAuthorId, writerId) && Objects.equals(proposerId, targetId))
		) {
			return;
		}

		throw new BusinessException(ErrorCode.MATCH_CHAT_NOT_CORRECT_CHAT_PARTNER,
			MessageFormat.format(
				"proposerId= {0}, proposal match authorId = {1}, chat writerId = {2}, chat targetId = {3}",
				proposerId,
				matchAuthorId,
				writerId,
				targetId
			)
		);
	}

	@Transactional
	public void deleteAllByProposals(List<MatchProposalResponse.SimpleProposal> proposalResponses) {
		List<MatchProposal> proposals = proposalResponses.stream()
			.map((proposal -> MatchProposal.builder()
				.id(proposal.id())
				.build())).toList();

		matchChatRepository.deleteAllByProposalIn(proposals);
	}
}
