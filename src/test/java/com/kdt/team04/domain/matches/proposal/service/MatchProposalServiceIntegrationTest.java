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
import com.kdt.team04.domain.matches.proposal.entity.MatchProposal;
import com.kdt.team04.domain.matches.proposal.entity.MatchProposalStatus;
import com.kdt.team04.domain.matches.proposal.repository.MatchProposalRepository;
import com.kdt.team04.domain.team.SportsCategory;
import com.kdt.team04.domain.team.entity.Team;
import com.kdt.team04.domain.teammember.entity.TeamMember;
import com.kdt.team04.domain.teammember.entity.TeamMemberRole;
import com.kdt.team04.domain.user.entity.Location;
import com.kdt.team04.domain.user.entity.User;

@Transactional
@SpringBootTest
class MatchProposalServiceIntegrationTest {

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private MatchProposalService matchProposalService;

	@Autowired
	private MatchProposalRepository matchProposalRepository;

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
		Team authorTeam = Team.builder()
			.name("team1")
			.description("first team")
			.sportsCategory(SportsCategory.BADMINTON)
			.leader(author)
			.build();
		entityManager.persist(authorTeam);
		entityManager.persist(author);

		User proposer = new User("proposer", "proposer", "aA1234!");
		User user1 = new User("member1", "member1", "password");
		User user2 = new User("member2", "member2", "password");
		entityManager.persist(proposer);
		entityManager.persist(user1);
		entityManager.persist(user2);
		proposer.updateLocation(new Location(1.1, 1.2));
		Team proposerTeam = Team.builder()
			.name("team1")
			.description("first team")
			.sportsCategory(SportsCategory.BADMINTON)
			.leader(proposer)
			.build();
		entityManager.persist(proposerTeam);
		entityManager.persist(new TeamMember(proposerTeam, user1, TeamMemberRole.LEADER));
		entityManager.persist(new TeamMember(proposerTeam, user1, TeamMemberRole.MEMBER));
		entityManager.persist(new TeamMember(proposerTeam, user2, TeamMemberRole.MEMBER));

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
	@DisplayName("팀전 매칭 신청시 신청자 팀원수보다 매칭 인원이 많으면 예외가 발생한다.")
	void testTeamProposerCreateFailByTeamMember() {
		//given
		User author = new User("author", "author", "aA1234!");
		Team authorTeam = Team.builder()
			.name("team1")
			.description("first team")
			.sportsCategory(SportsCategory.BADMINTON)
			.leader(author)
			.build();
		entityManager.persist(authorTeam);
		entityManager.persist(author);

		User proposer = new User("proposer", "proposer", "aA1234!");
		entityManager.persist(proposer);
		proposer.updateLocation(new Location(1.1, 1.2));
		Team proposerTeam = Team.builder()
			.name("team1")
			.description("first team")
			.sportsCategory(SportsCategory.BADMINTON)
			.leader(proposer)
			.build();
		entityManager.persist(proposerTeam);

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

		//when, then
		assertThatThrownBy(() -> matchProposalService.create(proposer.getId(), match.getId(), request)).isInstanceOf(
			BusinessException.class);
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
	@DisplayName("매칭 작성자가 매칭 신청을 거절하면 신청 상태가 REFUSE로 변경된다.")
	void testRefuseReactSuccess() {
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
		MatchProposal proposal = MatchProposal.builder()
			.user(proposer)
			.team(null)
			.match(match)
			.content("content")
			.status(MatchProposalStatus.WAITING)
			.build();
		MatchProposal savedProposal = matchProposalRepository.save(proposal);

		//when
		MatchProposalStatus react = matchProposalService.react(match.getId(), savedProposal.getId(),
			MatchProposalStatus.REFUSE);

		//then
		assertThat(react).isEqualTo(MatchProposalStatus.REFUSE);
		assertThat(match.getStatus()).isEqualTo(MatchStatus.WAITING);

	}

	@Test
	@DisplayName("매칭이 이루어진 후 다른 신청을 수락하면 예외가 발생한다.")
	void testAlreadyMatchedApproveReactFail() {
		//given
		User author = new User("author", "author", "aA1234!");
		User proposer = new User("proposer", "proposer", "aA1234!");
		entityManager.persist(author);
		entityManager.persist(proposer);

		Match match = Match.builder()
			.title("match")
			.status(MatchStatus.IN_GAME)
			.matchDate(LocalDate.now())
			.matchType(MatchType.INDIVIDUAL_MATCH)
			.participants(1)
			.user(author)
			.sportsCategory(SportsCategory.BADMINTON)
			.content("content")
			.build();
		entityManager.persist(match);
		MatchProposal proposal = MatchProposal.builder()
			.user(proposer)
			.team(null)
			.match(match)
			.content("content")
			.status(MatchProposalStatus.WAITING)
			.build();
		MatchProposal savedProposal = matchProposalRepository.save(proposal);

		//when
		assertThatThrownBy(() -> matchProposalService.react(match.getId(), savedProposal.getId(),
			MatchProposalStatus.APPROVED)).isInstanceOf(BusinessException.class);

	}
}