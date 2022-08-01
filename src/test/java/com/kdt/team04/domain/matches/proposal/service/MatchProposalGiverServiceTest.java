package com.kdt.team04.domain.matches.proposal.service;

import static com.kdt.team04.domain.matches.proposal.entity.MatchProposalStatus.FIXED;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.samePropertyValuesAs;
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
import com.kdt.team04.common.exception.EntityNotFoundException;
import com.kdt.team04.domain.matches.match.entity.Match;
import com.kdt.team04.domain.matches.match.entity.MatchStatus;
import com.kdt.team04.domain.matches.match.entity.MatchType;
import com.kdt.team04.domain.matches.proposal.dto.MatchProposalQueryDto;
import com.kdt.team04.domain.matches.proposal.entity.MatchProposal;
import com.kdt.team04.domain.matches.proposal.entity.MatchProposalStatus;
import com.kdt.team04.domain.team.SportsCategory;
import com.kdt.team04.domain.team.entity.Team;
import com.kdt.team04.domain.user.entity.User;

@Transactional
@SpringBootTest
class MatchProposalGiverServiceTest {

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private MatchProposalGiverService matchProposalGiver;

	@Test
	@DisplayName("채팅 등록을 위해 신청 정보를 조회한다.")
	void test_findSimpleProposalById() {
		//given
		User author = getUser("author");
		User target = getUser("target");
		Match match = getSoccerIndividualMatch("축구 하실?", MatchStatus.IN_GAME, author);

		MatchProposal proposal = MatchProposal.builder()
			.match(match)
			.content("덤벼라!")
			.user(target)
			.status(MatchProposalStatus.APPROVED)
			.build();

		entityManager.persist(author);
		entityManager.persist(target);
		entityManager.persist(match);
		entityManager.persist(proposal);

		MatchProposalQueryDto expected = new MatchProposalQueryDto(
			proposal.getId(),
			proposal.getStatus(),
			target.getId(),
			author.getId(),
			match.getStatus()
		);

		//when
		MatchProposalQueryDto response = matchProposalGiver.findSimpleProposalById(proposal.getId());

		//then
		assertThat(response, samePropertyValuesAs(expected));
	}

	@Test
	@DisplayName("채팅 등록을 위해 신청 정보 조회 시, 존재하지 않으면 오류가 발생한다.")
	void testFail_NotFoundBy_findSimpleProposalById() {
		//given
		User author = getUser("author");
		User target = getUser("target");
		Match match = getSoccerIndividualMatch("축구 하실?", MatchStatus.IN_GAME, author);

		entityManager.persist(author);
		entityManager.persist(target);
		entityManager.persist(match);

		Long invalidProposalId = 999L;

		//when, then
		assertThrows(EntityNotFoundException.class, () -> {
			matchProposalGiver.findSimpleProposalById(invalidProposalId);
		});
	}

	@Test
	@DisplayName("경기 종료 시, 대결 상대를 확정하기 위해 신청 상태를 Fixed 변경한다.")
	void test_updateToFixed() {
		//given
		User author = getUser("author");
		User target = getUser("target");
		Match match = getSoccerIndividualMatch("축구 하실?", MatchStatus.IN_GAME, author);

		MatchProposal proposal = MatchProposal.builder()
			.match(match)
			.content("덤벼라!")
			.user(target)
			.status(MatchProposalStatus.APPROVED)
			.build();

		entityManager.persist(author);
		entityManager.persist(target);
		entityManager.persist(match);
		entityManager.persist(proposal);

		//when
		matchProposalGiver.updateToFixed(proposal.getId());

		//then
		MatchProposal proposalResponse = entityManager.find(MatchProposal.class, proposal.getId());
		assertThat(proposalResponse.getStatus(), is(FIXED));
	}

	@Nested
	@DisplayName("신청 정보를 Fixed 변경 시")
	class UpdateToFixed {
		@Test
		@DisplayName("신청 정보가 존재하지 않는 경우 오류가 발생한다.")
		void testFail_NotFoundBy_updateToFixed() {
			//given
			User author = getUser("author");
			User target = getUser("target");
			Match match = getSoccerIndividualMatch("축구 하실?", MatchStatus.IN_GAME, author);

			entityManager.persist(author);
			entityManager.persist(target);
			entityManager.persist(match);

			Long invalidProposalId = 999L;

			//when, then
			assertThrows(EntityNotFoundException.class, () -> {
				matchProposalGiver.updateToFixed(invalidProposalId);
			});
		}

		@Test
		@DisplayName("신청 정보 상태가 APPROVED 아닐 시 오류가 발생한다.")
		void testFail_NotStatusApprovedBy_updateToFixed() {
			//given
			User author = getUser("author");
			User target = getUser("target");
			Match match = getSoccerIndividualMatch("축구 하실?", MatchStatus.IN_GAME, author);

			MatchProposal proposal = MatchProposal.builder()
				.match(match)
				.content("덤벼라!")
				.user(target)
				.status(MatchProposalStatus.WAITING)
				.build();

			entityManager.persist(author);
			entityManager.persist(target);
			entityManager.persist(match);
			entityManager.persist(proposal);

			//when, then
			assertThrows(BusinessException.class, () -> {
				matchProposalGiver.updateToFixed(proposal.getId());
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

	private static Match getSoccerIndividualMatch(String title, MatchStatus status, User user) {
		return Match.builder()
			.title(title)
			.sportsCategory(SportsCategory.SOCCER)
			.matchType(MatchType.INDIVIDUAL_MATCH)
			.matchDate(LocalDate.now())
			.participants(1)
			.status(status)
			.user(user)
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