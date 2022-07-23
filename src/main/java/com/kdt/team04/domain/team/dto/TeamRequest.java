package com.kdt.team04.domain.team.dto;

import com.kdt.team04.domain.team.Category;

public record TeamRequest() {
	public record CreateRequest(String name,
								String description,
								Category sportsCategory) {
	}
}
