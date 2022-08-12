package com.kdt.team04.domain.matches.match.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;

public record MatchPagingCursor(
	@Schema(description = "다음 생성일 커서", pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonInclude
	@NotNull
	LocalDateTime createdAt,

	@Schema(description = "다음 생성일 커서", pattern = "yyyy-MM-dd")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonInclude
	@NotNull
	LocalDate matchDate,

	@Schema(description = "다음 Match ID 커서")
	@NotNull
	Long id
) {
}
