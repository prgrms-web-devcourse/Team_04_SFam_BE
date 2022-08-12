package com.kdt.team04.domain.teams.team.repository;

import java.util.List;

import com.kdt.team04.domain.teams.team.dto.QueryTeamLeaderResponse;

public interface CustomTeamRepository {
	List<QueryTeamLeaderResponse> findTeamLeaderByLeaderId(Long id);
}
