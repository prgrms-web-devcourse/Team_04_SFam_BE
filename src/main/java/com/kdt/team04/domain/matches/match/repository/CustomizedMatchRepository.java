package com.kdt.team04.domain.matches.match.repository;

import java.util.List;

import com.kdt.team04.common.PageDto;
import com.kdt.team04.domain.matches.match.dto.MatchPagingCursor;
import com.kdt.team04.domain.matches.match.dto.MatchResponse;

public interface CustomizedMatchRepository {
	PageDto.CursorResponse<MatchResponse.ListViewResponse, MatchPagingCursor> findByLocationPaging(Double latitude,
		Double longitude, PageDto.MatchCursorPageRequest pageRequest);
}
