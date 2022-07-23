package com.kdt.team04.domain.team.dto;

import java.time.LocalDateTime;

import com.kdt.team04.domain.team.SportsCategory;

import lombok.Builder;

@Builder
public record TeamResponse(
	Long id,
	String teamName,
	String description,
	SportsCategory sportsCategory,
	LocalDateTime createdAt,
	LocalDateTime updatedAt) {
}
