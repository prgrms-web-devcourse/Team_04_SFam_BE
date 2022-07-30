package com.kdt.team04.domain.matches.match.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

public class MatchPagingCursor
{
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@NotNull
	private LocalDateTime createdAt;

	@NotNull
	private Long id;

	protected MatchPagingCursor() {
	}

	public MatchPagingCursor(LocalDateTime createdAt, Long id) {
		this.createdAt = createdAt;
		this.id = id;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public Long getId() {
		return id;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.createdAt = createdAt;
	}

	public void setId(Long id) {
		this.id = id;
	}
}
