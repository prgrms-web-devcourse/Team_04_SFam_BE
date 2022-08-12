package com.kdt.team04.domain.matches.proposal.repository;

import static com.kdt.team04.domain.matches.proposal.entity.MatchProposalStatus.APPROVED;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import com.amazonaws.services.s3.AmazonS3;
import com.kdt.team04.common.aws.s3.S3Uploader;
import com.kdt.team04.domain.matches.match.model.MatchStatus;
import com.kdt.team04.domain.matches.match.model.MatchType;
import com.kdt.team04.domain.matches.match.model.entity.Match;
import com.kdt.team04.domain.matches.proposal.dto.QueryProposalChatResponse;
import com.kdt.team04.domain.matches.proposal.entity.MatchChat;
import com.kdt.team04.domain.matches.proposal.entity.MatchProposal;
import com.kdt.team04.domain.matches.proposal.entity.MatchProposalStatus;
import com.kdt.team04.domain.teams.team.model.SportsCategory;
import com.kdt.team04.domain.teams.team.model.entity.Team;
import com.kdt.team04.domain.user.entity.User;

@Transactional
@SpringBootTest
class MatchProposalRepositoryCustomImplTest {

	@PersistenceContext
	EntityManager entityManager;

	@Autowired
	MatchProposalRepository proposalRepository;

	@MockBean
	S3Uploader s3Uploader;

	@MockBean
	AmazonS3 amazonS3;

	@Test
	@DisplayName("회원 Id 기준으로 신청(채팅방) 목록을 일자 기준(신청일자 or 마지막 채팅 기록 일자) 내림차순 정렬 조회한다.")
	void findAllProposalByUserID_OderByProposalDateOrLastChatDate() {
		//given
		User author = getUser("author");
		Team authorTeam = getSoccerTeam("author", author);
		entityManager.persist(author);
		entityManager.persist(authorTeam);

		Match match = getSoccerTeamMatch("축구 하실?", 3, MatchStatus.WAITING, author, authorTeam);
		entityManager.persist(match);

		User target1 = getUser("target1");
		Team targetTeam1 = getSoccerTeam("target1", target1);
		MatchProposal proposal1 = getProposal(match, "덤벼라!", target1, targetTeam1, APPROVED);
		entityManager.persist(target1);
		entityManager.persist(targetTeam1);
		entityManager.persist(proposal1);

		User target2 = getUser("target2");
		Team targetTeam2 = getSoccerTeam("target2", target2);
		MatchProposal proposal2 = getProposal(match, "마!", target2, targetTeam2, APPROVED);
		entityManager.persist(target2);
		entityManager.persist(targetTeam2);
		entityManager.persist(proposal2);

		User target3 = getUser("target3");
		Team targetTeam3 = getSoccerTeam("target3", target3);
		MatchProposal proposal3 = getProposal(match, "하모!", target3, targetTeam3, APPROVED);
		entityManager.persist(target3);
		entityManager.persist(targetTeam3);
		entityManager.persist(proposal3);

		MatchChat chat = getChat(proposal2, target2, author, "안녕");
		entityManager.persist(chat);

		List<QueryProposalChatResponse> expected = new ArrayList<>();
		expected.add(new QueryProposalChatResponse(BigInteger.valueOf(proposal2.getId()), proposal2.getContent(),
			BigInteger.valueOf(target2.getId()), target2.getNickname(), "", chat.getContent()));
		expected.add(new QueryProposalChatResponse(BigInteger.valueOf(proposal3.getId()), proposal3.getContent(),
			BigInteger.valueOf(target3.getId()), target3.getNickname(), "", null));
		expected.add(new QueryProposalChatResponse(BigInteger.valueOf(proposal1.getId()), proposal1.getContent(),
			BigInteger.valueOf(target1.getId()), target1.getNickname(), "", null));

		//when
		List<QueryProposalChatResponse> response
			= proposalRepository.findAllProposalByUserId(author.getId());

		//then
		assertThat(response.size(), is(expected.size()));
		for (int i = 0; i < response.size(); i++) {
			QueryProposalChatResponse proposal = response.get(i);
			QueryProposalChatResponse expectedProposal = expected.get(i);
			assertThat(proposal.getId(), is(expectedProposal.getId()));
			assertThat(proposal.getContent(), is(expectedProposal.getContent()));
			assertThat(proposal.getTarget().nickname(), is(expectedProposal.getTarget().nickname()));
			assertThat(proposal.getLastChat().content(), is(expectedProposal.getLastChat().content()));
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

	private static MatchProposal getProposal(Match match, String content, User proposer, Team proposerTeam,
		MatchProposalStatus status) {
		return MatchProposal.builder()
			.match(match)
			.content("덤벼라!")
			.user(proposer)
			.team(proposerTeam)
			.status(APPROVED)
			.build();
	}

	private static MatchChat getChat(MatchProposal proposal, User user, User target, String content) {
		return MatchChat.builder()
			.proposal(proposal)
			.user(user)
			.target(target)
			.content(content)
			.chattedAt(LocalDateTime.now())
			.build();
	}
}