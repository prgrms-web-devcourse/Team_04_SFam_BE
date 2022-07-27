package com.kdt.team04.domain.teammember.service;

import javax.persistence.EntityManager;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.kdt.team04.common.exception.BusinessException;
import com.kdt.team04.domain.team.SportsCategory;
import com.kdt.team04.domain.team.entity.Team;
import com.kdt.team04.domain.teaminvitation.entity.InvitationStatus;
import com.kdt.team04.domain.teaminvitation.entity.TeamInvitation;
import com.kdt.team04.domain.teammember.dto.TeamMemberRequest;
import com.kdt.team04.domain.teammember.entity.TeamMember;
import com.kdt.team04.domain.teammember.entity.TeamMemberRole;
import com.kdt.team04.domain.user.entity.User;

@SpringBootTest
@Transactional
class TeamMemberServiceTest {

	@Autowired
	TeamMemberService teamMemberService;

	@Autowired
	EntityManager entityManager;

	@Test
	@DisplayName("팀원 등록에 성공한다.")
	void testRegisterMember() {
		//given
		User userA = new User("test1234", "nickname", "$2a$12$JB1zYmj1TfoylCds8Tt5ue//BQTWE2xO5HZn.MjZcpo.z.7LKagZ.");
		User userB = new User("test4567", "nickname2", "$2a$12$JB1zYmj1TfoylCds8Tt5ue//BQTWE2xO5HZn.MjZcpo.z.7LKagZ.");
		entityManager.persist(userA);
		entityManager.persist(userB);

		Team team = Team.builder()
			.name("teamA")
			.description("description")
			.sportsCategory(SportsCategory.BADMINTON)
			.leader(userA)
			.build();
		entityManager.persist(team);

		TeamMember teamMemberA = new TeamMember(team, userA, TeamMemberRole.LEADER);
		entityManager.persist(teamMemberA);
		TeamInvitation teamInvitationA = new TeamInvitation(team, userB, InvitationStatus.WAITING);
		entityManager.persist(teamInvitationA);
		entityManager.flush();

		// when
		TeamMemberRequest.RegisterRequest request = new TeamMemberRequest.RegisterRequest(userB.getId());
		teamMemberService.registerTeamMember(team.getId(), request);

		// then
		Assertions.assertThat(teamMemberService.existsTeamMember(team.getId(), userB.getId())).isTrue();
	}

	@Test
	@DisplayName("이미 등록된 팀원은 등록에 실패한다.")
	void testAlreadyMember() {
		//given
		User userA = new User("test1234", "nickname", "$2a$12$JB1zYmj1TfoylCds8Tt5ue//BQTWE2xO5HZn.MjZcpo.z.7LKagZ.");
		User userB = new User("test4567", "nickname2", "$2a$12$JB1zYmj1TfoylCds8Tt5ue//BQTWE2xO5HZn.MjZcpo.z.7LKagZ.");
		entityManager.persist(userA);
		entityManager.persist(userB);

		Team team = Team.builder()
			.name("teamA")
			.description("description")
			.sportsCategory(SportsCategory.BADMINTON)
			.leader(userA)
			.build();
		entityManager.persist(team);

		TeamMember teamMemberA = new TeamMember(team, userA, TeamMemberRole.LEADER);
		entityManager.persist(teamMemberA);

		// 팀 맴버에 이미 등록
		TeamMember teamMemberB = new TeamMember(team, userB, TeamMemberRole.MEMBER);
		entityManager.persist(teamMemberB);
		entityManager.flush();

		// when
		TeamMemberRequest.RegisterRequest request = new TeamMemberRequest.RegisterRequest(userB.getId());

		// then
		Assertions.assertThatThrownBy(() -> teamMemberService.registerTeamMember(team.getId(), request))
			.isInstanceOf(BusinessException.class);
	}

	@Test
	@DisplayName("초대 상태가 WAITING 상태가 아니라면 등록에 실패한다.")
	void testInvalidInvitation() {
		//given
		User userA = new User("test1234", "nickname", "$2a$12$JB1zYmj1TfoylCds8Tt5ue//BQTWE2xO5HZn.MjZcpo.z.7LKagZ.");
		User userB = new User("test4567", "nickname2", "$2a$12$JB1zYmj1TfoylCds8Tt5ue//BQTWE2xO5HZn.MjZcpo.z.7LKagZ.");
		entityManager.persist(userA);
		entityManager.persist(userB);

		Team team = Team.builder()
			.name("teamA")
			.description("description")
			.sportsCategory(SportsCategory.BADMINTON)
			.leader(userA)
			.build();
		entityManager.persist(team);

		TeamMember teamMemberA = new TeamMember(team, userA, TeamMemberRole.LEADER);
		entityManager.persist(teamMemberA);

		// 이미 수락한 상태라면
		TeamInvitation teamInvitationA = new TeamInvitation(team, userB, InvitationStatus.ACCEPTED);
		entityManager.persist(teamInvitationA);

		// when
		TeamMemberRequest.RegisterRequest request = new TeamMemberRequest.RegisterRequest(userB.getId());

		// then
		Assertions.assertThatThrownBy(() -> teamMemberService.registerTeamMember(team.getId(), request))
			.isInstanceOf(BusinessException.class);
	}

}
