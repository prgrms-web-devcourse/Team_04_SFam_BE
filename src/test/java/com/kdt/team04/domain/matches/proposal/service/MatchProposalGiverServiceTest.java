package com.kdt.team04.domain.matches.proposal.service;

import static com.kdt.team04.domain.matches.proposal.entity.MatchProposalStatus.APPROVED;
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
import org.springframework.transaction.annotation.Transactional;

import com.kdt.team04.common.exception.BusinessException;
import com.kdt.team04.domain.matches.match.entity.Match;
import com.kdt.team04.domain.matches.match.entity.MatchStatus;
import com.kdt.team04.domain.matches.match.entity.MatchType;
import com.kdt.team04.domain.matches.proposal.dto.MatchProposalQueryDto;
import com.kdt.team04.domain.matches.proposal.dto.MatchProposalResponse;
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
	private MatchProposalGiverService matchProposalGiverService;

	@Test
	@DisplayName("채팅 등록을 위한 매칭 신청 정보를 간단 조회한다.")
	void test_findSimpleProposalById() {
		//given
		User author = getUser("author");
		User target = getUser("target");
		Match match = getSoccerIndividualMatch("축구 하실?", MatchStatus.WAITING, author);

		MatchProposal proposal = getProposal(match, "덤벼라!", target, null, APPROVED);

		entityManager.persist(author);
		entityManager.persist(target);
		entityManager.persist(match);
		entityManager.persist(proposal);

		MatchProposalQueryDto expected = new MatchProposalQueryDto(
			proposal.getId(),
			proposal.getStatus(),
			proposal.getUser().getId(),
			match.getUser().getId(),
			match.getStatus()
		);

		//when
		MatchProposalQueryDto queryDto = matchProposalGiverService.findSimpleProposalById(proposal.getId());

		//then
		assertThat(queryDto, samePropertyValuesAs(expected));
	}

	@Test
	@DisplayName("존재하지 않는 신청 정보 조회 시, 오류가 발생한다.")
	void testFail_NotFoundBy_findSimpleProposalById() {
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
			matchProposalGiverService.findSimpleProposalById(invalidProposalId);
		});
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
			= matchProposalGiverService.findChatMatchByProposalId(proposal.getId(), author.getId());

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
				matchProposalGiverService.findChatMatchByProposalId(invalidProposalId, author.getId());
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
				matchProposalGiverService.findChatMatchByProposalId(proposal.getId(), invalidUser.getId());
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

	private static MatchProposal getProposal(Match match, String content, User proposer, Team proposerTeam, MatchProposalStatus status) {
		return MatchProposal.builder()
			.match(match)
			.content("덤벼라!")
			.user(proposer)
			.team(proposerTeam)
			.status(APPROVED)
			.build();
	}
}