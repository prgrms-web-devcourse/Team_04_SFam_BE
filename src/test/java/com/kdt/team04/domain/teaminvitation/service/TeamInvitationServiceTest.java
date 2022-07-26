package com.kdt.team04.domain.teaminvitation.service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.kdt.team04.common.exception.BusinessException;
import com.kdt.team04.domain.team.SportsCategory;
import com.kdt.team04.domain.team.dto.TeamConverter;
import com.kdt.team04.domain.team.entity.Team;
import com.kdt.team04.domain.teaminvitation.controller.TeamInvitationRequest;
import com.kdt.team04.domain.teaminvitation.dto.TeamInvitationResponse;
import com.kdt.team04.domain.teammember.entity.TeamMember;
import com.kdt.team04.domain.teammember.entity.TeamMemberRole;
import com.kdt.team04.domain.user.entity.User;

@SpringBootTest
@Transactional
class TeamInvitationServiceTest {

	@Autowired
	TeamInvitationService teamInvitationService;

	@Autowired
	EntityManager entityManager;

	@Autowired
	TeamConverter teamConverter;

	@Test
	@DisplayName("팀 초대 성공")
	void testInviteSuccess() {
		//given
		User userA = new User("test1234", "nickname", "$2a$12$JB1zYmj1TfoylCds8Tt5ue//BQTWE2xO5HZn.MjZcpo.z.7LKagZ.");
		User userB = new User("test4567", "nickname2", "$2a$12$JB1zYmj1TfoylCds8Tt5ue//BQTWE2xO5HZn.MjZcpo.z.7LKagZ.");

		entityManager.persist(userA);
		entityManager.persist(userB);
		Team team = Team.builder()
			.teamName("teamA")
			.description("description")
			.sportsCategory(SportsCategory.BADMINTON)
			.leader(userA)
			.build();
		entityManager.persist(team);
		TeamMember teamMemberA = new TeamMember(team, userA, TeamMemberRole.LEADER);
		entityManager.persist(teamMemberA);

		entityManager.flush();
		TeamInvitationRequest teamInvitationRequest = new TeamInvitationRequest(team.getId(), userB.getId());

		//when
		TeamInvitationResponse.InviteResponse response = teamInvitationService.invite(userA.getId(),
			teamInvitationRequest);

		//then
		Assertions.assertThat(response.invitationId()).isNotNull();

	}

	@Test
	@DisplayName("이미 팀원인 유저는 초대할 수 없다.")
	void testInviteAlreadyTeamMember() {
		//given
		User userA = new User("test1234", "nickname", "$2a$12$JB1zYmj1TfoylCds8Tt5ue//BQTWE2xO5HZn.MjZcpo.z.7LKagZ.");
		User userB = new User("test4567", "nickname2", "$2a$12$JB1zYmj1TfoylCds8Tt5ue//BQTWE2xO5HZn.MjZcpo.z.7LKagZ.");

		entityManager.persist(userA);
		entityManager.persist(userB);
		Team team = Team.builder()
			.teamName("teamA")
			.description("description")
			.sportsCategory(SportsCategory.BADMINTON)
			.leader(userA)
			.build();

		entityManager.persist(team);
		TeamMember teamMemberA = new TeamMember(team, userA, TeamMemberRole.LEADER);
		TeamMember teamMemberB = new TeamMember(team, userB, TeamMemberRole.MEMBER);
		entityManager.persist(teamMemberA);
		entityManager.persist(teamMemberB);
		entityManager.flush();
		entityManager.clear();
		TeamInvitationRequest teamInvitationRequest = new TeamInvitationRequest(team.getId(), userB.getId());

		//when, then
		Assertions.assertThatThrownBy(() -> teamInvitationService.invite(userA.getId(), teamInvitationRequest))
			.isInstanceOf(BusinessException.class);
	}

	@Test
	@DisplayName("이미 초대를 보냈으면 초대할 수 없다.")
	void testInviteAlreadySendInviteFail() {
		//given
		User userA = new User("test1234", "nickname", "$2a$12$JB1zYmj1TfoylCds8Tt5ue//BQTWE2xO5HZn.MjZcpo.z.7LKagZ.");
		User userB = new User("test4567", "nickname2", "$2a$12$JB1zYmj1TfoylCds8Tt5ue//BQTWE2xO5HZn.MjZcpo.z.7LKagZ.");

		entityManager.persist(userA);
		entityManager.persist(userB);
		Team team = Team.builder()
			.teamName("teamA")
			.description("description")
			.sportsCategory(SportsCategory.BADMINTON)
			.leader(userA)
			.build();

		entityManager.persist(team);
		TeamMember teamMemberA = new TeamMember(team, userA, TeamMemberRole.LEADER);
		entityManager.persist(teamMemberA);
		entityManager.flush();
		entityManager.clear();
		TeamInvitationRequest teamInvitationRequest = new TeamInvitationRequest(team.getId(), userB.getId());
		teamInvitationService.invite(userA.getId(), teamInvitationRequest);
		teamInvitationService.invite(userA.getId(), teamInvitationRequest);

		//when, then
		Assertions.assertThatThrownBy(() -> entityManager.flush()).isInstanceOf(PersistenceException.class);
	}

	@Test
	@DisplayName("팀 리더가 아닌사람은 초대할 수 없다.")
	void testNotTeamLeaderCantInvite() {
		//given
		User userA = new User("test1234", "nickname", "$2a$12$JB1zYmj1TfoylCds8Tt5ue//BQTWE2xO5HZn.MjZcpo.z.7LKagZ.");
		User userB = new User("test4567", "nickname2", "$2a$12$JB1zYmj1TfoylCds8Tt5ue//BQTWE2xO5HZn.MjZcpo.z.7LKagZ.");

		entityManager.persist(userA);
		entityManager.persist(userB);
		Team team = Team.builder()
			.teamName("teamA")
			.description("description")
			.sportsCategory(SportsCategory.BADMINTON)
			.leader(userA)
			.build();
		entityManager.persist(team);
		TeamMember teamMemberA = new TeamMember(team, userA, TeamMemberRole.LEADER);
		entityManager.persist(teamMemberA);

		entityManager.flush();
		TeamInvitationRequest teamInvitationRequest = new TeamInvitationRequest(team.getId(), userB.getId());

		//when, then
		Assertions.assertThatThrownBy(
				() -> teamInvitationService.invite(-999L, teamInvitationRequest))
			.isInstanceOf(BusinessException.class);
	}
}