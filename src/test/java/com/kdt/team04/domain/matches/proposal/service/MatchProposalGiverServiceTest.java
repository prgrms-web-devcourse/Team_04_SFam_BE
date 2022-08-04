package com.kdt.team04.domain.matches.proposal.service;

import static com.kdt.team04.domain.matches.proposal.entity.MatchProposalStatus.APPROVED;
import static com.kdt.team04.domain.matches.proposal.entity.MatchProposalStatus.FIXED;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import com.amazonaws.services.s3.AmazonS3;
import com.kdt.team04.common.exception.BusinessException;
import com.kdt.team04.common.exception.EntityNotFoundException;
import com.kdt.team04.common.file.service.S3Uploader;
import com.kdt.team04.domain.matches.match.entity.Match;
import com.kdt.team04.domain.matches.match.entity.MatchStatus;
import com.kdt.team04.domain.matches.match.entity.MatchType;
import com.kdt.team04.domain.matches.proposal.dto.MatchProposalQueryDto;
import com.kdt.team04.domain.matches.proposal.dto.MatchProposalResponse;
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

	@MockBean
	S3Uploader s3Uploader;

	@MockBean
	AmazonS3 amazonS3;

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

		MatchProposalSimpleQueryDto expected = new MatchProposalSimpleQueryDto(
			proposal.getId(),
			proposal.getStatus(),
			target.getId(),
			author.getId(),
			match.getStatus()
		);

		//when
		MatchProposalSimpleQueryDto response = matchProposalGiver.findSimpleProposalById(proposal.getId());

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

	@Test
	@DisplayName("채팅 기록 조회를 위한 해당 매칭 정보를 조회한다.")
	void test_findChatMatchByProposalId() {
		//given
		User author = getUser("author");
		User target = getUser("target");
		Match match = getSoccerIndividualMatch("축구 하실?", MatchStatus.WAITING, author);

		MatchProposal proposal = getProposal(match, "덤벼라!", target, null, APPROVED);

		entityManager.persist(author);
		entityManager.persist(target);
		entityManager.persist(match);
		entityManager.persist(proposal);

		//when
		MatchProposalResponse.ChatMatch chatMatch
			= matchProposalGiver.findChatMatchByProposalId(proposal.getId(), author.getId());

		//then
		assertThat(chatMatch, notNullValue());
		assertThat(chatMatch.title(), is(match.getTitle()));
		assertThat(chatMatch.status(), is(match.getStatus()));
		assertThat(chatMatch.targetProfile().nickname(), is(target.getNickname()));
	}

	@Nested
	@DisplayName("채팅 기록 조회를 위한 해당 매칭 정보 조회 시")
	class FindChatMatchInfo {
		@Test
		@DisplayName("매칭 신청 정보가 없는 경우 오류가 발생한다.")
		void testFail_NotFoundProposalBy__findChatMatch() {
			//given
			User author = getUser("author");
			User target = getUser("target");
			Match match = getSoccerIndividualMatch("축구 하실?", MatchStatus.WAITING, author);

			entityManager.persist(author);
			entityManager.persist(target);
			entityManager.persist(match);

			Long invalidProposalId = 999L;

			//when, then
			assertThrows(BusinessException.class, () -> {
				matchProposalGiver.findChatMatchByProposalId(invalidProposalId, author.getId());
			});
		}

		@Test
		@DisplayName("작성자도 신청자도 아닌 경우 오류가 발생한다.")
		void testFail_AccessDeniedBy__findChatMatch() {
			//given
			User author = getUser("author");
			User target = getUser("target");
			Match match = getSoccerIndividualMatch("축구 하실?", MatchStatus.WAITING, author);

			MatchProposal proposal = getProposal(match, "덤벼라!", target, null, APPROVED);

			entityManager.persist(author);
			entityManager.persist(target);
			entityManager.persist(match);
			entityManager.persist(proposal);

			User invalidUser = getUser("invalid");
			entityManager.persist(invalidUser);

			//when, then
			assertThrows(BusinessException.class, () -> {
				matchProposalGiver.findChatMatchByProposalId(proposal.getId(), invalidUser.getId());
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

	private static MatchProposal getProposal(Match match, String content, User proposer, Team proposerTeam, MatchProposalStatus status) {
		return MatchProposal.builder()
			.match(match)
			.content(content)
			.user(proposer)
			.team(proposerTeam)
			.status(status)
			.build();
	}
}