package com.kdt.team04.domain.matches.match.service;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import com.amazonaws.services.s3.AmazonS3;
import com.kdt.team04.common.exception.BusinessException;
import com.kdt.team04.common.file.service.S3Uploader;
import com.kdt.team04.domain.matches.match.entity.Match;
import com.kdt.team04.domain.matches.match.entity.MatchStatus;
import com.kdt.team04.domain.matches.match.entity.MatchType;
import com.kdt.team04.domain.matches.proposal.entity.MatchProposal;
import com.kdt.team04.domain.matches.proposal.entity.MatchProposalStatus;
import com.kdt.team04.domain.team.SportsCategory;
import com.kdt.team04.domain.team.entity.Team;
import com.kdt.team04.domain.user.entity.User;

@Transactional
@SpringBootTest
class MatchGiverServiceTest {

	@Autowired
	MatchGiverService matchGiverService;

	@Autowired
	EntityManager entityManager;

	@MockBean
	S3Uploader s3Uploader;

	@MockBean
	AmazonS3 amazonS3;

	@Test
	@Transactional
	@DisplayName("경기 종료 시, 경기 종료 상태로 변경한다.")
	void test_endGame() {
		//given
		User author = getUser("author");
		User target = getUser("target");
		Match match = getSoccerIndividualMatch("축구 하실?", 3, MatchStatus.IN_GAME, author, null);

		entityManager.persist(author);
		entityManager.persist(target);
		entityManager.persist(match);

		//when
		matchGiverService.endGame(match.getId(), author.getId());
	}

	@Nested
	@DisplayName("경기 결과 등록 시")
	class endGame {
		@Test
		@Transactional
		@DisplayName("경기가 모집 완료 상태가 아닌 경우 오류가 발생한다.")
		void testFail_Match_NotInGame() {
			//given
			User author = getUser("author");
			User target = getUser("target");
			Match match = getSoccerIndividualMatch("축구 하실?", 3, MatchStatus.WAITING, author, null);

			MatchProposal matchProposal = MatchProposal.builder()
				.match(match)
				.content("덤벼라!")
				.user(target)
				.team(null)
				.status(MatchProposalStatus.APPROVED)
				.build();

			entityManager.persist(author);
			entityManager.persist(target);
			entityManager.persist(match);
			entityManager.persist(matchProposal);

			//when, then
			assertThrows(BusinessException.class, () -> {
				matchGiverService.endGame(match.getId(), author.getId());
			});
		}

		@Test
		@Transactional
		@DisplayName("매칭 작성자가 아닌 경우 오류가 발생한다.")
		void testFail_NotMatchAuthor() {
			//given
			User author = getUser("author");
			User target = getUser("target");
			Match match = getSoccerIndividualMatch("축구 하실?", 3, MatchStatus.WAITING, author, null);

			MatchProposal matchProposal = MatchProposal.builder()
				.match(match)
				.content("덤벼라!")
				.user(target)
				.team(null)
				.status(MatchProposalStatus.APPROVED)
				.build();

			entityManager.persist(author);
			entityManager.persist(target);
			entityManager.persist(match);
			entityManager.persist(matchProposal);

			Long invalidUserId = target.getId();

			//when, then
			assertThrows(BusinessException.class, () -> {
				matchGiverService.endGame(match.getId(), invalidUserId);
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