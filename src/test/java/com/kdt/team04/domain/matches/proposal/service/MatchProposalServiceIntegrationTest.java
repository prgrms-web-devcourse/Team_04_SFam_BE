package com.kdt.team04.domain.matches.proposal.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.is;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import javax.persistence.EntityManager;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.kdt.team04.common.exception.BusinessException;
import com.kdt.team04.domain.matches.match.entity.Match;
import com.kdt.team04.domain.matches.match.entity.MatchStatus;
import com.kdt.team04.domain.matches.match.entity.MatchType;
import com.kdt.team04.domain.matches.proposal.dto.MatchChatResponse;
import com.kdt.team04.domain.matches.proposal.dto.MatchProposalRequest;
import com.kdt.team04.domain.matches.proposal.dto.MatchProposalResponse;
import com.kdt.team04.domain.matches.proposal.entity.MatchChat;
import com.kdt.team04.domain.matches.proposal.entity.MatchProposal;
import com.kdt.team04.domain.matches.proposal.entity.MatchProposalStatus;
import com.kdt.team04.domain.team.SportsCategory;
import com.kdt.team04.domain.team.entity.Team;
import com.kdt.team04.domain.user.entity.User;

@Transactional
@SpringBootTest
class MatchProposalServiceIntegrationTest {

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private MatchProposalService matchProposalService;

	@Test
	@DisplayName("개인전 매칭을 신청하고 해당 신청 생성 후 Id 값을 return 한다.")
	void testIndividualCreateSuccess() {
		//given
		User author = new User("author", "author", "aA1234!");
		User proposer = new User("proposer", "proposer", "aA1234!");
		entityManager.persist(author);
		entityManager.persist(proposer);

		Match match = Match.builder()
			.title("match")
			.status(MatchStatus.WAITING)
			.matchDate(LocalDate.now())
			.matchType(MatchType.INDIVIDUAL_MATCH)
			.participants(1)
			.user(author)
			.sportsCategory(SportsCategory.BADMINTON)
			.content("content")
			.build();
		entityManager.persist(match);
		MatchProposalRequest.ProposalCreate request = new MatchProposalRequest.ProposalCreate(null, "개인전 신청합니다.");

		//when
		Long createdProposer = matchProposalService.create(proposer.getId(), match.getId(), request);

		//then
		assertThat(createdProposer).isNotNull();
	}

	@Test
	@DisplayName("팀전 매칭을 신청하고 해당 신청 생성 후 Id 값을 return 한다.")
	void testTeamProposerCreateSuccess() {
		//given
		User author = new User("author", "author", "aA1234!");
		User proposer = new User("proposer", "proposer", "aA1234!");
		Team authorTeam = Team.builder()
			.name("team1")
			.description("first team")
			.sportsCategory(SportsCategory.BADMINTON)
			.leader(proposer)
			.build();
		Team proposerTeam = Team.builder()
			.name("team1")
			.description("first team")
			.sportsCategory(SportsCategory.BADMINTON)
			.leader(proposer)
			.build();
		entityManager.persist(authorTeam);
		entityManager.persist(proposerTeam);
		entityManager.persist(author);
		entityManager.persist(proposer);

		Match match = Match.builder()
			.title("match")
			.status(MatchStatus.WAITING)
			.matchDate(LocalDate.now())
			.matchType(MatchType.TEAM_MATCH)
			.participants(3)
			.user(author)
			.team(authorTeam)
			.sportsCategory(SportsCategory.BADMINTON)
			.content("content")
			.build();
		entityManager.persist(match);
		MatchProposalRequest.ProposalCreate request = new MatchProposalRequest.ProposalCreate(proposerTeam.getId(),
			"팀전 신청합니다.");

		//when
		Long createdProposer = matchProposalService.create(proposer.getId(), match.getId(), request);

		//then
		assertThat(createdProposer).isNotNull();
	}

	@Test
	@DisplayName("팀전 매칭을 신청시 request의 teamId가 null일 경우 예외가 발생한다.")
	void testTeamProposerCreateFail() {
		//given
		User author = new User("author", "author", "aA1234!");
		User proposer = new User("proposer", "proposer", "aA1234!");
		Team authorTeam = Team.builder()
			.name("team1")
			.description("first team")
			.sportsCategory(SportsCategory.BADMINTON)
			.leader(proposer)
			.build();
		Team proposerTeam = Team.builder()
			.name("team1")
			.description("first team")
			.sportsCategory(SportsCategory.BADMINTON)
			.leader(proposer)
			.build();
		entityManager.persist(authorTeam);
		entityManager.persist(proposerTeam);
		entityManager.persist(author);
		entityManager.persist(proposer);

		Match match = Match.builder()
			.title("match")
			.status(MatchStatus.WAITING)
			.matchDate(LocalDate.now())
			.matchType(MatchType.TEAM_MATCH)
			.participants(3)
			.user(author)
			.team(authorTeam)
			.sportsCategory(SportsCategory.BADMINTON)
			.content("content")
			.build();
		entityManager.persist(match);
		MatchProposalRequest.ProposalCreate request = new MatchProposalRequest.ProposalCreate(null, "팀전 신청합니다.");

		//when, then
		assertThatThrownBy(() -> matchProposalService.create(proposer.getId(), match.getId(), request))
			.isInstanceOf(BusinessException.class);
	}

	@Test
	@DisplayName("매칭 ID로 매칭 신청 목록을 조회한다.")
	void test_findAllLastChats() {
		//given
		String lastChat = "마지막 채팅";

		User author = new User("author", "authorNik", "aA1234!");
		User target = new User("target", "targetNik", "aA1234!");
		Team authorTeam = Team.builder()
			.name("authorTeam")
			.description("first team")
			.sportsCategory(SportsCategory.SOCCER)
			.leader(author)
			.build();
		Team targetTeam = Team.builder()
			.name("targetTeam")
			.description("first team")
			.sportsCategory(SportsCategory.SOCCER)
			.leader(target)
			.build();
		entityManager.persist(author);
		entityManager.persist(target);
		entityManager.persist(authorTeam);
		entityManager.persist(targetTeam);

		Match match = Match.builder()
			.title("덤벼라!")
			.status(MatchStatus.WAITING)
			.matchDate(LocalDate.now())
			.matchType(MatchType.TEAM_MATCH)
			.participants(3)
			.user(author)
			.team(authorTeam)
			.sportsCategory(SportsCategory.SOCCER)
			.content("축구 하실?")
			.build();
		entityManager.persist(match);

		List<MatchProposal> proposals = new ArrayList<>();
		IntStream.range(1, 3)
			.forEach(id -> {
				MatchProposal matchProposal = MatchProposal.builder()
					.match(match)
					.content("덤벼라! 나는 " + id)
					.user(target)
					.team(targetTeam)
					.status(MatchProposalStatus.APPROVED)
					.build();

				proposals.add(matchProposal);
				entityManager.persist(matchProposal);
			});

		List<MatchChat> chats = new ArrayList<>();
		proposals.forEach(proposal -> {
			IntStream.range(1, 5)
				.forEach(id -> {
					MatchChat chat = MatchChat.builder()
						.proposal(proposal)
						.user(author)
						.target(target)
						.content(id == 4 ? lastChat + proposal.getId() : "칫챗")
						.chattedAt(LocalDateTime.now())
						.build();
					chats.add(chat);
					entityManager.persist(chat);
				});
		});

		Map<Long, String> expectedChats = new HashMap<>();
		proposals.forEach(proposal -> {
			expectedChats.put(proposal.getId(), lastChat + proposal.getId());
		});

		//when
		List<MatchProposalResponse.Chat> foundProposlas = matchProposalService.findAllProposals(match.getId(), author.getId());

		//then
		assertThat(foundProposlas).hasSize(2);
		foundProposlas.forEach(proposal -> {
			assertThat(proposal.id()).isNotNull();
			assertThat(proposal.target()).isNotNull();
			assertThat(proposal.lastChat()).isNotNull();
			assertThat(proposal.target().nickname()).isEqualTo(target.getNickname());
			assertThat(proposal.lastChat().content()).isEqualTo(expectedChats.get(proposal.id()));
		});
	}

	@Test
	@DisplayName("매칭에 대한 신청이 없다면 매칭 신청 목록 조회 시, 오류가 발생한다.")
	void testFail_EmptyProposalBy_findAllLastChats() {
		User author = new User("author", "authorNik", "aA1234!");
		User target = new User("target", "targetNik", "aA1234!");
		Team authorTeam = Team.builder()
			.name("authorTeam")
			.description("first team")
			.sportsCategory(SportsCategory.SOCCER)
			.leader(author)
			.build();
		Team targetTeam = Team.builder()
			.name("targetTeam")
			.description("first team")
			.sportsCategory(SportsCategory.SOCCER)
			.leader(target)
			.build();
		entityManager.persist(author);
		entityManager.persist(target);
		entityManager.persist(authorTeam);
		entityManager.persist(targetTeam);

		Match match = Match.builder()
			.title("덤벼라!")
			.status(MatchStatus.WAITING)
			.matchDate(LocalDate.now())
			.matchType(MatchType.TEAM_MATCH)
			.participants(3)
			.user(author)
			.team(authorTeam)
			.sportsCategory(SportsCategory.SOCCER)
			.content("축구 하실?")
			.build();
		entityManager.persist(match);

		//when, then
		assertThatThrownBy(() -> {
			matchProposalService.findAllProposals(match.getId(), author.getId());
		}).isInstanceOf(BusinessException.class);
	}

	@Test
	@DisplayName("매칭 작성자가 아니라면 매칭 신청 목록 조회 시, 오류가 발생한다.")
	void testFail_EmptyProposalBy_findAllLastChatsWith_WrongAuthorId() {
		//given
		String lastChat = "마지막 채팅";

		User author = new User("author", "authorNik", "aA1234!");
		User target = new User("target", "targetNik", "aA1234!");
		Team authorTeam = Team.builder()
			.name("authorTeam")
			.description("first team")
			.sportsCategory(SportsCategory.SOCCER)
			.leader(author)
			.build();
		Team targetTeam = Team.builder()
			.name("targetTeam")
			.description("first team")
			.sportsCategory(SportsCategory.SOCCER)
			.leader(target)
			.build();
		entityManager.persist(author);
		entityManager.persist(target);
		entityManager.persist(authorTeam);
		entityManager.persist(targetTeam);

		Match match = Match.builder()
			.title("덤벼라!")
			.status(MatchStatus.WAITING)
			.matchDate(LocalDate.now())
			.matchType(MatchType.TEAM_MATCH)
			.participants(3)
			.user(author)
			.team(authorTeam)
			.sportsCategory(SportsCategory.SOCCER)
			.content("축구 하실?")
			.build();
		entityManager.persist(match);

		List<MatchProposal> proposals = new ArrayList<>();
		IntStream.range(1, 3)
			.forEach(id -> {
				MatchProposal matchProposal = MatchProposal.builder()
					.match(match)
					.content("덤벼라! 나는 " + id)
					.user(target)
					.team(targetTeam)
					.status(MatchProposalStatus.APPROVED)
					.build();

				proposals.add(matchProposal);
				entityManager.persist(matchProposal);
			});

		List<MatchChat> chats = new ArrayList<>();
		proposals.forEach(proposal -> {
			IntStream.range(1, 5)
				.forEach(id -> {
					MatchChat chat = MatchChat.builder()
						.proposal(proposal)
						.user(author)
						.target(target)
						.content(id == 4 ? lastChat + proposal.getId() : "칫챗")
						.chattedAt(LocalDateTime.now())
						.build();
					chats.add(chat);
					entityManager.persist(chat);
				});
		});

		Map<Long, String> expectedChats = new HashMap<>();
		proposals.forEach(proposal -> {
			expectedChats.put(proposal.getId(), lastChat + proposal.getId());
		});

		Long invalidUserId = target.getId();

		//when, then
		assertThatThrownBy(() -> {
			matchProposalService.findAllProposals(match.getId(), invalidUserId);
		}).isInstanceOf(BusinessException.class);
	}
}