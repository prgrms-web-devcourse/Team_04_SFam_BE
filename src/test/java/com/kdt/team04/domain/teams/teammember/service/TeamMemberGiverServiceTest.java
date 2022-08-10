package com.kdt.team04.domain.teams.teammember.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.stream.Stream;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import com.kdt.team04.common.aws.s3.S3Uploader;
import com.kdt.team04.common.exception.BusinessException;
import com.kdt.team04.domain.teams.team.model.SportsCategory;
import com.kdt.team04.domain.teams.team.model.entity.Team;
import com.kdt.team04.domain.teams.teammember.dto.response.TeamMemberResponse;
import com.kdt.team04.domain.teams.teammember.model.TeamMemberRole;
import com.kdt.team04.domain.teams.teammember.model.entity.TeamMember;
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
		// given
		User leaderUser = createLeader();
		entityManager.persist(leaderUser);

		List<User> users = Stream.of("test1234", "test1235", "test1236", "test1237", "test1238")
			.map(n -> new User(n, n, "$2a$12$JB1zYmj1TfoylCds8Tt5ue//BQTWE2xO5HZn.MjZcpo.z.7LKagZ."))
			.peek(entityManager::persist)
			.toList();

		int teamMemberSize = users.size() + 1;

		Team team = createTeam(leaderUser);
		entityManager.persist(team);

		TeamMember teamLeader = new TeamMember(team, leaderUser, TeamMemberRole.LEADER);
		entityManager.persist(teamLeader);

		users.stream()
			.map(u -> new TeamMember(team, u, TeamMemberRole.MEMBER))
			.peek(entityManager::persist).toList();

		entityManager.flush();
		entityManager.clear();

		// when
		List<TeamMemberResponse> teamMemberResponses = teamMemberGiverService.findAllByTeamId(team.getId());

		// then
		assertThat(teamMemberResponses).hasSize(teamMemberSize);
	}

	@Test
	@DisplayName("팀 생성과 동시에 생성한 유저를 리더로 등록시킨다.")
	void successRegisterTeamLeader() {
		// given
		User leaderUser = createLeader();
		entityManager.persist(leaderUser);

		Team team = createTeam(leaderUser);
		entityManager.persist(team);

		// when
		teamMemberGiverService.registerTeamLeader(team.getId(), leaderUser.getId());

		entityManager.flush();
		entityManager.clear();

		// then
		List<TeamMemberResponse> teamMemberResponses = teamMemberGiverService.findAllByTeamId(team.getId());
		TeamMemberResponse leaderResponse = teamMemberResponses.get(0);

		assertThat(teamMemberResponses).hasSize(1);
		assertThat(leaderResponse.userId()).isEqualTo(leaderUser.getId());
		assertThat(leaderResponse.nickname()).isEqualTo(leaderUser.getNickname());
	}

	@Test
	@DisplayName("팀에 조회하려는 유저 아이디가 있을 경우 True를 반환한다.")
	void existsTeamMemberTrue() {
		// given
		User leaderUser = createLeader();
		entityManager.persist(leaderUser);

		User memberUser = createMember();
		entityManager.persist(memberUser);

		Team team = createTeam(leaderUser);
		entityManager.persist(team);

		TeamMember teamLeader = new TeamMember(team, leaderUser, TeamMemberRole.LEADER);
		TeamMember teamMember = new TeamMember(team, memberUser, TeamMemberRole.MEMBER);
		entityManager.persist(teamLeader);
		entityManager.persist(teamMember);

		entityManager.flush();
		entityManager.clear();

		// when
		boolean result = teamMemberGiverService.existsTeamMember(team.getId(), memberUser.getId());

		// then
		assertThat(result).isTrue();
	}

	@Test
	@DisplayName("팀에 조회하려는 유저 아이디가 없을 경우 False 반환한다.")
	void existsTeamMemberFalse() {
		// given
		User leaderUser = createLeader();
		entityManager.persist(leaderUser);

		Team team = createTeam(leaderUser);
		entityManager.persist(team);

		TeamMember teamLeader = new TeamMember(team, leaderUser, TeamMemberRole.LEADER);
		entityManager.persist(teamLeader);

		entityManager.flush();
		entityManager.clear();

		// when
		boolean result = teamMemberGiverService.existsTeamMember(team.getId(), 9999L);

		// then
		assertThat(result).isFalse();
	}

	@Test
	@DisplayName("팀전 대결 신청 시 팀의 인원이 충분하지 않다면 예외를 발생시킨다.")
	void hasEnoughMemberCountError() {
		// given
		User leaderUser = createLeader();
		entityManager.persist(leaderUser);

		User memberUser = createMember();
		entityManager.persist(memberUser);

		Team team = createTeam(leaderUser);
		entityManager.persist(team);

		TeamMember teamLeader = new TeamMember(team, leaderUser, TeamMemberRole.LEADER);
		TeamMember teamMember = new TeamMember(team, memberUser, TeamMemberRole.MEMBER);
		entityManager.persist(teamLeader);
		entityManager.persist(teamMember);

		// when | then
		assertThatThrownBy(() -> teamMemberGiverService.hasEnoughMemberCount(10, team.getId()))
			.isInstanceOf(BusinessException.class);
	}

	private User createLeader() {
		return new User("leader1234", "leader1234",
			"$2a$12$JB1zYmj1TfoylCds8Tt5ue//BQTWE2xO5HZn.MjZcpo.z.7LKagZ.");
	}

	private User createMember() {
		return new User("member1234", "member1234",
			"$2a$12$JB1zYmj1TfoylCds8Tt5ue//BQTWE2xO5HZn.MjZcpo.z.7LKagZ.");
	}

	private Team createTeam(User leaderUser) {
		return Team.builder()
			.name("teamA")
			.leader(leaderUser)
			.description("discription")
			.sportsCategory(SportsCategory.BADMINTON)
			.build();
	}

}