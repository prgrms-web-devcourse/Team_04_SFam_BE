package com.kdt.team04.domain.matches.match.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.kdt.team04.common.exception.BusinessException;
import com.kdt.team04.common.exception.EntityNotFoundException;
import com.kdt.team04.domain.matches.match.dto.MatchRequest;
import com.kdt.team04.domain.matches.match.dto.MatchResponse;
import com.kdt.team04.domain.matches.match.entity.Match;
import com.kdt.team04.domain.matches.match.entity.MatchType;
import com.kdt.team04.domain.matches.match.repository.MatchRepository;
import com.kdt.team04.domain.team.SportsCategory;
import com.kdt.team04.domain.team.entity.Team;
import com.kdt.team04.domain.user.entity.User;

@SpringBootTest
class MatchServiceIntegrationTest {

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private MatchRepository matchRepository;

	@Autowired
	private MatchService matchService;

	@Test
	@Transactional
	@DisplayName("팀의 리더는 팀 매칭 공고를 생성할 수 있다.")
	void testTeamCreateSuccess() {
		//given
		User user = new User("password", "username", "nickname");
		entityManager.persist(user);
		Team team = Team.builder()
			.name("team1")
			.description("first team")
			.sportsCategory(SportsCategory.BADMINTON)
			.leader(user)
			.build();
		entityManager.persist(team);
		MatchRequest.MatchCreateRequest request = new MatchRequest.MatchCreateRequest("match1", LocalDate.now(),
			MatchType.TEAM_MATCH,
			team.getId(), 3, SportsCategory.BADMINTON, "content");

		//when
		Long createdMatch = matchService.create(user.getId(), request);

		//then
		assertThat(createdMatch).isNotNull();
	}

	@Test
	@Transactional
	@DisplayName("사용자는 개인전 매칭 공고를 생성할 수 있다.")
	void testIndividualCreateSuccess() {
		//given
		User user = new User("password", "username", "nickname");
		entityManager.persist(user);
		MatchRequest.MatchCreateRequest request = new MatchRequest.MatchCreateRequest("match1", LocalDate.now(),
			MatchType.INDIVIDUAL_MATCH,
			null, 1, SportsCategory.BADMINTON, "content");

		//when
		Long createdMatch = matchService.create(user.getId(), request);

		//then
		assertThat(createdMatch).isNotNull();
	}

	@Test
	@Transactional
	@DisplayName("개인전 매칭 공고는 참여 인원이 1명이 아닐경우 예외가 발생한다.")
	void testIndividualCreateFail() {
		//given
		User user = new User("password", "username", "nickname");
		entityManager.persist(user);
		MatchRequest.MatchCreateRequest request = new MatchRequest.MatchCreateRequest("match1", LocalDate.now(),
			MatchType.INDIVIDUAL_MATCH,
			null, 2, SportsCategory.BADMINTON, "content");

		//when, then
		assertThatThrownBy(() -> matchService.create(user.getId(), request)).isInstanceOf(BusinessException.class);
	}

	@Test
	@Transactional
	@DisplayName("팀 매칭 공고를 생성하는 주체가 팀의 리더가 아닐 경우 예외가 발생한다.")
	void testTeamCreateFail() {
		//given
		User leader = new User("password", "leader", "nickname");
		User member = new User("password", "member", "nickname");
		entityManager.persist(leader);
		Team team = Team.builder()
			.name("team1")
			.description("first team")
			.sportsCategory(SportsCategory.BADMINTON)
			.leader(leader)
			.build();
		entityManager.persist(team);
		MatchRequest.MatchCreateRequest request = new MatchRequest.MatchCreateRequest("match1", LocalDate.now(),
			MatchType.TEAM_MATCH,
			team.getId(), 1, SportsCategory.BADMINTON, "content");

		//when, then
		assertThatThrownBy(() -> matchService.create(member.getId(), request)).isInstanceOf(BusinessException.class);
	}

	@Test
	@Transactional
	@DisplayName("매칭 공고 단건 조회를 할 수 있다.")
	void testFindByIdSuccess() {
		//given
		User user = new User("password", "username", "nickname");
		entityManager.persist(user);

		Match savedMatch = matchRepository.save(Match.builder()
			.title("match")
			.matchDate(LocalDate.now())
			.matchType(MatchType.INDIVIDUAL_MATCH)
			.participants(1)
			.user(user)
			.sportsCategory(SportsCategory.BADMINTON)
			.content("content")
			.build());

		//when
		MatchResponse foundMatch = matchService.findById(savedMatch.getId());

		//then
		assertThat(foundMatch.title()).isEqualTo(savedMatch.getTitle());
		assertThat(foundMatch.matchDate()).isEqualTo(savedMatch.getMatchDate());
		assertThat(foundMatch.content()).isEqualTo(savedMatch.getContent());
		assertThat(foundMatch.participants()).isEqualTo(savedMatch.getParticipants());
		assertThat(foundMatch.author().id()).isEqualTo(user.getId());
		assertThat(foundMatch.team()).isNull();
	}

	@Test
	@Transactional
	@DisplayName("존재하지 않은 매칭 공고 id의 경우 EntityNotFound 예외가 발생한다.")
	void testFindByIdFail() {
		//given
		Long invalidId = 1234L;
		//when, then
		assertThatThrownBy(() -> matchService.findById(invalidId)).isInstanceOf(EntityNotFoundException.class);
	}
}