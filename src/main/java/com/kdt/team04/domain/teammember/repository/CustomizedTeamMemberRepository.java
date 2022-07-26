package com.kdt.team04.domain.teammember.repository;

public interface CustomizedTeamMemberRepository {

	boolean existsByTeamIdAndMemberId(Long teamId, Long userId);
}
