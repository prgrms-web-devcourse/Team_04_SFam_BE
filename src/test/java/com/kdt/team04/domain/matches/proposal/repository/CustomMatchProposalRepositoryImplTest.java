package com.kdt.team04.domain.matches.proposal.repository;

import static com.kdt.team04.domain.matches.match.model.MatchStatus.END;
import static com.kdt.team04.domain.matches.proposal.entity.MatchProposalStatus.APPROVED;
import static com.kdt.team04.domain.matches.proposal.entity.MatchProposalStatus.FIXED;
import static com.kdt.team04.domain.matches.review.model.MatchRecordValue.WIN;
import static com.kdt.team04.domain.matches.review.model.MatchReviewValue.BEST;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.samePropertyValuesAs;

import java.math.BigInteger;
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
import com.kdt.team04.domain.matches.MatchFactory;
import com.kdt.team04.domain.matches.match.model.MatchStatus;
import com.kdt.team04.domain.matches.match.model.entity.Match;
import com.kdt.team04.domain.matches.proposal.dto.QueryProposalChatResponse;
import com.kdt.team04.domain.matches.proposal.entity.MatchChat;
import com.kdt.team04.domain.matches.proposal.entity.MatchProposal;
import com.kdt.team04.domain.matches.review.model.MatchRecordValue;
import com.kdt.team04.domain.matches.review.model.MatchReviewValue;
import com.kdt.team04.domain.matches.review.model.entity.MatchRecord;
import com.kdt.team04.domain.matches.review.model.entity.MatchReview;
import com.kdt.team04.domain.teams.team.model.entity.Team;
import com.kdt.team04.domain.user.entity.User;

@Transactional
@SpringBootTest
class CustomMatchProposalRepositoryImplTest {

	@PersistenceContext
	EntityManager entityManager;

	@Autowired
	MatchProposalRepository proposalRepository;

	@MockBean
	S3Uploader s3Uploader;

	@MockBean
	AmazonS3 amazonS3;

	@Test
	@DisplayName("[성공] 회원 Id 기준으로 신청(채팅방) 목록을 일자 기준(신청일자 or 마지막 채팅 기록 일자) 내림차순 정렬 조회한다.")
	void findAllProposalByUserId_orderByProposalDateOrLastChatDate_success() {
		//given
		User author = MatchFactory.getUser("author");
		Team authorTeam = MatchFactory.getTeamSoccer("author", author);
		entityManager.persist(author);
		entityManager.persist(authorTeam);

		Match match = MatchFactory.getTeamMatchSoccer(3, MatchStatus.WAITING, author, authorTeam);
		entityManager.persist(match);

		User target1 = MatchFactory.getUser("target1");
		Team targetTeam1 = MatchFactory.getTeamSoccer("target1", target1);
		MatchProposal proposal1 = MatchFactory.getTeamProposal(match, target1, targetTeam1, APPROVED);
		entityManager.persist(target1);
		entityManager.persist(targetTeam1);
		entityManager.persist(proposal1);

		User target2 = MatchFactory.getUser("target2");
		Team targetTeam2 = MatchFactory.getTeamSoccer("target2", target2);
		MatchProposal proposal2 = MatchFactory.getTeamProposal(match, target2, targetTeam2, APPROVED);
		entityManager.persist(target2);
		entityManager.persist(targetTeam2);
		entityManager.persist(proposal2);

		User target3 = MatchFactory.getUser("target3");
		Team targetTeam3 = MatchFactory.getTeamSoccer("target3", target3);
		MatchProposal proposal3 = MatchFactory.getTeamProposal(match, target3, targetTeam3, APPROVED);
		entityManager.persist(target3);
		entityManager.persist(targetTeam3);
		entityManager.persist(proposal3);

		MatchChat chat = MatchFactory.getChat(proposal2, target2, author, "안녕");
		entityManager.persist(chat);

		List<QueryProposalChatResponse> expected = new ArrayList<>();
		expected.add(new QueryProposalChatResponse(BigInteger.valueOf(proposal2.getId()), proposal2.getContent(),
			BigInteger.valueOf(target2.getId()), target2.getNickname(), "", chat.getContent(), BigInteger.valueOf(match.getId())));
		expected.add(new QueryProposalChatResponse(BigInteger.valueOf(proposal3.getId()), proposal3.getContent(),
			BigInteger.valueOf(target3.getId()), target3.getNickname(), "", null, BigInteger.valueOf(match.getId())));
		expected.add(new QueryProposalChatResponse(BigInteger.valueOf(proposal1.getId()), proposal1.getContent(),
			BigInteger.valueOf(target1.getId()), target1.getNickname(), "", null, BigInteger.valueOf(match.getId())));

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
			assertThat(proposal.getMatchId(), is(match.getId()));
		}
	}

	@Test
	@DisplayName("[성공] 경기가 종료되었다면, 확정된 상대만 조회된다.")
	void findAllProposalByUserId_fixedProposer_success() {
		//given
		User author = MatchFactory.getUser("author");
		Team authorTeam = MatchFactory.getTeamSoccer("author", author);
		entityManager.persist(author);
		entityManager.persist(authorTeam);

		Match match = MatchFactory.getTeamMatchSoccer(3, END, author, authorTeam);
		entityManager.persist(match);

		User target1 = MatchFactory.getUser("target1");
		Team targetTeam1 = MatchFactory.getTeamSoccer("target1", target1);
		MatchProposal proposal1 = MatchFactory.getTeamProposal(match, target1, targetTeam1, FIXED);
		entityManager.persist(target1);
		entityManager.persist(targetTeam1);
		entityManager.persist(proposal1);

		User target2 = MatchFactory.getUser("target2");
		Team targetTeam2 = MatchFactory.getTeamSoccer("target2", target2);
		MatchProposal proposal2 = MatchFactory.getTeamProposal(match, target2, targetTeam2, APPROVED);
		entityManager.persist(target2);
		entityManager.persist(targetTeam2);
		entityManager.persist(proposal2);

		User target3 = MatchFactory.getUser("target3");
		Team targetTeam3 = MatchFactory.getTeamSoccer("target3", target3);
		MatchProposal proposal3 = MatchFactory.getTeamProposal(match, target3, targetTeam3, APPROVED);
		entityManager.persist(target3);
		entityManager.persist(targetTeam3);
		entityManager.persist(proposal3);

		MatchRecord record = MatchFactory.getRecord(match, author, authorTeam, WIN);
		entityManager.persist(record);

		QueryProposalChatResponse fixedProposer = new QueryProposalChatResponse(
			BigInteger.valueOf(proposal1.getId()),
			proposal1.getContent(),
			BigInteger.valueOf(target1.getId()),
			target1.getNickname(),
			null,
			null,
			BigInteger.valueOf(match.getId())
		);

		//when
		List<QueryProposalChatResponse> response
			= proposalRepository.findAllProposalByUserId(author.getId());

		//then
		assertThat(response.size(), is(1));
		assertThat(response.get(0), samePropertyValuesAs(fixedProposer));
	}

	@Test
	@DisplayName("[성공] 경기가 종료되고 후가까지 작성한 사용자에겐 조회되지 않는다.")
	void findAllProposalByUserId_ExistsReviewThenNotSearch_success() {
		//given
		User author = MatchFactory.getUser("author");
		Team authorTeam = MatchFactory.getTeamSoccer("author", author);
		entityManager.persist(author);
		entityManager.persist(authorTeam);

		Match match = MatchFactory.getTeamMatchSoccer(3, END, author, authorTeam);
		entityManager.persist(match);

		User target1 = MatchFactory.getUser("target1");
		Team targetTeam1 = MatchFactory.getTeamSoccer("target1", target1);
		MatchProposal proposal1 = MatchFactory.getTeamProposal(match, target1, targetTeam1, FIXED);
		entityManager.persist(target1);
		entityManager.persist(targetTeam1);
		entityManager.persist(proposal1);

		User target2 = MatchFactory.getUser("target2");
		Team targetTeam2 = MatchFactory.getTeamSoccer("target2", target2);
		MatchProposal proposal2 = MatchFactory.getTeamProposal(match, target2, targetTeam2, APPROVED);
		entityManager.persist(target2);
		entityManager.persist(targetTeam2);
		entityManager.persist(proposal2);

		User target3 = MatchFactory.getUser("target3");
		Team targetTeam3 = MatchFactory.getTeamSoccer("target3", target3);
		MatchProposal proposal3 = MatchFactory.getTeamProposal(match, target3, targetTeam3, APPROVED);
		entityManager.persist(target3);
		entityManager.persist(targetTeam3);
		entityManager.persist(proposal3);

		MatchRecord record = MatchFactory.getRecord(match, author, authorTeam, WIN);
		entityManager.persist(record);

		MatchReview review = MatchFactory.getReview(match, BEST, author, authorTeam, target1, targetTeam1);
		entityManager.persist(review);

		//when
		List<QueryProposalChatResponse> response
			= proposalRepository.findAllProposalByUserId(author.getId());

		//then
		assertThat(response.isEmpty(), is(true));
	}
}