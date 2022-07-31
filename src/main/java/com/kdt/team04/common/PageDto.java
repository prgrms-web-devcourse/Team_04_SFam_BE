package com.kdt.team04.common;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

import javax.validation.constraints.AssertFalse;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import org.hibernate.validator.constraints.Range;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kdt.team04.domain.matches.match.entity.MatchStatus;
import com.kdt.team04.domain.team.SportsCategory;
import com.kdt.team04.domain.teaminvitation.entity.InvitationStatus;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.Builder;

public class PageDto {

	private PageDto() {
	}

	public record Request(@NotNull(message = "숫자를 입력해주세요")
						  @Positive Integer page,
						  @NotNull(message = "숫자를 입력해주세요")
						  @Range(min = 5, max = 10, message = "목록 단위는 5 ~ 10까지 가능합니다.") Integer size) {
		public Pageable getPageable(Sort sort) {
			return PageRequest.of(page - 1, size, sort);
		}
	}

	public static class TeamInvitationCursorPageRequest {
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
		@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
		@Parameter(description = "초대받은 일자 (yyyy-MM-dd HH:mm:ss)")
		private LocalDateTime createdAt;

		@Parameter(description = "마지막 조회 ID")
		private Long id;

		@NotNull(message = "사이즈는 필수입니다.")
		@Parameter(description = "페이징 사이즈")
		private Integer size;

		@NotNull(message = "초대 상태 값은 필수입니다.")
		private InvitationStatus status;

		public TeamInvitationCursorPageRequest(LocalDateTime createdAt, Long id, Integer size,
			InvitationStatus status) {
			this.createdAt = createdAt;
			this.id = id;
			this.size = size;
			this.status = status;
		}

		public InvitationStatus getStatus() {
			return status;
		}

		public LocalDateTime getCreatedAt() {
			return createdAt;
		}

		public Long getId() {
			return id;
		}

		public Integer getSize() {
			return size;
		}
	}

	public static class MatchCursorPageRequest {
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
		@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
		@Parameter(description = "마지막 조회 일자 (yyyy-MM-dd HH:mm:ss)")
		private LocalDateTime createdAt;

		@Parameter(description = "마지막 조회 ID")
		private Long id;

		@NotNull
		@Parameter(description = "페이징 사이즈")
		private Integer size;

		@Parameter(description = "매칭 종목")
		private SportsCategory category;

		@Parameter(description = "매칭 상태")
		private MatchStatus status;

		@Min(1)
		@Max(30)
		@Parameter(description = "검색 거리")
		private Double distance;

		@AssertFalse
		public boolean isValidCursor() {
			return id != null && createdAt == null || id == null && createdAt != null;
		}

		@Builder
		public MatchCursorPageRequest(LocalDateTime createdAt, Long id, Integer size, SportsCategory category,
			Double distance) {
			this.createdAt = createdAt;
			this.id = id;
			this.size = size;
			this.category = category;
			this.distance = distance;
		}

		public MatchStatus getStatus() {
			return status;
		}

		public void setStatus(MatchStatus status) {
			this.status = status;
		}

		public LocalDateTime getCreatedAt() {
			return createdAt;
		}

		public void setCreatedAt(LocalDateTime createdAt) {
			this.createdAt = createdAt;
		}

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public Integer getSize() {
			return size;
		}

		public void setSize(Integer size) {
			this.size = size;
		}

		public SportsCategory getCategory() {
			return category;
		}

		public void setCategory(SportsCategory category) {
			this.category = category;
		}

		public Double getDistance() {
			return distance;
		}

		public void setDistance(Double distance) {
			this.distance = distance;
		}
	}

	public static class Response<DTO, DOMAIN> {
		private final List<DTO> responses;
		private final int totalPage;
		private final int page;
		private final int size;
		private final int start;
		private final int end;
		private final boolean hasPrevious;
		private final boolean hasNext;
		private final List<Integer> pageNumbers;

		public Response(Page<DOMAIN> result, Function<DOMAIN, DTO> toResponse) {
			responses = result.stream().map(toResponse).toList();
			totalPage = result.getTotalPages();

			this.page = result.getPageable().getPageNumber() + 1;
			this.size = result.getPageable().getPageSize();

			int tempEnd = (int)(Math.ceil(page / (double)size)) * size;

			start = tempEnd - (size - 1);

			hasPrevious = start > 1;

			end = Math.min(totalPage, tempEnd);

			hasNext = totalPage > tempEnd;

			pageNumbers = IntStream.rangeClosed(start, end).boxed().toList();

		}

		public List<DTO> getResponses() {
			return responses;
		}

		public int getTotalPage() {
			return totalPage;
		}

		public int getPage() {
			return page;
		}

		public int getSize() {
			return size;
		}

		public int getStart() {
			return start;
		}

		public int getEnd() {
			return end;
		}

		public boolean hasPrevious() {
			return hasPrevious;
		}

		public boolean hasNext() {
			return hasNext;
		}

		public List<Integer> getPageNumbers() {
			return pageNumbers;
		}
	}

	public record CursorResponse<T, C>(List<T> values, Boolean hasNext, C cursor) {
	}
}
