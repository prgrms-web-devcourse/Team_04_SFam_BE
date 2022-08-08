package com.kdt.team04.domain.teams.team.repository;

import java.time.LocalDateTime;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import com.kdt.team04.common.config.QueryDslConfig;
import com.kdt.team04.configure.JpaAuditConfig;
import com.kdt.team04.domain.teams.team.model.SportsCategory;
import com.kdt.team04.domain.teams.team.model.entity.Team;
import com.kdt.team04.domain.teams.team.repository.TeamRepository;
import com.kdt.team04.domain.user.entity.User;

@Import({QueryDslConfig.class, JpaAuditConfig.class})
@DataJpaTest
class TeamRepositoryTest {

	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	private TeamRepository teamRepository;

	@Test
	@DisplayName("팀을 생성한다")
	void testSave() {
		//given
		User teamCreator = getTeamCreator();
		Team newTeam = Team.builder()
			.name("가보자고!")
			.description("이하 생략")
			.sportsCategory(SportsCategory.SOCCER)
			.leader(teamCreator)
			.build();

		//when
		Team createdTeam = teamRepository.save(newTeam);
		System.out.println("dasd : " + createdTeam);

		//then
		Assertions.assertThat(createdTeam.getName()).isEqualTo(newTeam.getName());
		Assertions.assertThat(createdTeam.getDescription()).isEqualTo(newTeam.getDescription());
		Assertions.assertThat(createdTeam.getSportsCategory()).isEqualTo(newTeam.getSportsCategory());
		Assertions.assertThat(createdTeam.getCreatedAt().getMinute()).isEqualTo(LocalDateTime.now().getMinute());
	}

	public User getTeamCreator() {
		return entityManager.persist(User.builder()
			.username("test1234")
			.nickname("nickname")
			.password("Test1234!!")
			.build());
	}

}