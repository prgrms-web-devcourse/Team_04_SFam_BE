package com.kdt.team04.domain.matches.match.repository;

import com.kdt.team04.common.PageDto;
import com.kdt.team04.domain.matches.match.dto.MatchPagingCursor;
import com.kdt.team04.domain.matches.match.dto.response.QueryMatchListResponse;

public interface CustomMatchRepository {
	PageDto.CursorResponse<QueryMatchListResponse, MatchPagingCursor> findByLocationPaging(Double latitude,
		Double longitude, PageDto.MatchCursorPageRequest pageRequest);

	Double getDistance(Double latitude, Double longitude, Long matchId);
}
