package com.kdt.team04.domain.teammember.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kdt.team04.domain.teammember.dto.TeamMemberConverter;
import com.kdt.team04.domain.teammember.dto.TeamMemberResponse;
import com.kdt.team04.domain.teammember.entity.TeamMember;
import com.kdt.team04.domain.teammember.repository.TeamMemberRepository;

@Service
@Transactional
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
}
