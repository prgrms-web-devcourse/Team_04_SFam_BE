package com.kdt.team04.domain.team.service;

import java.text.MessageFormat;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kdt.team04.common.exception.EntityNotFoundException;
import com.kdt.team04.common.exception.ErrorCode;
import com.kdt.team04.domain.team.dto.TeamConverter;
import com.kdt.team04.domain.team.dto.TeamResponse;
import com.kdt.team04.domain.team.entity.Team;
import com.kdt.team04.domain.team.repository.TeamRepository;

@Service
@Transactional(readOnly = true)
public class TeamGiverService {

	private final TeamRepository teamRepository;
	private final TeamConverter teamConverter;

	public TeamGiverService(TeamRepository teamRepository, TeamConverter teamConverter) {
		this.teamRepository = teamRepository;
		this.teamConverter = teamConverter;
	}

	public TeamResponse findById(Long id) {
		Team team = teamRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.TEAM_NOT_FOUND,
				MessageFormat.format("TeamId = {0}", id)));

		return teamConverter.toTeamResponse(team);
	}

}