package com.kdt.team04.domain.teams.teammember.service;

import java.text.MessageFormat;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kdt.team04.common.exception.BusinessException;
import com.kdt.team04.common.exception.ErrorCode;
import com.kdt.team04.domain.teams.team.model.entity.Team;
import com.kdt.team04.domain.teams.teammember.dto.TeamMemberConverter;
import com.kdt.team04.domain.teams.teammember.dto.response.TeamMemberResponse;
import com.kdt.team04.domain.teams.teammember.model.entity.TeamMember;
import com.kdt.team04.domain.teams.teammember.model.TeamMemberRole;
import com.kdt.team04.domain.teams.teammember.repository.TeamMemberRepository;
import com.kdt.team04.domain.user.entity.User;

@Service
@Transactional(readOnly = true)
public class TeamMemberGiverService {

	private final TeamMemberRepository teamMemberRepository;
	private final TeamMemberConverter teamMemberConverter;

	public TeamMemberGiverService(TeamMemberRepository teamMemberRepository, TeamMemberConverter teamMemberConverter) {
		this.teamMemberRepository = teamMemberRepository;
		this.teamMemberConverter = teamMemberConverter;
	}

	public List<TeamMemberResponse> findAllByTeamId(Long teamId) {
		return teamMemberRepository.findAllByTeamId(teamId).stream()
			.map(teamMemberConverter::toTeamMemberResponse)
			.toList();
	}

	public boolean existsTeamMember(Long teamId, Long userId) {
		return teamMemberRepository.existsByTeamIdAndUserId(teamId, userId);
	}

	@Transactional
	public void registerTeamLeader(Long teamId, Long userId) {
		User user = teamMemberConverter.toUser(userId);
		Team team = teamMemberConverter.toTeam(teamId);

		TeamMember teamMember = new TeamMember(team, user, TeamMemberRole.LEADER);
		teamMemberRepository.save(teamMember);
	}

	public int countByTeamId(Long teamId) {
		return teamMemberRepository.countAllByTeamId(teamId);
	}

	public void hasEnoughMemberCount(int participants, Long teamId) {
		int teamMemberCount = teamMemberRepository.countAllByTeamId(teamId);

		if (teamMemberCount < participants) {
			throw new BusinessException(ErrorCode.MATCH_INVALID_PARTICIPANTS,
				MessageFormat.format("TeamMemberCount = {0} participants = {1}",
					teamMemberCount, participants));
		}
	}
}
