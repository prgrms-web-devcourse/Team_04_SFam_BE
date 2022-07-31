package com.kdt.team04.domain.matches.review.service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kdt.team04.common.exception.BusinessException;
import com.kdt.team04.common.exception.ErrorCode;
import com.kdt.team04.domain.matches.match.dto.MatchResponse;
import com.kdt.team04.domain.matches.match.entity.MatchStatus;
import com.kdt.team04.domain.matches.match.entity.MatchType;
import com.kdt.team04.domain.matches.match.service.MatchGiverService;
import com.kdt.team04.domain.matches.proposal.dto.MatchProposalResponse;
import com.kdt.team04.domain.matches.proposal.service.MatchProposalGiverService;
import com.kdt.team04.domain.matches.review.dto.MatchRecordConverter;
import com.kdt.team04.domain.matches.review.entity.MatchRecord;
import com.kdt.team04.domain.matches.review.entity.MatchRecordValue;
import com.kdt.team04.domain.matches.review.repository.MatchRecordRepository;
import com.kdt.team04.domain.team.dto.TeamResponse;
import com.kdt.team04.domain.user.dto.UserResponse;

@Service
@Transactional(readOnly = true)
public class MatchRecordService {

	private final MatchRecordRepository matchRecordRepository;
	private final MatchGiverService matchGiver;
	private final MatchProposalGiverService matchProposalGiver;
	private final MatchRecordConverter matchRecordConverter;

	public MatchRecordService(MatchRecordRepository matchRecordRepository, MatchGiverService matchGiver,
		MatchProposalGiverService matchProposalGiver, MatchRecordConverter matchRecordConverter) {
		this.matchRecordRepository = matchRecordRepository;
		this.matchGiver = matchGiver;
		this.matchProposalGiver = matchProposalGiver;
		this.matchRecordConverter = matchRecordConverter;
	}

	@Transactional
	public void endGame(Long matchId, Long proposalId, MatchRecordValue result, Long userId) {
		MatchResponse match = matchGiver.endGame(matchId, userId);
		UserResponse.AuthorResponse author = match.author();

		MatchProposalResponse.FixedProposal fixedProposal = matchProposalGiver.updateToFixed(proposalId);
		UserResponse.AuthorResponse proposer = fixedProposal.proposer();

		List<MatchRecord> records = new ArrayList<>();
		if (match.matchType() == MatchType.TEAM_MATCH) {
			TeamResponse.SimpleResponse authorTeam = match.team();
			TeamResponse.SimpleResponse proposerTeam = fixedProposal.proposerTeam();

			MatchRecord authorRecord = matchRecordConverter.toRecord(matchId, author.id(), authorTeam.id(), result);
			MatchRecord proposalRecord = matchRecordConverter.toRecord(matchId, proposer.id(), proposerTeam.id(), result.getReverseResult());

			records.add(authorRecord);
			records.add(proposalRecord);
		} else if (match.matchType() == MatchType.INDIVIDUAL_MATCH) {
			MatchRecord authorRecord = matchRecordConverter.toRecord(matchId, author.id(), null, result);
			MatchRecord proposalRecord = matchRecordConverter.toRecord(matchId, proposer.id(), null, result.getReverseResult());

			records.add(authorRecord);
			records.add(proposalRecord);
		}


		matchRecordRepository.saveAll(records);
	}
}
