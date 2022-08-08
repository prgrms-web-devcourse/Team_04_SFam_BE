package com.kdt.team04.domain.matches.proposal.service;

import static java.util.stream.Collectors.toMap;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kdt.team04.common.exception.BusinessException;
import com.kdt.team04.common.exception.ErrorCode;
import com.kdt.team04.domain.matches.match.entity.MatchStatus;
import com.kdt.team04.domain.matches.proposal.dto.MatchChatConverter;
import com.kdt.team04.domain.matches.proposal.dto.MatchChatPartitionByProposalIdQueryDto;
import com.kdt.team04.domain.matches.proposal.dto.MatchProposalSimpleQueryDto;
import com.kdt.team04.domain.matches.proposal.dto.response.ChatLastResponse;
import com.kdt.team04.domain.matches.proposal.dto.response.ChatResponse;
import com.kdt.team04.domain.matches.proposal.dto.response.ChattingResponse;
import com.kdt.team04.domain.matches.proposal.dto.response.ProposalChatMatchResponse;
import com.kdt.team04.domain.matches.proposal.dto.response.ProposalIdResponse;
import com.kdt.team04.domain.matches.proposal.entity.MatchChat;
import com.kdt.team04.domain.matches.proposal.entity.MatchProposal;
import com.kdt.team04.domain.matches.proposal.entity.MatchProposalStatus;
import com.kdt.team04.domain.matches.proposal.repository.MatchChatRepository;
import com.kdt.team04.domain.user.dto.response.ChatWriterProfileResponse;

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
		MatchProposalSimpleQueryDto matchProposalDto = matchProposalGiver.findSimpleProposalById(proposalId);

		if (matchProposalDto.getStatus() != MatchProposalStatus.APPROVED) {
			throw new BusinessException(
				ErrorCode.PROPOSAL_NOT_APPROVED,
				MessageFormat.format("proposalId = {0}", proposalId));
		}

		if (matchProposalDto.getMatchStatus() != MatchStatus.WAITING
			&& matchProposalDto.getStatus() != MatchProposalStatus.FIXED
		) {
			throw new BusinessException(ErrorCode.ANOTHER_PROPOSAL_ALREADY_FIXED,
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

	public Map<Long, ChatLastResponse> findAllLastChats(List<Long> matchProposalIds) {
		List<MatchChatPartitionByProposalIdQueryDto> chatQueryDtos
			= matchChatRepository.findAllPartitionByProposalIdOrderByChattedAtDesc(matchProposalIds);

		Map<Long, ChatLastResponse> lastChats = chatQueryDtos.stream()
			.filter(chat -> chat.getRowNumber() == 1L)
			.collect(toMap(
				MatchChatPartitionByProposalIdQueryDto::getMatchProposalId,
				chat -> new ChatLastResponse(chat.getLastChat())
			));

		return lastChats;
	}

	public ChattingResponse findChatsByProposalId(Long proposalId, Long userId) {
		ProposalChatMatchResponse match
			= matchProposalGiver.findChatMatchByProposalId(proposalId, userId);

		List<MatchChat> matchChats = matchChatRepository.findAllByProposalId(proposalId);
		List<ChatResponse> chats = matchChats.stream()
			.map(chat -> {
				ChatWriterProfileResponse writer = new ChatWriterProfileResponse(chat.getUser().getId());

				return new ChatResponse(
					chat.getContent(),
					chat.getChattedAt(),
					writer
				);
			})
			.toList();

		return new ChattingResponse(match, chats);
	}

	@Transactional
	public void deleteAllByProposals(List<ProposalIdResponse> proposalResponses) {
		List<MatchProposal> proposals = proposalResponses.stream()
			.map((proposal -> MatchProposal.builder()
				.id(proposal.id())
				.build())).toList();

		matchChatRepository.deleteAllByProposalIn(proposals);
	}
}
