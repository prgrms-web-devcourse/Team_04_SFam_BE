package com.kdt.team04.domain.teams.team.service;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kdt.team04.common.aws.s3.S3Uploader;
import com.kdt.team04.common.exception.BusinessException;
import com.kdt.team04.common.exception.EntityNotFoundException;
import com.kdt.team04.common.exception.ErrorCode;
import com.kdt.team04.common.file.ImagePath;
import com.kdt.team04.domain.matches.review.dto.response.MatchRecordTotalResponse;
import com.kdt.team04.domain.matches.review.dto.response.MatchReviewTotalResponse;
import com.kdt.team04.domain.matches.review.service.MatchRecordGiverService;
import com.kdt.team04.domain.matches.review.service.MatchReviewGiverService;
import com.kdt.team04.domain.teams.team.dto.QueryTeamLeaderResponse;
import com.kdt.team04.domain.teams.team.dto.TeamConverter;
import com.kdt.team04.domain.teams.team.dto.request.CreateTeamRequest;
import com.kdt.team04.domain.teams.team.dto.response.TeamResponse;
import com.kdt.team04.domain.teams.team.model.entity.Team;
import com.kdt.team04.domain.teams.team.repository.TeamRepository;
import com.kdt.team04.domain.teams.teammember.dto.response.TeamMemberResponse;
import com.kdt.team04.domain.teams.teammember.service.TeamMemberGiverService;
import com.kdt.team04.domain.user.UserConverter;
import com.kdt.team04.domain.user.dto.response.UserResponse;
import com.kdt.team04.domain.user.entity.User;
import com.kdt.team04.domain.user.service.UserService;

@Service
@Transactional(readOnly = true)
public class TeamService {

	private final TeamRepository teamRepository;
	private final TeamConverter teamConverter;
	private final UserConverter userConverter;
	private final UserService userService;
	private final TeamMemberGiverService teamMemberGiver;
	private final MatchRecordGiverService matchRecordGiver;
	private final MatchReviewGiverService matchReviewGiver;
	private final S3Uploader s3Uploader;

	public TeamService(TeamRepository teamRepository, TeamConverter teamConverter, UserConverter userConverter,
		UserService userService,
		TeamMemberGiverService teamMemberGiver, MatchRecordGiverService matchRecordGiver,
		MatchReviewGiverService matchReviewGiver, S3Uploader s3Uploader) {
		this.teamRepository = teamRepository;
		this.teamConverter = teamConverter;
		this.userConverter = userConverter;
		this.userService = userService;
		this.teamMemberGiver = teamMemberGiver;
		this.matchRecordGiver = matchRecordGiver;
		this.matchReviewGiver = matchReviewGiver;
		this.s3Uploader = s3Uploader;
	}

	@Transactional
	public Long create(Long userId, CreateTeamRequest requestDto) {
		boolean existsName = teamRepository.existsByName(requestDto.name());

		if (existsName) {
			throw new BusinessException(ErrorCode.TEAM_DUPLICATE_NAME,
				MessageFormat.format("teamName={0}, userId={1}", requestDto.name(), userId));
		}

		User user = userConverter.toUser(userService.findById(userId));
		Team savedTeam = teamRepository.save(
			Team.builder()
				.name(requestDto.name())
				.sportsCategory(requestDto.sportsCategory())
				.description(requestDto.description())
				.leader(user)
				.build()
		);
		teamMemberGiver.registerTeamLeader(savedTeam.getId(), user.getId());

		return savedTeam.getId();
	}

	public TeamResponse findById(Long id) {
		Team team = teamRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.TEAM_NOT_FOUND,
				MessageFormat.format("TeamId = {0}", id)));

		List<TeamMemberResponse> teamMemberResponses = teamMemberGiver.findAllByTeamId(id);
		MatchRecordTotalResponse totalRecord = matchRecordGiver.findByTeamTotalRecord(id);
		MatchReviewTotalResponse totalReview = matchReviewGiver.findByTeamTotalReview(id);
		UserResponse leader = userConverter.toUserResponse(team.getLeader());

		return teamConverter.toTeamResponse(team, leader, teamMemberResponses, totalRecord, totalReview);
	}

	public List<QueryTeamLeaderResponse> findByLeaderId(Long userId) {
		return teamRepository.findTeamLeaderByLeaderId(userId);
	}

	@Transactional
	public void uploadLogo(Long teamId, Long leaderId, MultipartFile file) {
		Team team = teamRepository.findByIdAndLeaderId(teamId, leaderId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_TEAM_LEADER,
				MessageFormat.format("TeamId = {0}", teamId)));

		Optional.ofNullable(team.getLogoImageUrl())
			.ifPresentOrElse(
				key -> s3Uploader.uploadByKey(file.getResource(), key),
				() -> {
					String key = s3Uploader.uploadByPath(file.getResource(), ImagePath.TEAMS_LOGO);
					team.updateLogoUrl(key);
				}
			);
	}

}
