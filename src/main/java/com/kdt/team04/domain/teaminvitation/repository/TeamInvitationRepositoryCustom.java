package com.kdt.team04.domain.teaminvitation.repository;

import com.kdt.team04.common.PageDto;

public interface TeamInvitationRepositoryCustom {
	PageDto.CursorResponse getInvitations(Long targetId, PageDto.TeamInvitationCursorPageRequest request);
}
