package com.kdt.team04.domain.teaminvitation.repository;

import com.kdt.team04.common.PageDto;
import com.kdt.team04.domain.teaminvitation.dto.TeamInvitationCursor;
import com.kdt.team04.domain.teaminvitation.dto.TeamInvitationResponse;

public interface TeamInvitationRepositoryCustom {
	PageDto.CursorResponse<TeamInvitationResponse.InvitesResponse, TeamInvitationCursor> getInvitations(
		Long targetId, PageDto.TeamInvitationCursorPageRequest request);
}
