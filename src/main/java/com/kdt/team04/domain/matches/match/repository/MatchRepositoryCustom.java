package com.kdt.team04.domain.matches.match.repository;

import com.kdt.team04.common.PageDto;
import com.kdt.team04.domain.matches.match.dto.MatchPagingCursor;
import com.kdt.team04.domain.matches.match.dto.response.MatchListViewResponse;

public interface MatchRepositoryCustom {
	PageDto.CursorResponse<MatchListViewResponse, MatchPagingCursor> findByLocationPaging(Double latitude,
		Double longitude, PageDto.MatchCursorPageRequest pageRequest);
}
