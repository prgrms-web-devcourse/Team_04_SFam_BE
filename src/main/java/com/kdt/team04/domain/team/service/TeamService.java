package com.kdt.team04.domain.team.service;

import java.text.MessageFormat;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kdt.team04.common.exception.EntityNotFoundException;
import com.kdt.team04.common.exception.ErrorCode;
import com.kdt.team04.domain.team.SportsCategory;
import com.kdt.team04.domain.team.dto.TeamConverter;
import com.kdt.team04.domain.team.dto.TeamResponse;
import com.kdt.team04.domain.team.entity.Team;
import com.kdt.team04.domain.team.repository.TeamRepository;
import com.kdt.team04.domain.teammember.dto.TeamMemberResponse;
import com.kdt.team04.domain.teammember.service.TeamMemberGiverService;
import com.kdt.team04.domain.user.entity.User;
import com.kdt.team04.domain.user.service.UserService;

@Service
@Transactional(readOnly = true)
public class TeamService {

	private final TeamRepository teamRepository;
	private final TeamConverter teamConverter;
	private final UserService userService;
	private final TeamMemberGiverService teamMemberGiver;

	public TeamService(TeamRepository teamRepository, TeamConverter teamConverter, UserService userService,
		TeamMemberGiverService teamMemberGiver) {
		this.teamRepository = teamRepository;
		this.teamConverter = teamConverter;
		this.userService = userService;
		this.teamMemberGiver = teamMemberGiver;
	}

	@Transactional
	public TeamResponse create(Long userId, String teamName, SportsCategory sportsCategory, String description) {
		User user = teamConverter.toUser(userService.findById(userId));
		Team savedTeam = teamRepository.save(Team.builder()
			.teamName(teamName)
			.sportsCategory(sportsCategory)
			.description(description)
			.leader(user)
			.build());

		return teamConverter.toTeamResponse(savedTeam);
	}

	public TeamResponse findById(Long id) {
		Team team = teamRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.TEAM_NOT_FOUND,
				MessageFormat.format("TeamId = {0}", id)));
		List<TeamMemberResponse> teamMemberResponses = teamMemberGiver.findAllByTeamId(id);

		return teamConverter.toTeamResponse(team, teamMemberResponses);
	}
}
