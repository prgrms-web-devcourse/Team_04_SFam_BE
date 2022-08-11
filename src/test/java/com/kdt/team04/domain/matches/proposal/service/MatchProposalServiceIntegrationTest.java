package com.kdt.team04.domain.matches.proposal.service;

import static com.kdt.team04.domain.matches.proposal.entity.MatchProposalStatus.APPROVED;
import static com.kdt.team04.domain.matches.proposal.entity.MatchProposalStatus.WAITING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.samePropertyValuesAs;

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

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import com.amazonaws.services.s3.AmazonS3;
import com.kdt.team04.common.aws.s3.S3Uploader;
import com.kdt.team04.common.exception.BusinessException;
import com.kdt.team04.domain.matches.MatchFactory;
import com.kdt.team04.domain.matches.match.model.MatchStatus;
import com.kdt.team04.domain.matches.match.model.MatchType;
import com.kdt.team04.domain.matches.match.model.entity.Match;
import com.kdt.team04.domain.matches.proposal.dto.request.CreateProposalRequest;
import com.kdt.team04.domain.matches.proposal.dto.response.ChatRoomResponse;
import com.kdt.team04.domain.matches.proposal.dto.response.LastChatResponse;
import com.kdt.team04.domain.matches.proposal.dto.response.ProposalChatResponse;
import com.kdt.team04.domain.matches.proposal.entity.MatchChat;
import com.kdt.team04.domain.matches.proposal.entity.MatchProposal;
import com.kdt.team04.domain.matches.proposal.entity.MatchProposalStatus;
import com.kdt.team04.domain.matches.proposal.repository.MatchProposalRepository;
import com.kdt.team04.domain.teams.team.model.SportsCategory;
import com.kdt.team04.domain.teams.team.model.entity.Team;
import com.kdt.team04.domain.teams.teammember.model.TeamMemberRole;
import com.kdt.team04.domain.teams.teammember.model.entity.TeamMember;
import com.kdt.team04.domain.teams.teammember.repository.TeamMemberRepository;
import com.kdt.team04.domain.user.dto.response.ChatTargetProfileResponse;
import com.kdt.team04.domain.user.entity.User;

@Transactional
@SpringBootTest
class MatchProposalServiceIntegrationTest {

	@Autowired
	EntityManager entityManager;

	@Autowired
	MatchProposalService matchProposalService;

	@Autowired
	MatchProposalRepository matchProposalRepository;

	@MockBean
	S3Uploader s3Uploader;

	@MockBean
	AmazonS3 amazonS3;

	@Test
	@DisplayName("[성공] 개인전 매칭을 신청하고 해당 신청 생성 후 Id 값을 return 한다.")
	void create_individualProposal_success() {
		//given
		User author = MatchFactory.getUser("author");
		User proposer = MatchFactory.getUser("proposer");
		entityManager.persist(author);
		entityManager.persist(proposer);

		Match match = MatchFactory.getIndividualMatchSoccer(MatchStatus.WAITING, author);
		entityManager.persist(match);

		CreateProposalRequest request = new CreateProposalRequest(null, "개인전 신청합니다.");

		//when
		Long createdProposer = matchProposalService.create(proposer.getId(), match.getId(), request);

		//then
		assertThat(createdProposer).isNotNull();
	}

	@Test
	@DisplayName("[성공] 팀전 매칭을 신청하고 해당 신청 생성 후 Id 값을 return 한다.")
	void create_teamProposer_success() {
		//given
		User author = MatchFactory.getUser("author");
		entityManager.persist(author);

		Team authorTeam = MatchFactory.getTeamSoccer("author", author);
		entityManager.persist(authorTeam);

		User proposer = MatchFactory.getUser("proposer");
		User member1 = MatchFactory.getUser("member1");
		User member2 = MatchFactory.getUser("member2");

		entityManager.persist(proposer);
		entityManager.persist(member1);
		entityManager.persist(member2);

		Team proposerTeam = MatchFactory.getTeamSoccer("proposer", proposer);
		List<TeamMember> teamMembers = MatchFactory.getTeamMembers(proposerTeam, proposer, member1, member2);

		entityManager.persist(proposerTeam);
		teamMembers.forEach(teamMember -> entityManager.persist(teamMember));

		Match match = MatchFactory.getTeamMatchSoccer(3, MatchStatus.WAITING, author, authorTeam);
		entityManager.persist(match);

		CreateProposalRequest request = new CreateProposalRequest(proposerTeam.getId(), "팀전 신청합니다.");

		//when
		Long createdProposer = matchProposalService.create(proposer.getId(), match.getId(), request);

		//then
		assertThat(createdProposer).isNotNull();
	}

	@Test
	@DisplayName("[실패] 이미 신청한 사용자가 개인전 매칭에 중복 신청 시, 오류가 발생한다.")
	void create_duplicateIndividualProposal_fail() {
		//given
		User author = MatchFactory.getUser("author");
		User proposer = MatchFactory.getUser("proposer");
		entityManager.persist(author);
		entityManager.persist(proposer);

		Match match = MatchFactory.getIndividualMatchSoccer(MatchStatus.WAITING, author);
		entityManager.persist(match);

		MatchProposal proposal = MatchFactory.getIndividualProposal(match, proposer, WAITING);
		entityManager.persist(proposal);

		CreateProposalRequest request = new CreateProposalRequest(null, "팀전 신청합니다.");

		//when, then
		assertThatThrownBy(() ->
			matchProposalService.create(proposer.getId(), match.getId(), request)
		).isInstanceOf(BusinessException.class);
	}

	@Test
	@DisplayName("[실패] 이미 신청한 사용자가 팀전 매칭에 중복 신청 시, 오류가 발생한다.")
	void create_duplicateTeamProposal_fail() {
		//given
		User author = MatchFactory.getUser("author");
		Team authorTeam = MatchFactory.getTeamSoccer("author", author);
		entityManager.persist(author);
		entityManager.persist(authorTeam);

		User proposer = MatchFactory.getUser("proposer");
		Team proposerTeam = MatchFactory.getTeamSoccer("proposer", proposer);
		User member = MatchFactory.getUser("member");
		entityManager.persist(proposer);
		entityManager.persist(proposerTeam);
		entityManager.persist(member);

		List<TeamMember> teamMembers = MatchFactory.getTeamMembers(proposerTeam, proposer, member);
		teamMembers.forEach(teamMember -> entityManager.persist(teamMember));

		Match match = MatchFactory.getTeamMatchSoccer(2, MatchStatus.WAITING, author, authorTeam);
		entityManager.persist(match);

		MatchProposal proposal = MatchFactory.getTeamProposal(match, proposer, proposerTeam, WAITING);
		entityManager.persist(proposal);

		CreateProposalRequest request = new CreateProposalRequest(proposerTeam.getId(), "팀전 신청합니다.");

		//when, then
		assertThatThrownBy(() ->
			matchProposalService.create(proposer.getId(), match.getId(), request)
		).isInstanceOf(BusinessException.class);
	}

	@Test
	@DisplayName("[실패] 팀전 매칭 신청시 신청자 팀원수보다 매칭 인원이 많으면 예외가 발생한다.")
	void create_teamProposal_notMatchParticipants_fail() {
		//given
		User author = MatchFactory.getUser("author");
		Team authorTeam = MatchFactory.getTeamSoccer("author", author);
		entityManager.persist(author);
		entityManager.persist(authorTeam);

		User proposer = MatchFactory.getUser("proposer");
		Team proposerTeam = MatchFactory.getTeamSoccer("proposer", proposer);
		entityManager.persist(proposer);
		entityManager.persist(proposerTeam);

		Match match = MatchFactory.getTeamMatchSoccer(3, MatchStatus.WAITING, author, authorTeam);
		entityManager.persist(match);

		CreateProposalRequest request = new CreateProposalRequest(proposerTeam.getId(),
			"팀전 신청합니다.");

		//when, then
		assertThatThrownBy(() ->
			matchProposalService.create(proposer.getId(), match.getId(), request)
		).isInstanceOf(BusinessException.class);
	}

	@Test
	@DisplayName("[실패] 팀전 매칭을 신청시 request의 teamId가 null일 경우 예외가 발생한다.")
	void create_teamProposal_proposerTeamIdNull_fail() {
		//given
		User author = MatchFactory.getUser("author");
		User proposer = MatchFactory.getUser("proposer");
		User member = MatchFactory.getUser("member");
		entityManager.persist(author);
		entityManager.persist(proposer);
		entityManager.persist(member);

		Team authorTeam = MatchFactory.getTeamSoccer("author", author);
		Team proposerTeam = MatchFactory.getTeamSoccer("proposer", proposer);
		entityManager.persist(authorTeam);
		entityManager.persist(proposerTeam);

		List<TeamMember> teamMembers = MatchFactory.getTeamMembers(proposerTeam, proposer, member);
		teamMembers.forEach(teamMember -> entityManager.persist(teamMember));

		Match match = MatchFactory.getTeamMatchSoccer(2, MatchStatus.WAITING, author, authorTeam);
		entityManager.persist(match);

		CreateProposalRequest request = new CreateProposalRequest(null, "팀전 신청합니다.");

		//when, then
		assertThatThrownBy(() -> matchProposalService.create(proposer.getId(), match.getId(), request))
			.isInstanceOf(BusinessException.class);
	}

	@Test
	@DisplayName("[실패] 공고 작성자와 신청자의 Id가 같으면 예외가 발생한다.")
	void create_teamProposal_proposerIdEqualsAuthorId_fail() {
		//given
		User author = MatchFactory.getUser("author");
		entityManager.persist(author);

		Match match = MatchFactory.getIndividualMatchSoccer(MatchStatus.WAITING, author);
		entityManager.persist(match);

		CreateProposalRequest request = new CreateProposalRequest(null, "개인전 신청합니다.");

		//when, then
		assertThatThrownBy(() ->
			matchProposalService.create(author.getId(), match.getId(), request)
		).isInstanceOf(BusinessException.class);

	}

	@Test
	@DisplayName("[성공] 매칭 ID로 매칭 신청 목록을 일자 기준(신청일자 or 마지막 채팅 기록 일자) 내림차순 정렬 조회한다.")
	void findAllLastChats_orderByProposalDateOrLastChatDate_success() {
		//given
		User author = MatchFactory.getUser("author");
		Team authorTeam = MatchFactory.getTeamSoccer("author", author);
		entityManager.persist(author);
		entityManager.persist(authorTeam);

		Match match = MatchFactory.getTeamMatchSoccer(2, MatchStatus.WAITING, author, authorTeam);
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

		List<ChatRoomResponse> expected = new ArrayList<>();
		expected.add(new ChatRoomResponse(proposal2.getId(), proposal2.getContent(), new ChatTargetProfileResponse(
			BigInteger.valueOf(target2.getId()), target2.getNickname()), new LastChatResponse(chat.getContent()), proposal2.getCreatedAt()));
		expected.add(new ChatRoomResponse(proposal3.getId(), proposal3.getContent(), new ChatTargetProfileResponse(BigInteger.valueOf(target3.getId()), target3.getNickname()), null, proposal3.getCreatedAt()));
		expected.add(new ChatRoomResponse(proposal1.getId(), proposal1.getContent(), new ChatTargetProfileResponse(BigInteger.valueOf(target1.getId()), target1.getNickname()), null, proposal1.getCreatedAt()));

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
	@DisplayName("[실패] 매칭에 대한 신청이 없다면 매칭 신청 목록 조회 시, 오류가 발생한다.")
	void findAllLastChats_notExistsProposal_fail() {
		User author = MatchFactory.getUser("author");
		User target = MatchFactory.getUser("target");
		entityManager.persist(author);
		entityManager.persist(target);

		Team authorTeam = MatchFactory.getTeamSoccer("author", author);
		Team targetTeam = MatchFactory.getTeamSoccer("target", target);
		entityManager.persist(authorTeam);
		entityManager.persist(targetTeam);

		Match match = MatchFactory.getTeamMatchSoccer(2, MatchStatus.WAITING, author, authorTeam);
		entityManager.persist(match);

		//when, then
		assertThatThrownBy(() -> {
			matchProposalService.findAllProposalChats(match.getId(), author.getId());
		}).isInstanceOf(BusinessException.class);
	}

	@Test
	@DisplayName("[실패] 매칭 작성자가 아니라면 매칭 신청 목록 조회 시, 오류가 발생한다.")
	void findAllProposalChats_notMatchAuthor_fail() {
		//given
		String lastChat = "마지막 채팅";

		User author = MatchFactory.getUser("author");
		User target = MatchFactory.getUser("target");
		entityManager.persist(author);
		entityManager.persist(target);

		Team authorTeam = MatchFactory.getTeamSoccer("author", author);
		Team targetTeam = MatchFactory.getTeamSoccer("target", target);
		entityManager.persist(authorTeam);
		entityManager.persist(targetTeam);

		Match match = MatchFactory.getTeamMatchSoccer(2, MatchStatus.WAITING, author, authorTeam);
		entityManager.persist(match);

		List<MatchProposal> proposals = new ArrayList<>();
		IntStream.range(1, 3)
			.forEach(id -> {
				MatchProposal matchProposal = MatchFactory.getTeamProposal(match, target, targetTeam, APPROVED);
				entityManager.persist(matchProposal);
				proposals.add(matchProposal);
			});

		List<MatchChat> chats = new ArrayList<>();
		proposals.forEach(proposal -> {
			IntStream.range(1, 5)
				.forEach(id -> {
					String content = id == 4 ? lastChat + proposal.getId() : "칫챗";
					MatchChat chat = MatchFactory.getChat(proposal, author, target, content);
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
	@DisplayName("[성공] 매칭 작성자가 매칭 신청을 거절하면 신청 상태가 REFUSE로 변경된다.")
	void react_toRefuse_success() {
		//given
		User author = MatchFactory.getUser("author");
		User proposer = MatchFactory.getUser("proposer");
		entityManager.persist(author);
		entityManager.persist(proposer);

		Match match = MatchFactory.getIndividualMatchSoccer(MatchStatus.WAITING, author);
		entityManager.persist(match);

		MatchProposal proposal = MatchFactory.getIndividualProposal(match, proposer, WAITING);
		MatchProposal savedProposal = matchProposalRepository.save(proposal);

		//when
		MatchProposalStatus react = matchProposalService.react(match.getId(), savedProposal.getId(),
			MatchProposalStatus.REFUSE);

		//then
		assertThat(react).isEqualTo(MatchProposalStatus.REFUSE);
		assertThat(match.getStatus()).isEqualTo(MatchStatus.WAITING);

	}

	@Test
	@DisplayName("[실패] 매칭이 이루어진 후 다른 신청을 수락하면 예외가 발생한다.")
	void react_alreadyMatched_fail() {
		//given
		User author = MatchFactory.getUser("author");
		User proposer = MatchFactory.getUser("proposer");
		entityManager.persist(author);
		entityManager.persist(proposer);

		Match match = MatchFactory.getIndividualMatchSoccer(MatchStatus.IN_GAME, author);
		entityManager.persist(match);

		MatchProposal proposal = MatchFactory.getIndividualProposal(match, proposer, WAITING);
		MatchProposal savedProposal = matchProposalRepository.save(proposal);

		//when
		assertThatThrownBy(() -> matchProposalService.react(match.getId(), savedProposal.getId(),
			MatchProposalStatus.APPROVED)).isInstanceOf(BusinessException.class);

	}

	@Test
	@DisplayName("[성공] 매칭 공고의 모든 매칭 신청이 삭제된다.")
	void deleteByMatches_success() {
		//given
		User author = MatchFactory.getUser("author");
		User proposer = MatchFactory.getUser("proposer");
		entityManager.persist(author);
		entityManager.persist(proposer);

		Match match = MatchFactory.getIndividualMatchSoccer(MatchStatus.WAITING, author);
		entityManager.persist(match);

		MatchProposal proposal = MatchFactory.getIndividualProposal(match, proposer, WAITING);
		entityManager.persist(proposal);

		//when
		matchProposalService.deleteByMatches(match.getId());

		//then
		Optional<MatchProposal> deletedProposal = matchProposalRepository.findById(proposal.getId());
		assertThat(deletedProposal).isEmpty();
	}

	@Test
	@DisplayName("[성공] 매칭 공고글 작성자가 매칭 신청 정보를 조회한다.(By 신청 ID, 사용자 ID)")
	void findById_andAuthorId_success() {
		//given
		User author = MatchFactory.getUser("author");
		User proposer = MatchFactory.getUser("proposer");
		entityManager.persist(author);
		entityManager.persist(proposer);

		Match match = MatchFactory.getIndividualMatchSoccer(MatchStatus.WAITING, author);
		entityManager.persist(match);

		MatchProposal proposal = MatchFactory.getIndividualProposal(match, proposer, WAITING);
		entityManager.persist(proposal);

		ProposalChatResponse expected = new ProposalChatResponse(
			proposal.getId(),
			proposal.getStatus(),
			proposal.getContent(),
			true
		);

		//when
		ProposalChatResponse response = matchProposalService.findById(proposal.getId(), author.getId());

		//then
		MatcherAssert.assertThat(response, samePropertyValuesAs(expected));
	}

	@Test
	@DisplayName("[성공] 신청자가 매칭 신청 정보를 조회한다.(By 신청 ID, 사용자 ID)")
	void findById_andProposerId_success() {
		//given
		User author = MatchFactory.getUser("author");
		User proposer = MatchFactory.getUser("proposer");
		entityManager.persist(author);
		entityManager.persist(proposer);

		Match match = MatchFactory.getIndividualMatchSoccer(MatchStatus.WAITING, author);
		entityManager.persist(match);

		MatchProposal proposal = MatchFactory.getIndividualProposal(match, proposer, WAITING);
		entityManager.persist(proposal);

		ProposalChatResponse expected = new ProposalChatResponse(
			proposal.getId(),
			proposal.getStatus(),
			proposal.getContent(),
			false
		);

		//when
		ProposalChatResponse response = matchProposalService.findById(proposal.getId(), proposer.getId());

		//then
		MatcherAssert.assertThat(response, samePropertyValuesAs(expected));
	}

	@Test
	@DisplayName("[실패] 존재하지 않는 신청 ID로 매칭 신청 정보를 조회 시, 오류가 발생한다.")
	void findById_notFound_fail() {
		//given
		User author = MatchFactory.getUser("author");
		User proposer = MatchFactory.getUser("proposer");
		entityManager.persist(author);
		entityManager.persist(proposer);

		Match match = MatchFactory.getIndividualMatchSoccer(MatchStatus.WAITING, author);
		entityManager.persist(match);

		MatchProposal proposal = MatchFactory.getIndividualProposal(match, proposer, WAITING);
		entityManager.persist(proposal);

		Long invalidProposalId = 999L;

		//when, then
		assertThatThrownBy(() -> {
			matchProposalService.findById(invalidProposalId, author.getId());
		}).isInstanceOf(BusinessException.class);
	}

	@Test
	@DisplayName("[실패] 작성자도 신청자도 아닌 사용자가 매칭 신청 정보를 조회 시, 오류가 발생한다.")
	void findById_notPermission_fail() {
		//given
		User author = MatchFactory.getUser("author");
		User proposer = MatchFactory.getUser("proposer");
		User anonymous = MatchFactory.getUser("anonymous");
		entityManager.persist(author);
		entityManager.persist(proposer);
		entityManager.persist(anonymous);

		Match match = MatchFactory.getIndividualMatchSoccer(MatchStatus.WAITING, author);
		entityManager.persist(match);

		MatchProposal proposal = MatchFactory.getIndividualProposal(match, proposer, WAITING);
		entityManager.persist(proposal);

		//when, then
		assertThatThrownBy(() -> {
			matchProposalService.findById(proposal.getId(), anonymous.getId());
		}).isInstanceOf(BusinessException.class);
	}
}