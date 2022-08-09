package com.kdt.team04.domain.teams.team.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.text.MessageFormat;
import java.util.List;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import com.amazonaws.services.s3.AmazonS3;
import com.kdt.team04.common.aws.s3.S3Uploader;
import com.kdt.team04.common.exception.BusinessException;
import com.kdt.team04.common.exception.EntityNotFoundException;
import com.kdt.team04.common.exception.ErrorCode;
import com.kdt.team04.domain.teams.team.dto.request.CreateTeamRequest;
import com.kdt.team04.domain.teams.team.dto.response.TeamResponse;
import com.kdt.team04.domain.teams.team.dto.response.TeamSimpleResponse;
import com.kdt.team04.domain.teams.team.model.SportsCategory;
import com.kdt.team04.domain.teams.team.model.entity.Team;
import com.kdt.team04.domain.teams.team.repository.TeamRepository;
import com.kdt.team04.domain.user.entity.User;

@SpringBootTest
class TeamServiceIntegrationTest {

	@Autowired
	TeamService teamService;

	@Autowired
	TeamRepository teamRepository;

	@Autowired
	EntityManager entityManager;

	@MockBean
	S3Uploader s3Uploader;

	@MockBean
	AmazonS3 amazonS3;

	@Test
	@Transactional
	@DisplayName("해당 사용자는 팀을 생성할 수 있고, 해당 팀의 리더가 된다.")
	void testCreateSuccess() {
		//given
		User teamCreator = getDemoUser();
		CreateTeamRequest requestDto = new CreateTeamRequest("team1", "first team",
			SportsCategory.BADMINTON);

		//when
		Long savedTeamId = teamService.create(teamCreator.getId(), requestDto);
		Long leaderId = teamRepository.findById(savedTeamId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.TEAM_NOT_FOUND,
				MessageFormat.format("TeamId = {0}", savedTeamId)))
			.getLeader()
			.getId();

		//then
		assertThat(savedTeamId).isNotNull();
		assertThat(teamCreator.getId()).isEqualTo(leaderId);
	}

	@Test
	@Transactional
	@DisplayName("같은 이름으로 팀 등록 시, 중복 오류가 발생한다.")
	void createFail_DuplicateTeamName() {
		//given
		User teamCreator = getDemoUser();
		CreateTeamRequest requestDto = new CreateTeamRequest("team1", "first team",
			SportsCategory.BADMINTON);

		teamService.create(teamCreator.getId(), requestDto);

		//when, then
		assertThatThrownBy(() -> {
			teamService.create(teamCreator.getId(), requestDto);
		}).isInstanceOf(BusinessException.class);
	}

	@Test
	@Transactional
	@DisplayName("팀의 id로 해당 팀 프로필을 조회할 수 있다.")
	void testFindByIdSuccess() {
		//given
		User user = new User("test1234", "nickname", "$2a$12$JB1zYmj1TfoylCds8Tt5ue//BQTWE2xO5HZn.MjZcpo.z.7LKagZ.");
		entityManager.persist(user);
		Team team = Team.builder()
			.name("team1")
			.description("first team")
			.sportsCategory(SportsCategory.BADMINTON)
			.leader(user)
			.build();
		Team savedTeam = teamRepository.save(team);

		//when
		TeamResponse teamResponse = teamService.findById(savedTeam.getId());

		//then
		assertThat(teamResponse.id()).isEqualTo(savedTeam.getId());
		assertThat(teamResponse.name()).isEqualTo(savedTeam.getName());
		assertThat(teamResponse.description()).isEqualTo(savedTeam.getDescription());
		assertThat(teamResponse.leader().id()).isEqualTo(savedTeam.getLeader().getId());
	}

	@Test
	@DisplayName("유효하지 않은 id로 조회할 경우 EntityNotFoundException 이 발생한다.")
	void testFindByIdFail() {
		Long invalidId = 1234L;
		assertThatThrownBy(() -> teamService.findById(invalidId)).isInstanceOf(EntityNotFoundException.class);
	}

	@Test
	@Transactional
	@DisplayName("해당 사용자가 리더인 팀 목록을 조회할 수 있다.")
	void testFindByLeaderIdSuccess() {
		//given
		User user = new User("test1234", "nickname", "$2a$12$JB1zYmj1TfoylCds8Tt5ue//BQTWE2xO5HZn.MjZcpo.z.7LKagZ.");
		entityManager.persist(user);
		Team team = Team.builder()
			.name("team1")
			.description("first team")
			.sportsCategory(SportsCategory.BADMINTON)
			.leader(user)
			.build();
		Team savedTeam = teamRepository.save(team);

		//when
		List<TeamSimpleResponse> foundTeams = teamService.findByLeaderId(user.getId());

		//then
		assertThat(foundTeams).hasSize(1);
		assertThat(foundTeams.get(0).id()).isEqualTo(savedTeam.getId());
	}

	public User getDemoUser() {
		User demoUser = User.builder()
			.username("test1234")
			.nickname("nickname")
			.password("Test1234!!")
			.build();

		entityManager.persist(demoUser);

		return demoUser;
	}

}
