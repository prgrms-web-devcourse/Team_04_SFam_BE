package com.kdt.team04.domain.teams.teaminvitation.service;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import javax.persistence.EntityManager;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import com.amazonaws.services.s3.AmazonS3;
import com.kdt.team04.common.PageDto;
import com.kdt.team04.common.exception.BusinessException;
import com.kdt.team04.common.aws.s3.S3Uploader;
import com.kdt.team04.domain.teams.team.model.SportsCategory;
import com.kdt.team04.domain.teams.team.dto.TeamConverter;
import com.kdt.team04.domain.teams.team.model.entity.Team;
import com.kdt.team04.domain.teams.teaminvitation.dto.request.TeamInvitationRefuseRequest;
import com.kdt.team04.domain.teams.teaminvitation.dto.request.TeamInvitationRequest;
import com.kdt.team04.domain.teams.teaminvitation.dto.response.TeamInviteResponse;
import com.kdt.team04.domain.teams.teaminvitation.model.InvitationStatus;
import com.kdt.team04.domain.teams.teaminvitation.model.entity.TeamInvitation;
import com.kdt.team04.domain.teams.teaminvitation.service.TeamInvitationService;
import com.kdt.team04.domain.teams.teammember.model.entity.TeamMember;
import com.kdt.team04.domain.teams.teammember.model.TeamMemberRole;
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

	@MockBean
	S3Uploader s3Uploader;

	@MockBean
	AmazonS3 amazonS3;

	@Test
	@DisplayName("팀 초대 성공")
	void inviteSuccess() {
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

		entityManager.flush();
		TeamInvitationRequest teamInvitationRequest = new TeamInvitationRequest(userB.getId());

		//when
		TeamInviteResponse response = teamInvitationService.invite(userA.getId(), team.getId(),
			teamInvitationRequest.targetUserId());

		//then
		Assertions.assertThat(response.invitationId()).isNotNull();

	}

	@Test
	@DisplayName("이미 팀원인 유저는 초대할 수 없다.")
	void inviteAlreadyTeamMember() {
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
		TeamMember teamMemberB = new TeamMember(team, userB, TeamMemberRole.MEMBER);
		entityManager.persist(teamMemberA);
		entityManager.persist(teamMemberB);
		entityManager.flush();
		entityManager.clear();
		TeamInvitationRequest teamInvitationRequest = new TeamInvitationRequest(userB.getId());

		//when, then
		Assertions.assertThatThrownBy(
				() -> teamInvitationService.invite(userA.getId(), team.getId(), teamInvitationRequest.targetUserId()))
			.isInstanceOf(BusinessException.class);
	}

	@Test
	@DisplayName("이미 초대 대기상태나 수락 상태인 유저에게 초대를 보낼 수 없다.")
	void inviteAlreadySendInviteFail() {
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

		TeamInvitationRequest teamInvitationRequest = new TeamInvitationRequest(userB.getId());

		//when, then
		Assertions.assertThatThrownBy(() ->
			teamInvitationService.invite(userA.getId(), team.getId(), teamInvitationRequest.targetUserId())
		).isInstanceOf(BusinessException.class);
	}

	@Test
	@DisplayName("팀 리더가 아닌사람은 초대할 수 없다.")
	void notTeamLeaderCantInvite() {
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

		entityManager.flush();
		TeamInvitationRequest teamInvitationRequest = new TeamInvitationRequest(userB.getId());

		//when, then
		Assertions.assertThatThrownBy(
				() -> teamInvitationService.invite(-999L, team.getId(), teamInvitationRequest.targetUserId()))
			.isInstanceOf(BusinessException.class);
	}

	@Test
	@DisplayName("초대를 거절할 수 있다.")
	void invitationRefused() {
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

		// when
		TeamInvitationRefuseRequest request = new TeamInvitationRefuseRequest(userB.getId());
		teamInvitationService.refuse(userB.getId(), team.getId(), teamInvitation.getId(), request);

		entityManager.flush();
		entityManager.clear();

		// then
		TeamInvitation result = entityManager.find(TeamInvitation.class, teamInvitation.getId());
		Assertions.assertThat(result.getStatus())
			.isEqualTo(InvitationStatus.REFUSED);
	}

	@Test
	@DisplayName("다른 사용자의 초대를 거절할 시 권한 예외가 발생한다.")
	void notAuthentication() {
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
		TeamInvitationRefuseRequest request = new TeamInvitationRefuseRequest(userB.getId());

		// then
		Assertions.assertThatThrownBy(
				() -> teamInvitationService.refuse(userA.getId(), team.getId(), teamInvitation.getId(), request))
			.isInstanceOf(BusinessException.class);
	}

	@Test
	@DisplayName("팀 초대 목록을 20개중 10개씩 조회한다.")
	void findInvitationsLimit10() {
		// given
		User userA = new User("test1234", "nickname", "$2a$12$JB1zYmj1TfoylCds8Tt5ue//BQTWE2xO5HZn.MjZcpo.z.7LKagZ.");
		User userB = new User("test4567", "nickname2", "$2a$12$JB1zYmj1TfoylCds8Tt5ue//BQTWE2xO5HZn.MjZcpo.z.7LKagZ.");
		entityManager.persist(userA);
		entityManager.persist(userB);

		List<Team> teams = LongStream.range(1, 21)
			.mapToObj(
				index -> Team.builder()
					.name("teamName" + index)
					.description("description" + index)
					.sportsCategory(SportsCategory.SOCCER)
					.leader(userA)
					.build()
			).peek(entityManager::persist)
			.toList();

		List<TeamInvitation> teamInvitations = IntStream.range(0, 20)
			.mapToObj(
				index -> TeamInvitation.builder()
					.team(teams.get(index))
					.target(userB)
					.status(InvitationStatus.WAITING)
					.build()
			).peek(entityManager::persist)
			.toList();

		entityManager.flush();
		entityManager.clear();

		// when
		PageDto.TeamInvitationCursorPageRequest request = new PageDto.TeamInvitationCursorPageRequest(null, null, 10,
			InvitationStatus.WAITING);
		PageDto.CursorResponse invites = teamInvitationService.getInvitations(userB.getId(), request);

		// then
		Assertions.assertThat(invites.values()).hasSize(10);
		Assertions.assertThat(invites.hasNext()).isTrue();
	}

	@Test
	@DisplayName("팀 초대 목록을 20개중 20개씩 조회한다.")
	void findInvitationsLimit20() {
		// given
		User userA = new User("test1234", "nickname", "$2a$12$JB1zYmj1TfoylCds8Tt5ue//BQTWE2xO5HZn.MjZcpo.z.7LKagZ.");
		User userB = new User("test4567", "nickname2", "$2a$12$JB1zYmj1TfoylCds8Tt5ue//BQTWE2xO5HZn.MjZcpo.z.7LKagZ.");
		entityManager.persist(userA);
		entityManager.persist(userB);

		List<Team> teams = LongStream.range(1, 21)
			.mapToObj(
				index -> Team.builder()
					.name("teamName" + index)
					.description("description" + index)
					.sportsCategory(SportsCategory.SOCCER)
					.leader(userA)
					.build()
			).peek(entityManager::persist)
			.toList();

		List<TeamInvitation> teamInvitations = IntStream.range(0, 20)
			.mapToObj(
				index -> TeamInvitation.builder()
					.team(teams.get(index))
					.target(userB)
					.status(InvitationStatus.WAITING)
					.build()
			).peek(entityManager::persist)
			.toList();

		entityManager.flush();
		entityManager.clear();

		// when
		PageDto.TeamInvitationCursorPageRequest request = new PageDto.TeamInvitationCursorPageRequest(null, null, 20,
			InvitationStatus.WAITING);
		PageDto.CursorResponse invites = teamInvitationService.getInvitations(userB.getId(), request);

		// then
		Assertions.assertThat(invites.values()).hasSize(20);
		Assertions.assertThat(invites.hasNext()).isFalse();
	}

}