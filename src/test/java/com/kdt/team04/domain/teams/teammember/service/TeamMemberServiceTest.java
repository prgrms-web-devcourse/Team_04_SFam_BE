package com.kdt.team04.domain.teams.teammember.service;

import javax.persistence.EntityManager;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import com.amazonaws.services.s3.AmazonS3;
import com.kdt.team04.common.aws.s3.S3Uploader;
import com.kdt.team04.common.exception.BusinessException;
import com.kdt.team04.domain.teams.team.model.SportsCategory;
import com.kdt.team04.domain.teams.team.model.entity.Team;
import com.kdt.team04.domain.teams.teaminvitation.model.InvitationStatus;
import com.kdt.team04.domain.teams.teaminvitation.model.entity.TeamInvitation;
import com.kdt.team04.domain.teams.teammember.dto.request.RegisterTeamMemberRequest;
import com.kdt.team04.domain.teams.teammember.model.TeamMemberRole;
import com.kdt.team04.domain.teams.teammember.model.entity.TeamMember;
import com.kdt.team04.domain.user.entity.User;

@SpringBootTest
@Transactional
class TeamMemberServiceTest {

	@Autowired
	TeamMemberService teamMemberService;

	@Autowired
	EntityManager entityManager;

	@MockBean
	S3Uploader s3Uploader;

	@MockBean
	AmazonS3 amazonS3;

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

		// when
		RegisterTeamMemberRequest request = new RegisterTeamMemberRequest(userB.getId(), teamInvitationA.getId());
		teamMemberService.registerTeamMember(userB.getId(), team.getId(), request);

		entityManager.flush();
		entityManager.clear();

		// then
		Assertions.assertThat(teamMemberService.existsTeamMember(team.getId(), userB.getId())).isTrue();
	}

	@Test
	@DisplayName("다른 사용자의 초대를 수락할 시 권한 예외가 발생한다.")
	void testNotAuthentication() {
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

		TeamInvitation teamInvitation = new TeamInvitation(team, userB, InvitationStatus.WAITING);
		entityManager.persist(teamInvitation);
		entityManager.flush();
		entityManager.clear();

		// when
		RegisterTeamMemberRequest request = new RegisterTeamMemberRequest(userB.getId(), teamInvitation.getId());

		// then
		Assertions.assertThatThrownBy(() -> teamMemberService.registerTeamMember(userA.getId(), team.getId(), request))
			.isInstanceOf(BusinessException.class);
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
		RegisterTeamMemberRequest request = new RegisterTeamMemberRequest(userB.getId(), teamMemberA.getId());

		// then
		Assertions.assertThatThrownBy(() -> teamMemberService.registerTeamMember(userB.getId(), team.getId(), request))
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
		RegisterTeamMemberRequest request = new RegisterTeamMemberRequest(userB.getId(), teamInvitationA.getId());

		// then
		Assertions.assertThatThrownBy(() -> teamMemberService.registerTeamMember(userB.getId(), team.getId(), request))
			.isInstanceOf(BusinessException.class);
	}

}
