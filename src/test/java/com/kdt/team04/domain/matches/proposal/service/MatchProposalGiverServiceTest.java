package com.kdt.team04.domain.matches.proposal.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.kdt.team04.common.exception.BusinessException;
import com.kdt.team04.domain.matches.match.entity.Match;
import com.kdt.team04.domain.matches.match.entity.MatchStatus;
import com.kdt.team04.domain.matches.match.entity.MatchType;
import com.kdt.team04.domain.matches.proposal.dto.MatchProposalQueryDto;
import com.kdt.team04.domain.matches.proposal.dto.MatchProposalSimpleQueryDto;
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
	@DisplayName("매칭 신청 정보를 간단 조회한다.")
	void test_findSimpleProposalById() {
		//given
		User author = getUser("author");
		Team authorTeam = getSoccerTeam("author", author);
		User target = getUser("target");
		Team targetTeam = getSoccerTeam("target", target);
		Match match = getSoccerTeamMatch("축구 하실?", 3, MatchStatus.IN_GAME, author, authorTeam);

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

		MatchProposalSimpleQueryDto expectedQueryDto = new MatchProposalSimpleQueryDto(
			matchProposal.getId(),
			matchProposal.getStatus(),
			matchProposal.getUser().getId(),
			match.getUser().getId(),
			match.getStatus()
		);

		//when
		MatchProposalSimpleQueryDto simpleProposal = matchProposalGiver.findSimpleProposalById(matchProposal.getId());

		//then
		assertThat(simpleProposal, samePropertyValuesAs(expectedQueryDto));
	}

	@Test
	@DisplayName("존재하지 않는 매칭 신청 조회 시, 오류가 발생한다.")
	void testFail_searchNotExistsProposal() {
		//given
		User author = getUser("author");
		Team authorTeam = getSoccerTeam("author", author);
		User target = getUser("target");
		Team targetTeam = getSoccerTeam("target", target);
		Match match = getSoccerTeamMatch("축구 하실?", 3, MatchStatus.IN_GAME, author, authorTeam);

		entityManager.persist(author);
		entityManager.persist(authorTeam);
		entityManager.persist(target);
		entityManager.persist(targetTeam);
		entityManager.persist(match);

		Long invalidProposalId = 999L;

		//when, then
		assertThrows(BusinessException.class, () -> {
			matchProposalGiver.findSimpleProposalById(invalidProposalId);
		});
	}

	@Test
	@DisplayName("경기 종료된 매칭과 상대 정보를 조회한다.")
	void test_findFixedProposalByMatchId() {
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

		MatchProposalQueryDto expectedQueryDto = new MatchProposalQueryDto(
			matchProposal.getId(),
			matchProposal.getUser().getId(),
			matchProposal.getTeam().getId(),
			match.getId(),
			match.getStatus(),
			match.getMatchType(),
			match.getUser().getId(),
			match.getTeam().getId()
		);

		//when
		MatchProposalQueryDto fixedProposal = matchProposalGiver.findFixedProposalByMatchId(match.getId());

		//then
		assertThat(fixedProposal, samePropertyValuesAs(expectedQueryDto));
	}

	@Test
	@DisplayName("경기 완료가 되었는데, Fixed 된 신청이 없으면 오류가 발생한다.")
	void testFail_MatchEnded_ButNotExists_FixedProposal() {
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
			.status(MatchProposalStatus.APPROVED)
			.build();

		entityManager.persist(author);
		entityManager.persist(authorTeam);
		entityManager.persist(target);
		entityManager.persist(targetTeam);
		entityManager.persist(match);
		entityManager.persist(matchProposal);

		//when, then
		assertThrows(BusinessException.class, () -> {
			matchProposalGiver.findFixedProposalByMatchId(match.getId());
		});
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