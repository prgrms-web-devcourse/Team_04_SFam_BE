package com.kdt.team04.domain.matches.review.service;

import static com.kdt.team04.domain.matches.match.model.MatchType.INDIVIDUAL_MATCH;

import java.text.MessageFormat;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kdt.team04.common.exception.BusinessException;
import com.kdt.team04.common.exception.ErrorCode;
import com.kdt.team04.domain.matches.proposal.dto.QueryMatchProposalResponse;
import com.kdt.team04.domain.matches.proposal.service.MatchProposalGiverService;
import com.kdt.team04.domain.matches.review.dto.MatchReviewConverter;
import com.kdt.team04.domain.matches.review.model.entity.MatchReview;
import com.kdt.team04.domain.matches.review.model.MatchReviewValue;
import com.kdt.team04.domain.matches.review.repository.MatchReviewRepository;

@Service
@Transactional(readOnly = true)
public class MatchReviewService {

	private final MatchReviewRepository matchReviewRepository;
	private final MatchProposalGiverService matchProposalGiver;

	private final MatchReviewConverter matchReviewConverter;

	public MatchReviewService(
		MatchReviewRepository matchReviewRepository,
		MatchProposalGiverService matchProposalGiver,
		MatchReviewConverter matchReviewConverter
	) {
		this.matchReviewRepository = matchReviewRepository;
		this.matchProposalGiver = matchProposalGiver;
		this.matchReviewConverter = matchReviewConverter;
	}

	@Transactional
	public Long review(Long matchId, MatchReviewValue review, Long userId) {
		QueryMatchProposalResponse proposalQueryDto = matchProposalGiver.findFixedProposalByMatchId(matchId);

		if (!proposalQueryDto.getMatchStatus().isEnded()) {
			throw new BusinessException(ErrorCode.MATCH_NOT_ENDED,
				MessageFormat.format("Match not ended with matchId = {0}, userId = {1}", matchId, userId));
		}

		if (!Objects.equals(proposalQueryDto.getProposerId(), userId)
			&& !Objects.equals(proposalQueryDto.getAuthorId(), userId)
		) {
			throw new BusinessException(ErrorCode.MATCH_ACCESS_DENIED,
				MessageFormat.format("matchId = {0} , userId = {1}", matchId, userId));
		}

		boolean existsReview = matchReviewRepository.existsByMatchIdAndUserId(matchId, userId);
		if (existsReview) {
			throw new BusinessException(ErrorCode.MATCH_REVIEW_ALREADY_EXISTS,
				MessageFormat.format("matchId = {0}, userId = {1}", matchId, userId));
		}

		MatchReview matchReview = proposalQueryDto.getMatchType() == INDIVIDUAL_MATCH ?
			createIndividualReview(proposalQueryDto, review, userId) :
			createTeamReview(proposalQueryDto, review, userId);

		matchReviewRepository.save(matchReview);

		return matchReview.getId();
	}

	private MatchReview createTeamReview(
		QueryMatchProposalResponse proposalDto,
		MatchReviewValue review,
		Long loginId
	) {
		Long teamId = Objects.equals(proposalDto.getAuthorId(), loginId) ?
			proposalDto.getAuthorTeamId() :
			proposalDto.getProposerTeamId();
		Long targetTeamId = Objects.equals(proposalDto.getAuthorId(), loginId) ?
			proposalDto.getProposerTeamId() :
			proposalDto.getAuthorTeamId();

		return matchReviewConverter.toTeamReview(proposalDto.getMatchId(), review, teamId, targetTeamId);
	}

	private MatchReview createIndividualReview(
		QueryMatchProposalResponse proposalDto,
		MatchReviewValue review,
		Long loginId
	) {
		Long userId = Objects.equals(proposalDto.getAuthorId(), loginId) ?
			proposalDto.getAuthorId() :
			proposalDto.getProposerId();
		Long targetId = Objects.equals(proposalDto.getAuthorId(), loginId) ?
			proposalDto.getProposerId() :
			proposalDto.getAuthorId();

		return matchReviewConverter.toIndividualReview(proposalDto.getMatchId(), review, userId, targetId);
	}
}
