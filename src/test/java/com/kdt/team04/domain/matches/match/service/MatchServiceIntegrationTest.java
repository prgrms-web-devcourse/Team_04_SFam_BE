package com.kdt.team04.domain.matches.match.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.kdt.team04.common.PageDto;
import com.kdt.team04.common.exception.BusinessException;
import com.kdt.team04.common.exception.EntityNotFoundException;
import com.kdt.team04.domain.matches.match.dto.MatchPagingCursor;
import com.kdt.team04.domain.matches.match.dto.MatchRequest;
import com.kdt.team04.domain.matches.match.dto.MatchResponse;
import com.kdt.team04.domain.matches.match.entity.Match;
import com.kdt.team04.domain.matches.match.entity.MatchStatus;
import com.kdt.team04.domain.matches.match.entity.MatchType;
import com.kdt.team04.domain.matches.match.repository.MatchRepository;
import com.kdt.team04.domain.team.SportsCategory;
import com.kdt.team04.domain.team.entity.Team;
import com.kdt.team04.domain.user.entity.Location;
import com.kdt.team04.domain.user.entity.User;

@SpringBootTest
@Transactional
class MatchServiceIntegrationTest {

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private MatchService matchService;

	@Autowired
	MatchRepository matchRepository;

	@Test
	@Transactional
	@DisplayName("팀의 리더는 팀 매칭 공고를 생성할 수 있다.")
	void testTeamCreateSuccess() {
		//given
		User user = new User("password", "username", "nickname");
		entityManager.persist(user);
		user.updateLocation(new Location(1.1, 1.2));
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
		User user = new User("test1234", "nickname", "$2a$12$JB1zYmj1TfoylCds8Tt5ue//BQTWE2xO5HZn.MjZcpo.z.7LKagZ.");
		entityManager.persist(user);
		user.updateLocation(new Location(1.1, 1.2));
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
		User user = new User("test1234", "nickname", "$2a$12$JB1zYmj1TfoylCds8Tt5ue//BQTWE2xO5HZn.MjZcpo.z.7LKagZ.");
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
		User leader = new User("test1234", "nicknameA", "$2a$12$JB1zYmj1TfoylCds8Tt5ue//BQTWE2xO5HZn.MjZcpo.z.7LKagZ.");
		User member = new User("test1235", "nicknameB", "$2a$12$JB1zYmj1TfoylCds8Tt5ue//BQTWE2xO5HZn.MjZcpo.z.7LKagZ.");
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

	@Test
	@DisplayName("작성자와 공고글 사이의 거리가 1.51km이고 검색범위가 1.50면 검색이 안된다.")
	void testFindMatchesCursorPagingTooFar() {
		Location myLocation = new Location(37.3947122, 127.111253);
		Location authorLocation = new Location(37.3956683, 127.128228);
		LocalDateTime cursorCreatedAt = LocalDateTime.now().plusWeeks(1);
		User leader = User.builder()
			.username("test1234")
			.nickname("nicknameA")
			.password("$2a$12$JB1zYmj1TfoylCds8Tt5ue//BQTWE2xO5HZn.MjZcpo.z.7LKagZ.")
			.location(authorLocation)
			.build();
		User member = User.builder()
			.username("test1235")
			.nickname("nicknameB")
			.password("$2a$12$JB1zYmj1TfoylCds8Tt5ue//BQTWE2xO5HZn.MjZcpo.z.7LKagZ.")
			.location(myLocation)
			.build();
		entityManager.persist(leader);
		entityManager.persist(member);

		Team team = Team.builder()
			.name("team1")
			.description("first team")
			.sportsCategory(SportsCategory.BADMINTON)
			.leader(leader)
			.build();
		entityManager.persist(team);

		Match match = Match.builder()
			.title("title")
			.content("content")
			.team(team)
			.sportsCategory(SportsCategory.BADMINTON)
			.matchDate(LocalDate.now())
			.matchType(MatchType.INDIVIDUAL_MATCH)
			.location(authorLocation)
			.status(MatchStatus.WAITING)
			.user(leader)
			.participants(1)
			.build();
		entityManager.persist(match);
		entityManager.flush();

		PageDto.MatchCursorPageRequest request = PageDto.MatchCursorPageRequest.builder().createdAt(cursorCreatedAt)
			.id(match.getId())
			.createdAt(cursorCreatedAt)
			.size(5)
			.category(SportsCategory.BADMINTON)
			.distance(1.5)
			.build();
		PageDto.CursorResponse<MatchResponse.ListViewResponse, MatchPagingCursor> foundMatches = matchService.findMatches(
			member.getId(), request);

		assertThat(foundMatches.values().size()).isEqualTo(0);
		assertThat(foundMatches.hasNext()).isFalse();
	}

	@Test
	@DisplayName("두 지점 간 거리 1.51km 5개 매치 불러오기")
	void testFindMatchesCursorPaging() {
		Location myLocation = new Location(37.3947122, 127.111253);
		Location authorLocation = new Location(37.3956683, 127.128228);
		LocalDateTime cursorCreatedAt = LocalDateTime.now().plusWeeks(1);
		User leader = User.builder()
			.username("test1234")
			.nickname("nicknameA")
			.password("$2a$12$JB1zYmj1TfoylCds8Tt5ue//BQTWE2xO5HZn.MjZcpo.z.7LKagZ.")
			.location(authorLocation)
			.build();
		User member = User.builder()
			.username("test1235")
			.nickname("nicknameB")
			.password("$2a$12$JB1zYmj1TfoylCds8Tt5ue//BQTWE2xO5HZn.MjZcpo.z.7LKagZ.")
			.location(myLocation)
			.build();
		entityManager.persist(leader);
		entityManager.persist(member);

		Team team = Team.builder()
			.name("team1")
			.description("first team")
			.sportsCategory(SportsCategory.BADMINTON)
			.leader(leader)
			.build();
		entityManager.persist(team);

		List<Match> matches = Stream.iterate(0, i -> i + 1)
			.limit(10)
			.map(i ->
				Match.builder()
					.title("title" + i)
					.content("content" + i)
					.team(team)
					.sportsCategory(SportsCategory.BADMINTON)
					.matchDate(LocalDate.now())
					.matchType(MatchType.INDIVIDUAL_MATCH)
					.location(authorLocation)
					.status(MatchStatus.WAITING)
					.user(leader)
					.participants(1)
					.build()
			).peek(entityManager::persist)
			.toList();

		entityManager.flush();

		PageDto.MatchCursorPageRequest request = PageDto.MatchCursorPageRequest.builder().createdAt(cursorCreatedAt)
			.id(matches.get(matches.size() - 1).getId())
			.createdAt(cursorCreatedAt)
			.size(5)
			.category(SportsCategory.BADMINTON)
			.distance(1.51)
			.build();
		PageDto.CursorResponse<MatchResponse.ListViewResponse, MatchPagingCursor> foundMatches = matchService.findMatches(
			member.getId(), request);

		assertThat(foundMatches.values().size()).isEqualTo(request.getSize());
		assertThat(foundMatches.hasNext()).isTrue();
		assertThat(foundMatches.values().get(0).id()).isEqualTo(matches.get(matches.size() - 1).getId());
		assertThat(foundMatches.values().get(4).id()).isEqualTo(matches.get(matches.size() - 5).getId());
	}

	@Test
	@DisplayName("축구매치 3개, 배트민턴 2개일때 매치 불러오기")
	void testFindMatchesCursorPagingDifferentCategory() {
		Location myLocation = new Location(37.3947122, 127.111253);
		Location authorLocation = new Location(37.3956683, 127.128228);
		LocalDateTime cursorCreatedAt = LocalDateTime.now().plusWeeks(1);
		User leader = User.builder()
			.username("test1234")
			.nickname("nicknameA")
			.password("$2a$12$JB1zYmj1TfoylCds8Tt5ue//BQTWE2xO5HZn.MjZcpo.z.7LKagZ.")
			.location(authorLocation)
			.build();
		User member = User.builder()
			.username("test1235")
			.nickname("nicknameB")
			.password("$2a$12$JB1zYmj1TfoylCds8Tt5ue//BQTWE2xO5HZn.MjZcpo.z.7LKagZ.")
			.location(myLocation)
			.build();
		entityManager.persist(leader);
		entityManager.persist(member);

		Team team = Team.builder()
			.name("team1")
			.description("first team")
			.sportsCategory(SportsCategory.BADMINTON)
			.leader(leader)
			.build();
		entityManager.persist(team);

		Stream.iterate(0, i -> i + 1)
			.limit(3)
			.map(i ->
				Match.builder()
					.title("title" + i)
					.content("content" + i)
					.team(team)
					.sportsCategory(SportsCategory.SOCCER)
					.matchDate(LocalDate.now())
					.matchType(MatchType.INDIVIDUAL_MATCH)
					.location(authorLocation)
					.status(MatchStatus.WAITING)
					.user(leader)
					.participants(1)
					.build()
			).forEach(entityManager::persist);

		List<Match> badmintonMatches = Stream.iterate(0, i -> i + 1)
			.limit(2)
			.map(i ->
				Match.builder()
					.title("title" + i)
					.content("content" + i)
					.team(team)
					.sportsCategory(SportsCategory.BADMINTON)
					.matchDate(LocalDate.now())
					.matchType(MatchType.INDIVIDUAL_MATCH)
					.location(authorLocation)
					.status(MatchStatus.WAITING)
					.user(leader)
					.participants(1)
					.build()
			).peek(entityManager::persist)
			.toList();

		entityManager.flush();

		PageDto.MatchCursorPageRequest request = PageDto.MatchCursorPageRequest.builder().createdAt(cursorCreatedAt)
			.id(badmintonMatches.get(badmintonMatches.size() - 1).getId())
			.createdAt(cursorCreatedAt)
			.size(5)
			.category(SportsCategory.BADMINTON)
			.distance(1.51)
			.build();
		PageDto.CursorResponse<MatchResponse.ListViewResponse, MatchPagingCursor> foundMatches = matchService.findMatches(
			member.getId(), request);

		assertThat(foundMatches.values().size()).isEqualTo(2);
		assertThat(foundMatches.hasNext()).isFalse();
		assertThat(foundMatches.values().get(0).id()).isEqualTo(
			badmintonMatches.get(badmintonMatches.size() - 1).getId());
	}

	@Test
	@DisplayName("두 지점 간 거리 1.51km 5개 매치 불러오기 - 2번째 페이징")
	void testFindMatchesCursorPagingTwice() {
		Location myLocation = new Location(37.3947122, 127.111253);
		Location authorLocation = new Location(37.3956683, 127.128228);
		LocalDateTime cursorCreatedAt = LocalDateTime.now().plusWeeks(1);
		User leader = User.builder()
			.username("test1234")
			.nickname("nicknameA")
			.password("$2a$12$JB1zYmj1TfoylCds8Tt5ue//BQTWE2xO5HZn.MjZcpo.z.7LKagZ.")
			.location(authorLocation)
			.build();
		User member = User.builder()
			.username("test1235")
			.nickname("nicknameB")
			.password("$2a$12$JB1zYmj1TfoylCds8Tt5ue//BQTWE2xO5HZn.MjZcpo.z.7LKagZ.")
			.location(myLocation)
			.build();
		entityManager.persist(leader);
		entityManager.persist(member);

		Team team = Team.builder()
			.name("team1")
			.description("first team")
			.sportsCategory(SportsCategory.BADMINTON)
			.leader(leader)
			.build();
		entityManager.persist(team);

		List<Match> matches = Stream.iterate(0, i -> i + 1)
			.limit(10)
			.map(i ->
				Match.builder()
					.title("title" + i)
					.content("content" + i)
					.team(team)
					.sportsCategory(SportsCategory.BADMINTON)
					.matchDate(LocalDate.now())
					.matchType(MatchType.INDIVIDUAL_MATCH)
					.location(authorLocation)
					.status(MatchStatus.WAITING)
					.user(leader)
					.participants(1)
					.build()
			).peek(entityManager::persist)
			.toList();

		entityManager.flush();

		PageDto.MatchCursorPageRequest request = PageDto.MatchCursorPageRequest.builder().createdAt(cursorCreatedAt)
			.id(matches.get(matches.size() - 1).getId())
			.size(5)
			.createdAt(cursorCreatedAt)
			.category(SportsCategory.BADMINTON)
			.distance(1.51)
			.build();
		PageDto.CursorResponse<MatchResponse.ListViewResponse, MatchPagingCursor> foundMatches = matchService.findMatches(
			member.getId(), request);

		LocalDateTime lastCreatedAt = foundMatches.cursor().getCreatedAt();
		Long lastId = foundMatches.cursor().getId();

		PageDto.MatchCursorPageRequest secondRequest = PageDto.MatchCursorPageRequest.builder()
			.createdAt(cursorCreatedAt)
			.id(lastId)
			.createdAt(lastCreatedAt)
			.size(5)
			.category(SportsCategory.BADMINTON)
			.distance(1.51)
			.build();

		PageDto.CursorResponse<MatchResponse.ListViewResponse, MatchPagingCursor> secondFoundMatches = matchService.findMatches(
			member.getId(), secondRequest);

		assertThat(secondFoundMatches.values().size()).isEqualTo(request.getSize());
		assertThat(secondFoundMatches.hasNext()).isFalse();
		assertThat(secondFoundMatches.values().get(0).id()).isEqualTo(matches.get(matches.size() - 6).getId());
		assertThat(secondFoundMatches.values().get(4).id()).isEqualTo(matches.get(matches.size() - 10).getId());
	}

	@Test
	@DisplayName("페이징 요청 객체가 없으면 디폴트 PageRequest로 조회한다.")
	void testFindAllMatchesIfNullPagingRequest() {
		Location myLocation = new Location(37.3947122, 127.111253);
		Location authorLocation = new Location(37.3956683, 127.128228);

		User leader = User.builder()
			.username("test1234")
			.nickname("nicknameA")
			.password("$2a$12$JB1zYmj1TfoylCds8Tt5ue//BQTWE2xO5HZn.MjZcpo.z.7LKagZ.")
			.location(authorLocation)
			.build();
		User member = User.builder()
			.username("test1235")
			.nickname("nicknameB")
			.password("$2a$12$JB1zYmj1TfoylCds8Tt5ue//BQTWE2xO5HZn.MjZcpo.z.7LKagZ.")
			.location(myLocation)
			.build();
		entityManager.persist(leader);
		entityManager.persist(member);

		Team team = Team.builder()
			.name("team1")
			.description("first team")
			.sportsCategory(SportsCategory.BADMINTON)
			.leader(leader)
			.build();
		entityManager.persist(team);

		List<Match> matches = Stream.iterate(0, i -> i + 1)
			.limit(10)
			.map(i ->
				Match.builder()
					.title("title" + i)
					.content("content" + i)
					.team(team)
					.sportsCategory(SportsCategory.BADMINTON)
					.matchDate(LocalDate.now())
					.matchType(MatchType.INDIVIDUAL_MATCH)
					.location(authorLocation)
					.status(MatchStatus.WAITING)
					.user(leader)
					.participants(1)
					.build()
			).peek(entityManager::persist)
			.toList();

		entityManager.flush();

		PageDto.CursorResponse<MatchResponse.ListViewResponse, MatchPagingCursor> foundMatches = matchService.findMatches(
			member.getId(), null);

		assertThat(foundMatches.values().size()).isEqualTo(matches.size());
		assertThat(foundMatches.values().get(0).id()).isEqualTo(matches.get(matches.size() - 1).getId());
		assertThat(foundMatches.values().get(9).id()).isEqualTo(matches.get(matches.size() - 10).getId());
		assertThat(foundMatches.hasNext()).isFalse();
	}
}