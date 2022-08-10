package com.kdt.team04.domain.teams.teaminvitation.repository;

import java.util.List;

import com.kdt.team04.common.PageDto;
import com.kdt.team04.domain.teams.teaminvitation.dto.TeamInvitationCursor;
import com.kdt.team04.domain.teams.teaminvitation.dto.response.TeamInvitationResponse;
import com.kdt.team04.domain.teams.teaminvitation.model.InvitationStatus;

public interface CustomTeamInvitationRepository {
	PageDto.CursorResponse<TeamInvitationResponse, TeamInvitationCursor> getInvitations(
		Long targetId, PageDto.TeamInvitationCursorPageRequest request);

	boolean existsByTeamIdAndTargetIdAndStatusIn(Long teamId, Long targetId, List<InvitationStatus> statusList);
}
