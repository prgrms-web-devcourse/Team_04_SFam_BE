package com.kdt.team04.domain.matches.request.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.kdt.team04.common.exception.BusinessException;
import com.kdt.team04.common.exception.EntityNotFoundException;
import com.kdt.team04.domain.matches.match.entity.Match;
import com.kdt.team04.domain.matches.match.entity.MatchStatus;
import com.kdt.team04.domain.matches.match.entity.MatchType;
import com.kdt.team04.domain.matches.request.entity.MatchChat;
import com.kdt.team04.domain.matches.request.entity.MatchProposal;
import com.kdt.team04.domain.matches.request.entity.MatchProposalStatus;
import com.kdt.team04.domain.matches.request.repository.MatchChatRepository;
import com.kdt.team04.domain.team.SportsCategory;
import com.kdt.team04.domain.team.entity.Team;
import com.kdt.team04.domain.user.entity.User;

@Transactional
@SpringBootTest
class MatchChatServiceIntegrationTest {

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private MatchChatService matchChatService;

	@Autowired
	private MatchChatRepository matchChatRepository;

	@Test
	@Transactional
	@DisplayName("작성자는 채팅 상대에게 채팅을 등록 한다.")
	void test_chat() {
		//given
		User author = getUser("author");
		Team authorTeam = getSoccerTeam("author", author);
		User target = getUser("target");
		Team targetTeam = getSoccerTeam("target", target);
		Match match = getSoccerTeamMatch("축구 하실?", 3, MatchStatus.WAITING, author, authorTeam);

		MatchProposal matchProposal = MatchProposal.builder()
			.match(match)
			.content("덤벼라!")
			.user(target)
			.team(targetTeam)
			.status(MatchProposalStatus.APPROVED)
			.build();

		entityManager.persist(author);
		entityManager.persist(authorTeam);
		entityManager.persist(target);
		entityManager.persist(targetTeam);
		entityManager.persist(match);
		entityManager.persist(matchProposal);

		String content = "hi";
		LocalDateTime chattedAt = LocalDateTime.now();

		//when
		matchChatService.chat(matchProposal.getId(), author.getId(), target.getId(), content, chattedAt);

		//then
		List<MatchChat> matchChats = matchChatRepository.findAll();
		assertThat(matchChats.isEmpty(), is(false));
		assertThat(matchChats.get(0).getProposal().getId(), is(matchProposal.getId()));
		assertThat(matchChats.get(0).getUser().getId(), is(author.getId()));
		assertThat(matchChats.get(0).getTarget().getId(), is(target.getId()));
		assertThat(matchChats.get(0).getContent(), is(content));
		assertThat(matchChats.get(0).getChattedAt(), is(chattedAt));
	}

	@Test
	@Transactional
	@DisplayName("존재하지 않는 매칭 요청 아이디로 채팅 등록 시, 오류가 발생한다.")
	void testFail_NotFoundProposal() {
		//given
		User author = getUser("author");
		User target = getUser("target");

		entityManager.persist(author);
		entityManager.persist(target);

		Long invalidId = 999L;

		//when, then
		assertThrows(EntityNotFoundException.class, () -> {
			matchChatService.chat(invalidId, author.getId(), target.getId(), "hi", LocalDateTime.now());
		});
	}

	@Test
	@Transactional
	@DisplayName("수락되지 않은 매칭 요청에 채팅 등록 시, 오류가 발생한다.")
	void testFail_NotApprovedProposal() {
		//given
		User author = getUser("author");
		Team authorTeam = getSoccerTeam("author", author);
		User target = getUser("target");
		Team targetTeam = getSoccerTeam("target", target);
		Match match = getSoccerTeamMatch("축구 하실?", 3, MatchStatus.WAITING, author, authorTeam);
		MatchProposal matchProposal = MatchProposal.builder()
			.match(match)
			.content("덤벼라!")
			.user(target)
			.team(targetTeam)
			.status(MatchProposalStatus.WAITING)
			.build();

		entityManager.persist(author);
		entityManager.persist(authorTeam);
		entityManager.persist(target);
		entityManager.persist(targetTeam);
		entityManager.persist(match);
		entityManager.persist(matchProposal);

		//when, then
		assertThrows(BusinessException.class, () -> {
			matchChatService.chat(matchProposal.getId(), author.getId(), target.getId(), "hi", LocalDateTime.now());
		});
	}

	@Nested
	@DisplayName("유효하지 않은 채팅 대상인")
	class InCorrectChattingPartner {

		@Test
		@Transactional
		@DisplayName("ID로 채팅 등록 시, 오류가 발생한다.")
		void testFail_InvalidTargetId() {
			//given
			User author = getUser("author");
			Team authorTeam = getSoccerTeam("author", author);
			User target = getUser("target");
			Team targetTeam = getSoccerTeam("target", target);
			Match match = getSoccerTeamMatch("축구 하실?", 3, MatchStatus.WAITING, author, authorTeam);
			MatchProposal matchProposal = MatchProposal.builder()
				.match(match)
				.content("덤벼라!")
				.user(target)
				.team(targetTeam)
				.status(MatchProposalStatus.WAITING)
				.build();

			entityManager.persist(author);
			entityManager.persist(authorTeam);
			entityManager.persist(target);
			entityManager.persist(targetTeam);
			entityManager.persist(match);
			entityManager.persist(matchProposal);

			Long invalidTargetId = 999L;

			//when, then
			assertThrows(BusinessException.class, () -> {
				matchChatService.chat(matchProposal.getId(), author.getId(), invalidTargetId, "hi", LocalDateTime.now());
			});
		}

		@Test
		@Transactional
		@DisplayName("작성자 ID로 채팅 등록 시, 오류가 발생한다.")
		void testFail_InvalidWriterId() {
			//given
			User author = getUser("author");
			Team authorTeam = getSoccerTeam("author", author);
			User target = getUser("target");
			Team targetTeam = getSoccerTeam("target", target);
			Match match = getSoccerTeamMatch("축구 하실?", 3, MatchStatus.WAITING, author, authorTeam);
			MatchProposal matchProposal = MatchProposal.builder()
				.match(match)
				.content("덤벼라!")
				.user(target)
				.team(targetTeam)
				.status(MatchProposalStatus.WAITING)
				.build();

			entityManager.persist(author);
			entityManager.persist(authorTeam);
			entityManager.persist(target);
			entityManager.persist(targetTeam);
			entityManager.persist(match);
			entityManager.persist(matchProposal);

			Long invalidAuthorId = 999L;

			//when, then
			assertThrows(BusinessException.class, () -> {
				matchChatService.chat(matchProposal.getId(), invalidAuthorId, target.getId(), "hi", LocalDateTime.now());
			});
		}

		@Test
		@Transactional
		@DisplayName("ID와 작성자 ID로 채팅 등록 시, 오류가 발생한다.")
		void testFail_InvalidTargetIdAndWriterId() {
			//given
			User author = getUser("author");
			Team authorTeam = getSoccerTeam("author", author);
			User target = getUser("target");
			Team targetTeam = getSoccerTeam("target", target);
			Match match = getSoccerTeamMatch("축구 하실?", 3, MatchStatus.WAITING, author, authorTeam);
			MatchProposal matchProposal = MatchProposal.builder()
				.match(match)
				.content("덤벼라!")
				.user(target)
				.team(targetTeam)
				.status(MatchProposalStatus.WAITING)
				.build();

			entityManager.persist(author);
			entityManager.persist(authorTeam);
			entityManager.persist(target);
			entityManager.persist(targetTeam);
			entityManager.persist(match);
			entityManager.persist(matchProposal);

			Long invalidTargetId = 999L;
			Long invalidAuthorId = 999L;

			//when, then
			assertThrows(BusinessException.class, () -> {
				matchChatService.chat(matchProposal.getId(), invalidAuthorId, invalidTargetId, "hi", LocalDateTime.now());
			});
		}
	}

	private static User getUser(String name) {
		return User.builder()
			.password("1234")
			.username(name)
			.nickname(name + "Nik")
			.build();
	}

	private static Team getSoccerTeam(String name, User user) {
		return Team.builder()
			.name(name + "-t")
			.description("we are team " + name)
			.sportsCategory(SportsCategory.SOCCER)
			.leader(user)
			.build();
	}

	private static Match getSoccerTeamMatch(String title, int participants, MatchStatus status, User user, Team team) {
		return Match.builder()
			.title(title)
			.sportsCategory(SportsCategory.SOCCER)
			.matchType(MatchType.TEAM_MATCH)
			.matchDate(LocalDate.now())
			.participants(participants)
			.status(status)
			.user(user)
			.team(team)
			.build();
	}
}