package com.kdt.team04.domain.matches.review.service;

import static com.kdt.team04.domain.matches.review.model.MatchRecordValue.LOSE;
import static com.kdt.team04.domain.matches.review.model.MatchRecordValue.WIN;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import com.amazonaws.services.s3.AmazonS3;
import com.kdt.team04.common.aws.s3.S3Uploader;
import com.kdt.team04.domain.matches.match.model.entity.Match;
import com.kdt.team04.domain.matches.match.model.MatchStatus;
import com.kdt.team04.domain.matches.match.model.MatchType;
import com.kdt.team04.domain.matches.proposal.entity.MatchProposal;
import com.kdt.team04.domain.matches.proposal.entity.MatchProposalStatus;
import com.kdt.team04.domain.matches.review.model.entity.MatchRecord;
import com.kdt.team04.domain.matches.review.repository.MatchRecordRepository;
import com.kdt.team04.domain.teams.team.model.SportsCategory;
import com.kdt.team04.domain.teams.team.model.entity.Team;
import com.kdt.team04.domain.user.entity.User;

@Transactional
@SpringBootTest
class MatchRecordServiceIntegrationTest {

	@Autowired
	MatchRecordService matchRecordService;

	@Autowired
	EntityManager entityManager;

	@Autowired
	MatchRecordRepository matchRecordRepository;

	@MockBean
	S3Uploader s3Uploader;

	@MockBean
	AmazonS3 amazonS3;

	@Test
	@Transactional
	@DisplayName("팀전 경기가 끝난 사용자는 승리한 결과를 등록한다.")
	void endGame_TeamGame() {
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

		//when
		matchRecordService.endGame(match.getId(), matchProposal.getId(), WIN, author.getId());

		//then
		List<MatchRecord> records = entityManager
			.createQuery("SELECT mr FROM MatchRecord mr WHERE mr.match.id = :id", MatchRecord.class)
			.setParameter("id", match.getId())
			.getResultList();

		assertThat(records.isEmpty(), is(false));
		records.forEach(record -> {
			switch (record.getResult()) {
				case WIN -> {
					assertThat(record.getUser().getId(), is(author.getId()));
					assertThat(record.getTeam().getId(), is(authorTeam.getId()));
					assertThat(record.getResult(), is(WIN));
				}
				case LOSE -> {
					assertThat(record.getUser().getId(), is(target.getId()));
					assertThat(record.getTeam().getId(), is(targetTeam.getId()));
					assertThat(record.getResult(), is(LOSE));
				}
			}
		});
	}

	@Test
	@Transactional
	@DisplayName("개인전 경기가 끝난 사용자는 승리한 결과를 등록한다.")
	void endGame_IndividualGame() {
		//given
		User author = getUser("author");
		User target = getUser("target");
		Match match = getSoccerIndividualMatch("축구 하실?", MatchStatus.IN_GAME, author);

		MatchProposal matchProposal = MatchProposal.builder()
			.match(match)
			.content("덤벼라!")
			.user(target)
			.status(MatchProposalStatus.APPROVED)
			.build();

		entityManager.persist(author);
		entityManager.persist(target);
		entityManager.persist(match);
		entityManager.persist(matchProposal);

		//when
		matchRecordService.endGame(match.getId(), matchProposal.getId(), WIN, author.getId());

		//then
		List<MatchRecord> records = matchRecordRepository.findAll();

		assertThat(records.isEmpty(), is(false));
		records.forEach(record -> {
			switch (record.getResult()) {
				case WIN -> {
					assertThat(record.getUser().getId(), is(author.getId()));
					assertThat(record.getTeam(), nullValue());
					assertThat(record.getResult(), is(WIN));
				}
				case LOSE -> {
					assertThat(record.getUser().getId(), is(target.getId()));
					assertThat(record.getTeam(), nullValue());
					assertThat(record.getResult(), is(LOSE));
				}
			}
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