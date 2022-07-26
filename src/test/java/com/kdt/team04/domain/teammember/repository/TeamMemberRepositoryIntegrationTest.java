package com.kdt.team04.domain.teammember.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import com.kdt.team04.common.config.QueryDslConfig;
import com.kdt.team04.domain.team.SportsCategory;
import com.kdt.team04.domain.team.entity.Team;
import com.kdt.team04.domain.teammember.entity.TeamMember;
import com.kdt.team04.domain.teammember.entity.TeamMemberRole;
import com.kdt.team04.domain.user.entity.User;

@DataJpaTest
@Import(QueryDslConfig.class)
class TeamMemberRepositoryIntegrationTest {

	@Autowired
	TestEntityManager entityManager;

	@Autowired
	TeamMemberRepository teamMemberRepository;

	@Test
	@DisplayName("team ID와 user ID로 팀 멤버 조회 시 존재 하면 true 반환")
	void existsByTeamIdAndMemberId() {
		User userA = new User("test1234", "nickname", "$2a$12$JB1zYmj1TfoylCds8Tt5ue//BQTWE2xO5HZn.MjZcpo.z.7LKagZ.");
		User userB = new User("test4567", "nickname2", "$2a$12$JB1zYmj1TfoylCds8Tt5ue//BQTWE2xO5HZn.MjZcpo.z.7LKagZ.");
		Team team = Team.builder()
			.teamName("teamA")
			.description("description")
			.sportsCategory(SportsCategory.BADMINTON)
			.leader(userA)
			.build();
		TeamMember teamMemberA = new TeamMember(team, userA, TeamMemberRole.LEADER);
		TeamMember teamMemberB = new TeamMember(team, userB, TeamMemberRole.MEMBER);
		Team persistedTeam = entityManager.persist(team);
		User persistedUserA = entityManager.persist(userA);
		entityManager.persist(userB);
		entityManager.persist(teamMemberA);
		entityManager.persist(teamMemberB);

		Assertions.assertThat(teamMemberRepository.existsByTeamIdAndMemberId(persistedTeam.getId(), persistedUserA.getId())).isTrue();
	}

	@Test
	@DisplayName("team ID와 user ID로 팀 멤버 조회 시 존재 하면 true 반환")
	void notExistsByTeamIdAndMemberId() {
		//given
		User userA = new User("test1234", "nickname", "$2a$12$JB1zYmj1TfoylCds8Tt5ue//BQTWE2xO5HZn.MjZcpo.z.7LKagZ.");
		User userB = new User("test4567", "nickname2", "$2a$12$JB1zYmj1TfoylCds8Tt5ue//BQTWE2xO5HZn.MjZcpo.z.7LKagZ.");
		Team team = Team.builder()
			.teamName("teamA")
			.description("description")
			.sportsCategory(SportsCategory.BADMINTON)
			.leader(userA)
			.build();
		TeamMember teamMemberA = new TeamMember(team, userA, TeamMemberRole.LEADER);
		Team persistedTeam = entityManager.persist(team);
		User persistedUserB = entityManager.persist(userB);

		entityManager.persist(userA);
		entityManager.persist(teamMemberA);

		//when
		boolean exist = teamMemberRepository.existsByTeamIdAndMemberId(persistedTeam.getId(), persistedUserB.getId());

		//then
		Assertions.assertThat(exist).isFalse();
	}
}