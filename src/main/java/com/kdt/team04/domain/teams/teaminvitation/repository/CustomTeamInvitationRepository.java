package com.kdt.team04.domain.teams.teaminvitation.repository;

import com.kdt.team04.common.PageDto;
import com.kdt.team04.domain.teams.teaminvitation.dto.TeamInvitationCursor;
import com.kdt.team04.domain.teams.teaminvitation.dto.response.TeamInvitationResponse;

public interface CustomTeamInvitationRepository {
	PageDto.CursorResponse<TeamInvitationResponse, TeamInvitationCursor> getInvitations(
		Long targetId, PageDto.TeamInvitationCursorPageRequest request);
}
