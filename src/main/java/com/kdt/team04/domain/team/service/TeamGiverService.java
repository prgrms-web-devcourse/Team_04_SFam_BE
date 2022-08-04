package com.kdt.team04.domain.team.service;

import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kdt.team04.common.exception.BusinessException;
import com.kdt.team04.common.exception.EntityNotFoundException;
import com.kdt.team04.common.exception.ErrorCode;
import com.kdt.team04.domain.team.dto.TeamConverter;
import com.kdt.team04.domain.team.dto.TeamResponse;
import com.kdt.team04.domain.team.entity.Team;
import com.kdt.team04.domain.team.repository.TeamRepository;
import com.kdt.team04.domain.user.UserConverter;
import com.kdt.team04.domain.user.dto.UserResponse;

@Service
@Transactional(readOnly = true)
public class TeamGiverService {

	private final TeamRepository teamRepository;
	private final TeamConverter teamConverter;
	private final UserConverter userConverter;

	public TeamGiverService(TeamRepository teamRepository, TeamConverter teamConverter, UserConverter userConverter) {
		this.teamRepository = teamRepository;
		this.teamConverter = teamConverter;
		this.userConverter = userConverter;
	}

	public TeamResponse findById(Long id) {
		Team team = teamRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.TEAM_NOT_FOUND,
				MessageFormat.format("TeamId = {0}", id)));
		UserResponse leader = userConverter.toUserResponse(team.getLeader());

		return teamConverter.toTeamResponse(team, leader);
	}

	public List<TeamResponse.SimpleResponse> findAllByTeamMemberUserId(Long userId) {
		return teamRepository.findAllByTeamMemberUserId(userId).stream()
			.map(team -> new TeamResponse.SimpleResponse(
					team.getId(), team.getName(), team.getSportsCategory(), team.getLogoImageUrl()
				)
			).toList();
	}

	public void verifyLeader(Long userId, Long teamId, Long leaderId) {
		if (!Objects.equals(userId, leaderId)) {
			throw new BusinessException(ErrorCode.NOT_TEAM_LEADER,
				MessageFormat.format("teamId = {0} , userId = {1}", teamId, userId));
		}
	}
}
