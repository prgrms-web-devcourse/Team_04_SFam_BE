package com.kdt.team04.domain.teams.teammember.service;

import java.util.List;
import java.util.stream.Stream;

import javax.persistence.EntityManager;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import com.kdt.team04.common.aws.s3.S3Uploader;
import com.kdt.team04.domain.teams.team.model.SportsCategory;
import com.kdt.team04.domain.teams.team.model.entity.Team;
import com.kdt.team04.domain.teams.teammember.dto.response.TeamMemberResponse;
import com.kdt.team04.domain.teams.teammember.model.entity.TeamMember;
import com.kdt.team04.domain.teams.teammember.model.TeamMemberRole;
import com.kdt.team04.domain.teams.teammember.service.TeamMemberGiverService;
import com.kdt.team04.domain.user.entity.User;

@SpringBootTest
@Transactional
class TeamMemberGiverServiceTest {

	@Autowired
	EntityManager entityManager;

	@Autowired
	TeamMemberGiverService teamMemberGiverService;

	@MockBean
	S3Uploader s3Uploader;

	@Test
	@DisplayName("팀 ID로 멤버 전체 조회")
	void findAllByTeamId() {
		List<User> users = Stream.of("test1234", "test1235", "test1236", "test1237", "test1238")
			.map(n -> new User(n, n, "$2a$12$JB1zYmj1TfoylCds8Tt5ue//BQTWE2xO5HZn.MjZcpo.z.7LKagZ."))
			.peek(entityManager::persist)
			.toList();

		Team team = Team.builder()
			.name("teamA")
			.leader(users.get(0))
			.description("discription")
			.sportsCategory(SportsCategory.BADMINTON)
			.build();

		entityManager.persist(team);

		List<TeamMember> teamMembers = users.stream()
			.map(u -> new TeamMember(team, u, TeamMemberRole.MEMBER))
			.peek(entityManager::persist).toList();

		entityManager.flush();
		entityManager.clear();

		List<TeamMemberResponse> teamMemberResponses = teamMemberGiverService.findAllByTeamId(team.getId());

		Assertions.assertThat(teamMemberResponses).hasSameSizeAs(teamMembers);
	}
}