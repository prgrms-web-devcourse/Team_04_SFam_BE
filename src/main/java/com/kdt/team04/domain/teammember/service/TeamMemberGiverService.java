package com.kdt.team04.domain.teammember.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kdt.team04.domain.team.entity.Team;
import com.kdt.team04.domain.teammember.dto.TeamMemberConverter;
import com.kdt.team04.domain.teammember.dto.TeamMemberResponse;
import com.kdt.team04.domain.teammember.entity.TeamMember;
import com.kdt.team04.domain.teammember.entity.TeamMemberRole;
import com.kdt.team04.domain.teammember.repository.TeamMemberRepository;
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
		List<TeamMember> teams = teamMemberRepository.findAllByTeamId(teamId);

		return teams.stream()
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

	public int countByTeamId(Long teamId){
		return teamMemberRepository.countAllByTeamId(teamId);
	}
}
