package com.kdt.team04.domain.team.entity;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.stream.Stream;

import javax.validation.ConstraintViolationException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import com.kdt.team04.configure.TestQueryDslConfig;
import com.kdt.team04.domain.team.SportsCategory;
import com.kdt.team04.domain.team.repository.TeamRepository;
import com.kdt.team04.domain.user.entity.User;

@Import(TestQueryDslConfig.class)
@DataJpaTest
public class TeamValidationTest {

	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	private TeamRepository teamRepository;

	@Test
	@DisplayName("팀 이름이 2글자 미만이거나 10글자 보다 크면 validation 예외가 발생한다.[공백 및 공백으로 이루어진 문자열 포함]")
	void testTeamNameViolation() {
		//given

		List<String> violations = List.of("", "꿀", " ", "일이삼사오육칠팔구십초과");
		User teamCreator = getTeamCreator();

		violations.forEach(violation -> {
			Team newTeam = Team.builder()
				.name(violation)
				.description("이하 생략")
				.sportsCategory(SportsCategory.SOCCER)
				.leader(teamCreator)
				.build();

			//when
			//then
			assertThatThrownBy(() -> teamRepository.save(newTeam))
				.isInstanceOf(ConstraintViolationException.class);
		});
	}

	@Test
	@DisplayName("팀 설명이 100글자를 넘어가면 validation 예외가 발생한다.[공백 및 공백으로 이루어진 문자열 포함]")
	void testDescriptionGrater100() {
		//given
		User teamCreator = getTeamCreator();
		String violationLetters = create101Letters();
		List<String> violations = List.of("", " ", violationLetters);

		violations.forEach(violation -> {
			Team newTeam = Team.builder()
				.name("꿀꿀꿀")
				.description(violation)
				.sportsCategory(SportsCategory.BADMINTON)
				.leader(teamCreator)
				.build();

			//when
			//then
			assertThatThrownBy(() -> teamRepository.save(newTeam))
				.isInstanceOf(ConstraintViolationException.class);
		});
	}

	@Test
	@DisplayName("팀 리더 없이 팀을 생성하면 validation 예외가 발생한다")
	void testNotContainLeader() {
		//given
		Team newTeam = Team.builder()
			.name("꿀꿀꿀")
			.description("이하 생략.")
			.sportsCategory(SportsCategory.SOCCER)
			.build();

		//when
		//then
		assertThatThrownBy(() -> teamRepository.save(newTeam))
			.isInstanceOf(ConstraintViolationException.class);
	}

	private String create101Letters() {
		StringBuilder builder = new StringBuilder();

		Stream.generate(() -> 0)
			.limit(101)
			.forEach(builder::append);

		return builder.toString();
	}

	public User getTeamCreator() {
		return entityManager.persist(User.builder()
			.username("test1234")
			.nickname("nickname")
			.password("Test1234!!")
			.build());
	}
}
