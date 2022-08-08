package com.kdt.team04.domain.matches.review.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kdt.team04.domain.matches.match.dto.response.MatchResponse;
import com.kdt.team04.domain.matches.match.model.MatchType;
import com.kdt.team04.domain.matches.match.service.MatchGiverService;
import com.kdt.team04.domain.matches.proposal.dto.response.FixedProposalResponse;
import com.kdt.team04.domain.matches.proposal.service.MatchProposalGiverService;
import com.kdt.team04.domain.matches.review.dto.MatchRecordConverter;
import com.kdt.team04.domain.matches.review.model.entity.MatchRecord;
import com.kdt.team04.domain.matches.review.model.MatchRecordValue;
import com.kdt.team04.domain.matches.review.repository.MatchRecordRepository;
import com.kdt.team04.domain.teams.team.dto.response.TeamSimpleResponse;
import com.kdt.team04.domain.user.dto.response.AuthorResponse;

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
		AuthorResponse author = match.author();

		FixedProposalResponse fixedProposal = matchProposalGiver.updateToFixed(proposalId);
		AuthorResponse proposer = fixedProposal.proposer();

		List<MatchRecord> records = new ArrayList<>();
		if (match.matchType() == MatchType.TEAM_MATCH) {
			TeamSimpleResponse authorTeam = match.team();
			TeamSimpleResponse proposerTeam = fixedProposal.proposerTeam();

			MatchRecord authorRecord = matchRecordConverter.toRecord(matchId, author.id(), authorTeam.id(), result);
			MatchRecord proposalRecord = matchRecordConverter.toRecord(matchId, proposer.id(), proposerTeam.id(),
				result.getReverseResult());

			records.add(authorRecord);
			records.add(proposalRecord);
		} else if (match.matchType() == MatchType.INDIVIDUAL_MATCH) {
			MatchRecord authorRecord = matchRecordConverter.toRecord(matchId, author.id(), result);
			MatchRecord proposalRecord = matchRecordConverter.toRecord(matchId, proposer.id(),
				result.getReverseResult());

			records.add(authorRecord);
			records.add(proposalRecord);
		}

		matchRecordRepository.saveAll(records);
	}
}
