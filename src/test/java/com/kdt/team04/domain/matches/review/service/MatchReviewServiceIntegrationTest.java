package com.kdt.team04.domain.matches.review.service;

import static com.kdt.team04.domain.matches.review.entity.MatchReviewValue.BEST;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.kdt.team04.common.exception.BusinessException;
import com.kdt.team04.domain.matches.match.entity.Match;
import com.kdt.team04.domain.matches.match.entity.MatchStatus;
import com.kdt.team04.domain.matches.match.entity.MatchType;
import com.kdt.team04.domain.matches.proposal.entity.MatchProposal;
import com.kdt.team04.domain.matches.proposal.entity.MatchProposalStatus;
import com.kdt.team04.domain.matches.review.entity.MatchReview;
import com.kdt.team04.domain.matches.review.repository.MatchReviewRepository;
import com.kdt.team04.domain.team.SportsCategory;
import com.kdt.team04.domain.team.entity.Team;
import com.kdt.team04.domain.user.entity.User;

@Transactional
@SpringBootTest
class MatchReviewServiceIntegrationTest {

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private MatchReviewService matchReviewService;

	@Autowired
	private MatchReviewRepository matchReviewRepository;

	@Test
	@DisplayName("팀전 종료 후 경기 후기를 등록한다.")
	void test_review() {
		//given
		User author = getUser("author");
		Team authorTeam = getSoccerTeam("author", author);
		User target = getUser("target");
		Team targetTeam = getSoccerTeam("target", target);
		Match match = getSoccerTeamMatch("축구 하실?", 3, MatchStatus.END, author, authorTeam);

		MatchProposal matchProposal = MatchProposal.builder()
			.match(match)
			.content("덤벼라!")
			.user(target)
			.team(targetTeam)
			.status(MatchProposalStatus.FIXED)
			.build();

		entityManager.persist(author);
		entityManager.persist(authorTeam);
		entityManager.persist(target);
		entityManager.persist(targetTeam);
		entityManager.persist(match);
		entityManager.persist(matchProposal);

		//when
		Long savedId = matchReviewService.review(match.getId(), BEST, author.getId());

		//then
		assertThat(savedId, notNullValue());
		MatchReview matchReview = entityManager.find(MatchReview.class, savedId);
		assertThat(matchReview, notNullValue());
		assertThat(matchReview.getMatch().getId(), is(match.getId()));
		assertThat(matchReview.getTeam().getId(), is(authorTeam.getId()));
		assertThat(matchReview.getTargetTeam().getId(), is(targetTeam.getId()));
		assertThat(matchReview.getReview(), is(BEST));
	}

	@Test
	@DisplayName("개인전 종료 후 경기 후기를 등록한다.")
	void test_review_individualGame() {
		//given
		User author = getUser("author");
		User target = getUser("target");
		Match match = getSoccerIndividualMatch("축구 하실?", 1, MatchStatus.END, author, null);

		MatchProposal matchProposal = MatchProposal.builder()
			.match(match)
			.content("덤벼라!")
			.user(target)
			.team(null)
			.status(MatchProposalStatus.FIXED)
			.build();

		entityManager.persist(author);
		entityManager.persist(target);
		entityManager.persist(match);
		entityManager.persist(matchProposal);

		//when
		Long savedId = matchReviewService.review(match.getId(), BEST, author.getId());

		//then
		assertThat(savedId, notNullValue());
		MatchReview matchReview = entityManager.find(MatchReview.class, savedId);
		assertThat(matchReview, notNullValue());
		assertThat(matchReview.getMatch().getId(), is(match.getId()));
		assertThat(matchReview.getUser().getId(), is(author.getId()));
		assertThat(matchReview.getTargetUser().getId(), is(target.getId()));
		assertThat(matchReview.getReview(), is(BEST));
	}

	@Nested
	@DisplayName("경기 후기 등록 시")
	class review {

		@Test
		@DisplayName("경기 종료 상태가 아닌 경우 오류가 발생한다.")
		void testFail_MatchNotEnded() {
			User author = getUser("author");
			User target = getUser("target");
			Match match = getSoccerIndividualMatch("축구 하실?", 1, MatchStatus.IN_GAME, author, null);

			MatchProposal matchProposal = MatchProposal.builder()
				.match(match)
				.content("덤벼라!")
				.user(target)
				.team(null)
				.status(MatchProposalStatus.FIXED)
				.build();

			entityManager.persist(author);
			entityManager.persist(target);
			entityManager.persist(match);
			entityManager.persist(matchProposal);

			//when, then
			assertThrows(BusinessException.class, () -> {
				matchReviewService.review(match.getId(), BEST, author.getId());
			});
		}

		@Test
		@DisplayName("경기 공고 작성자 혹은 신청자가 아닌 경우 오류가 발생한다.")
		void testFail_AccessDenied() {
			User author = getUser("author");
			User target = getUser("target");
			Match match = getSoccerIndividualMatch("축구 하실?", 1, MatchStatus.END, author, null);

			MatchProposal matchProposal = MatchProposal.builder()
				.match(match)
				.content("덤벼라!")
				.user(target)
				.team(null)
				.status(MatchProposalStatus.FIXED)
				.build();

			entityManager.persist(author);
			entityManager.persist(target);
			entityManager.persist(match);
			entityManager.persist(matchProposal);

			User invalidUser = getUser("invalid");
			entityManager.persist(invalidUser);

			//when, then
			assertThrows(BusinessException.class, () -> {
				matchReviewService.review(match.getId(), BEST, invalidUser.getId());
			});
		}

		@Test
		@DisplayName("해당 사용자 리뷰가 이미 등록된 경우 오류가 발생한다.")
		void testFail_AlreadyExists() {
			//given
			User author = getUser("author");
			User target = getUser("target");
			Match match = getSoccerIndividualMatch("축구 하실?", 1, MatchStatus.END, author, null);

			MatchProposal matchProposal = MatchProposal.builder()
				.match(match)
				.content("덤벼라!")
				.user(target)
				.team(null)
				.status(MatchProposalStatus.FIXED)
				.build();

			entityManager.persist(author);
			entityManager.persist(target);
			entityManager.persist(match);
			entityManager.persist(matchProposal);

			matchReviewService.review(match.getId(), BEST, author.getId());

			//when
			assertThrows(BusinessException.class, () -> {
				matchReviewService.review(match.getId(), BEST, author.getId());
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

	private static Match getSoccerIndividualMatch(String title, int participants, MatchStatus status, User user, Team team) {
		return Match.builder()
			.title(title)
			.sportsCategory(SportsCategory.SOCCER)
			.matchType(MatchType.INDIVIDUAL_MATCH)
			.matchDate(LocalDate.now())
			.participants(participants)
			.status(status)
			.user(user)
			.team(team)
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