package com.kdt.team04.domain.matches.proposal.service;

import static com.kdt.team04.domain.matches.proposal.entity.MatchProposalStatus.APPROVED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import com.amazonaws.services.s3.AmazonS3;
import com.kdt.team04.common.aws.s3.S3Uploader;
import com.kdt.team04.common.exception.BusinessException;
import com.kdt.team04.domain.matches.match.model.MatchStatus;
import com.kdt.team04.domain.matches.match.model.MatchType;
import com.kdt.team04.domain.matches.match.model.entity.Match;
import com.kdt.team04.domain.matches.proposal.dto.request.CreateProposalRequest;
import com.kdt.team04.domain.matches.proposal.dto.response.ChatRoomResponse;
import com.kdt.team04.domain.matches.proposal.dto.response.LastChatResponse;
import com.kdt.team04.domain.matches.proposal.entity.MatchChat;
import com.kdt.team04.domain.matches.proposal.entity.MatchProposal;
import com.kdt.team04.domain.matches.proposal.entity.MatchProposalStatus;
import com.kdt.team04.domain.matches.proposal.repository.MatchProposalRepository;
import com.kdt.team04.domain.teams.team.model.SportsCategory;
import com.kdt.team04.domain.teams.team.model.entity.Team;
import com.kdt.team04.domain.teams.teammember.model.TeamMemberRole;
import com.kdt.team04.domain.teams.teammember.model.entity.TeamMember;
import com.kdt.team04.domain.user.dto.response.ChatTargetProfileResponse;
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

	@MockBean
	S3Uploader s3Uploader;

	@MockBean
	AmazonS3 amazonS3;

	@Test
	@DisplayName("개인전 매칭을 신청하고 해당 신청 생성 후 Id 값을 return 한다.")
	void testIndividualCreateSuccess() {
		//given
		User author = new User("author", "author", "aA1234!");
		author.updateSettings(127.1554531, 37.6139816, 40);
		User proposer = new User("proposer", "proposer", "aA1234!");
		proposer.updateSettings(127.1554531, 37.6139816, 40);

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
			.location(author.getUserSettings().getLocation())
			.build();
		entityManager.persist(match);
		CreateProposalRequest request = new CreateProposalRequest(null, "개인전 신청합니다.");

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
		author.updateSettings(127.1554531, 37.6139816, 40);

		entityManager.persist(author);

		Team authorTeam = Team.builder()
			.name("author")
			.description("first team")
			.sportsCategory(SportsCategory.BADMINTON)
			.leader(author)
			.build();

		entityManager.persist(authorTeam);

		User proposer = new User("proposer", "proposer", "aA1234!");
		proposer.updateSettings(127.1554531, 37.6139816, 40);
		User user1 = new User("member1", "member1", "password");
		User user2 = new User("member2", "member2", "password");

		entityManager.persist(proposer);
		entityManager.persist(user1);
		entityManager.persist(user2);

		Team proposerTeam = Team.builder()
			.name("proposer")
			.description("proposer team")
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
			.location(author.getUserSettings().getLocation())
			.build();

		entityManager.persist(match);

		CreateProposalRequest request = new CreateProposalRequest(proposerTeam.getId(),
			"팀전 신청합니다.");

		//when
		Long createdProposer = matchProposalService.create(proposer.getId(), match.getId(), request);

		//then
		assertThat(createdProposer).isNotNull();
	}

	@Test
	@DisplayName("이미 신청한 사용자가 개인전 매칭에 중복 신청 시, 오류가 발생한다.")
	void create_DuplicateIndividualProposal_Fail() {
		//given
		User author = new User("author", "author", "aA1234!");

		entityManager.persist(author);

		User proposer = new User("proposer", "proposer", "aA1234!");
		entityManager.persist(proposer);

		Match match = Match.builder()
			.title("match")
			.status(MatchStatus.WAITING)
			.matchDate(LocalDate.now())
			.matchType(MatchType.INDIVIDUAL_MATCH)
			.participants(1)
			.user(author)
			.team(null)
			.sportsCategory(SportsCategory.BADMINTON)
			.content("content")
			.build();

		entityManager.persist(match);

		MatchProposal proposal = MatchProposal.builder()
			.match(match)
			.content("덤벼라!")
			.user(proposer)
			.team(null)
			.status(MatchProposalStatus.WAITING)
			.build();

		entityManager.persist(proposal);

		CreateProposalRequest request = new CreateProposalRequest(null, "팀전 신청합니다.");

		//when, then
		assertThatThrownBy(() -> matchProposalService.create(proposer.getId(), match.getId(), request))
			.isInstanceOf(BusinessException.class);
	}

	@Test
	@DisplayName("이미 신청한 사용자가 팀전 매칭에 중복 신청 시, 오류가 발생한다.")
	void create_DuplicateTeamProposal__Fail() {
		//given
		User author = new User("author", "author", "aA1234!");
		Team authorTeam = Team.builder()
			.name("author")
			.description("author team")
			.sportsCategory(SportsCategory.BADMINTON)
			.leader(author)
			.build();

		entityManager.persist(author);
		entityManager.persist(authorTeam);

		User proposer = new User("proposer", "proposer", "aA1234!");
		User proposerPair = new User("proposer_pair", "proposer_pair", "aA1234!");
		Team proposerTeam = Team.builder()
			.name("proposer")
			.description("proposer team")
			.sportsCategory(SportsCategory.BADMINTON)
			.leader(proposer)
			.build();

		entityManager.persist(proposer);
		entityManager.persist(proposerPair);
		entityManager.persist(proposerTeam);
		entityManager.persist(new TeamMember(proposerTeam, proposer, TeamMemberRole.LEADER));
		entityManager.persist(new TeamMember(proposerTeam, proposerPair, TeamMemberRole.MEMBER));

		Match match = Match.builder()
			.title("match")
			.status(MatchStatus.WAITING)
			.matchDate(LocalDate.now())
			.matchType(MatchType.TEAM_MATCH)
			.participants(2)
			.user(author)
			.team(authorTeam)
			.sportsCategory(SportsCategory.BADMINTON)
			.content("content")
			.build();

		entityManager.persist(match);

		MatchProposal proposal = MatchProposal.builder()
			.match(match)
			.content("덤벼라!")
			.user(proposer)
			.team(proposerTeam)
			.status(MatchProposalStatus.WAITING)
			.build();

		entityManager.persist(proposal);

		CreateProposalRequest request = new CreateProposalRequest(proposerTeam.getId(), "팀전 신청합니다.");

		//when, then
		assertThatThrownBy(() -> matchProposalService.create(proposer.getId(), match.getId(), request))
			.isInstanceOf(BusinessException.class);
	}

	@Test
	@DisplayName("팀전 매칭 신청시 신청자 팀원수보다 매칭 인원이 많으면 예외가 발생한다.")
	void testTeamProposerCreateFailByTeamMember() {
		//given
		User author = new User("author", "author", "aA1234!");
		author.updateSettings(127.1554531, 37.6139816, 40);

		entityManager.persist(author);

		Team authorTeam = Team.builder()
			.name("author")
			.description("first team")
			.sportsCategory(SportsCategory.BADMINTON)
			.leader(author)
			.build();

		entityManager.persist(authorTeam);
		entityManager.persist(author);

		User proposer = new User("proposer", "proposer", "aA1234!");

		entityManager.persist(proposer);

		proposer.updateSettings(1.1, 1.2, 10);
		Team proposerTeam = Team.builder()
			.name("proposer")
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
			.location(author.getUserSettings().getLocation())
			.build();

		entityManager.persist(match);

		CreateProposalRequest request = new CreateProposalRequest(proposerTeam.getId(),
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
		author.updateSettings(127.1554531, 37.6139816, 40);
		User proposer = new User("proposer", "proposer", "aA1234!");
		proposer.updateSettings(127.1554531, 37.6139816, 40);

		entityManager.persist(author);
		entityManager.persist(proposer);

		Team authorTeam = Team.builder()
			.name("author")
			.description("first team")
			.sportsCategory(SportsCategory.BADMINTON)
			.leader(proposer)
			.build();
		Team proposerTeam = Team.builder()
			.name("proposer")
			.description("first team")
			.sportsCategory(SportsCategory.BADMINTON)
			.leader(proposer)
			.build();

		entityManager.persist(authorTeam);
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
			.location(author.getUserSettings().getLocation())
			.build();
		entityManager.persist(match);
		CreateProposalRequest request = new CreateProposalRequest(null, "팀전 신청합니다.");

		//when, then
		assertThatThrownBy(() -> matchProposalService.create(proposer.getId(), match.getId(), request))
			.isInstanceOf(BusinessException.class);
	}

	@Test
	@DisplayName("공고 작성자와 신청자의 Id가 같으면 예외가 발생한다.")
	void TeamProposerCreateFail() {
		//given
		User author = new User("author", "author", "aA1234!");
		entityManager.persist(author);

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
		CreateProposalRequest request = new CreateProposalRequest(null, "개인전 신청합니다.");

		//when, then
		assertThatThrownBy(() -> matchProposalService.create(author.getId(), match.getId(), request)).isInstanceOf(
			BusinessException.class);

	}

	@Test
	@DisplayName("매칭 ID로 매칭 신청 목록을 일자 기준(신청일자 or 마지막 채팅 기록 일자) 내림차순 정렬 조회한다.")
	void findAllLastChats_OrderByProposalDateOrLastChatDate() {
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

		List<ChatRoomResponse> expected = new ArrayList<>();
		// TODO match null 들어가는 부분 채워야 됨!! 2022-08-12 midas
		expected.add(new ChatRoomResponse(proposal2.getId(), proposal2.getContent(), new ChatTargetProfileResponse(
			BigInteger.valueOf(target2.getId()), target2.getNickname(), ""), new LastChatResponse(chat.getContent()), null,
			proposal2.getCreatedAt()));
		expected.add(new ChatRoomResponse(proposal3.getId(), proposal3.getContent(),
			new ChatTargetProfileResponse(BigInteger.valueOf(target3.getId()), target3.getNickname(), ""), null, null,
			proposal3.getCreatedAt()));
		expected.add(new ChatRoomResponse(proposal1.getId(), proposal1.getContent(),
			new ChatTargetProfileResponse(BigInteger.valueOf(target1.getId()), target1.getNickname(), ""), null, null,
			proposal1.getCreatedAt()));

		//when
		matchProposalService.findAllProposalChats(match.getId(), author.getId());
		List<ChatRoomResponse> response = matchProposalService.findAllProposalChats(match.getId(), author.getId());

		//then
		assertThat(response).hasSize(expected.size());
		for (int i = 0; i < response.size(); i++) {
			ChatRoomResponse proposal = response.get(i);
			ChatRoomResponse expectedProposal = expected.get(i);
			assertThat(proposal.id()).isEqualTo(expectedProposal.id());
			assertThat(proposal.content()).isEqualTo(expectedProposal.content());
			assertThat(proposal.target().nickname()).isEqualTo(expectedProposal.target().nickname());
			if (proposal.lastChat() != null) {
				assertThat(proposal.lastChat().content()).isEqualTo(expectedProposal.lastChat().content());
			}
		}
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
			matchProposalService.findAllProposalChats(match.getId(), author.getId());
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
			matchProposalService.findAllProposalChats(match.getId(), invalidUserId);
		}).isInstanceOf(BusinessException.class);
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
		MatchProposalStatus react = matchProposalService.approveOrRefuse(author.getId(), match.getId(), savedProposal.getId(),
			MatchProposalStatus.REFUSE);

		//then
		assertThat(react).isEqualTo(MatchProposalStatus.REFUSE);
		assertThat(match.getStatus()).isEqualTo(MatchStatus.WAITING);

	}

	@Test
	@DisplayName("매칭 종료된 후 다른 신청을 수락하면 예외가 발생한다.")
	void testAlreadyMatchedApproveReactFail() {
		//given
		User author = new User("author", "author", "aA1234!");
		User proposer = new User("proposer", "proposer", "aA1234!");
		entityManager.persist(author);
		entityManager.persist(proposer);

		Match match = Match.builder()
			.title("match")
			.status(MatchStatus.END)
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
		assertThatThrownBy(() -> matchProposalService.approveOrRefuse(author.getId(), match.getId(), savedProposal.getId(),
			MatchProposalStatus.APPROVED)).isInstanceOf(BusinessException.class);
	}

	// TODO react() - to change Fixed
	// TODO react() - to change Waiting
	// TODO react() - already approved or refuse

	@Test
	@DisplayName("다른 사용자가 공고 신청상태 변경 시 예외가 발생한다.")
	void notAuthentication() {
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

		entityManager.flush();
		entityManager.clear();

		//when
		assertThatThrownBy(() -> matchProposalService.approveOrRefuse(-999L, match.getId(), savedProposal.getId(),
			MatchProposalStatus.APPROVED)).isInstanceOf(BusinessException.class);
	}

	@Test
	@DisplayName("매칭 공고의 모든 매칭 신청이 삭제된다.")
	void testDeleteByMatchesSuccess() {
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
		entityManager.persist(proposal);

		//when
		matchProposalService.deleteByMatches(match.getId());

		//then
		Optional<MatchProposal> deletedProposal = matchProposalRepository.findById(proposal.getId());
		assertThat(deletedProposal).isEmpty();
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