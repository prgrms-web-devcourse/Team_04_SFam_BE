package com.kdt.team04.domain.matches.proposal.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.kdt.team04.common.exception.BusinessException;
import com.kdt.team04.domain.matches.match.entity.Match;
import com.kdt.team04.domain.matches.match.entity.MatchStatus;
import com.kdt.team04.domain.matches.match.entity.MatchType;
import com.kdt.team04.domain.matches.proposal.dto.MatchProposalRequest;
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

		entityManager.persist(author);
		entityManager.persist(proposer);

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

		entityManager.persist(author);
		entityManager.persist(proposer);

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
}