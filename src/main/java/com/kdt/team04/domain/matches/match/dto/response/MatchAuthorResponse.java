package com.kdt.team04.domain.matches.match.dto.response;

import com.kdt.team04.domain.matches.match.entity.MatchStatus;
import com.kdt.team04.domain.user.dto.response.AuthorResponse;

public record MatchAuthorResponse(
	Long id,
	String title,
	MatchStatus status,
	AuthorResponse author
) {
}
