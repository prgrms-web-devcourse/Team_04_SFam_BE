package com.kdt.team04.domain.team.service;

import com.kdt.team04.domain.team.Category;
import com.kdt.team04.domain.team.dto.TeamResponse;

public interface TeamService {
	TeamResponse create(Long id, String name, Category sportsCategory, String description);

	TeamResponse findById(Long id);
}
