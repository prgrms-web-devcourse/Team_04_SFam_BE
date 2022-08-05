package com.kdt.team04.domain.team.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import com.kdt.team04.common.exception.EntityNotFoundException;
import com.kdt.team04.domain.matches.review.dto.MatchRecordResponse;
import com.kdt.team04.domain.matches.review.dto.MatchReviewResponse;
import com.kdt.team04.domain.matches.review.service.MatchRecordGiverService;
import com.kdt.team04.domain.matches.review.service.MatchReviewGiverService;
import com.kdt.team04.domain.team.SportsCategory;
import com.kdt.team04.domain.team.dto.TeamConverter;
import com.kdt.team04.domain.team.dto.TeamRequest;
import com.kdt.team04.domain.team.dto.TeamResponse;
import com.kdt.team04.domain.team.entity.Team;
import com.kdt.team04.domain.team.repository.TeamRepository;
import com.kdt.team04.domain.teammember.service.TeamMemberGiverService;
import com.kdt.team04.domain.user.Role;
import com.kdt.team04.domain.user.UserConverter;
import com.kdt.team04.domain.user.dto.UserResponse;
import com.kdt.team04.domain.user.entity.User;
import com.kdt.team04.domain.user.service.UserService;

@Transactional
@ExtendWith(MockitoExtension.class)
class TeamServiceTest {
	@InjectMocks
	private TeamService teamService;

	@Mock
	private UserService userService;

	@Mock
	private TeamConverter teamConverter;

	@Mock
	private UserConverter userConverter;

	@Mock
	private TeamRepository teamRepository;

	@Mock
	private TeamMemberGiverService teamMemberGiver;

	@Mock
	private MatchRecordGiverService matchRecordGiver;

	@Mock
	private MatchReviewGiverService matchReviewGiver;

	private final User USER = new User(1L, "password", "username", "nickname", null, null);
	private final UserResponse USER_RESPONSE = new UserResponse(USER.getId(), USER.getUsername(), USER.getPassword(),
		USER.getNickname(), null, null);
	private final TeamRequest.CreateRequest CREATE_REQUEST = new TeamRequest.CreateRequest("team1", "first team",
		SportsCategory.BADMINTON);
	private final Team TEAM = new Team(10L, CREATE_REQUEST.name(), CREATE_REQUEST.description(),
		CREATE_REQUEST.sportsCategory(), USER);
	private final MatchRecordResponse.TotalCount RECORD = new MatchRecordResponse.TotalCount(1, 1, 1);
	private final MatchReviewResponse.TotalCount REVIEW = new MatchReviewResponse.TotalCount(1, 1, 1);
	private final TeamResponse RESPONSE = new TeamResponse(TEAM.getId(), TEAM.getName(), TEAM.getDescription(),
		TEAM.getSportsCategory(),
		Collections.emptyList(), RECORD, REVIEW, USER_RESPONSE);

	@Test
	@DisplayName("팀 생성에 성공합니다.")
	void testCreateSuccess() {
		//given
		User user = getDemoUser();
		UserResponse userResponse = toUserResponse(user);
		Team newTeam = Team.builder()
			.id(1L)
			.name("team1")
			.description("first team")
			.sportsCategory(SportsCategory.BADMINTON)
			.build();
		TeamRequest.CreateRequest createRequest = new TeamRequest.CreateRequest("team1", "first team",
			SportsCategory.BADMINTON);

		given(userService.findById(any(Long.class))).willReturn(userResponse);
		given(userConverter.toUser(userResponse)).willReturn(user);
		given(teamRepository.save(any(Team.class))).willReturn(newTeam);

		//when
		Long teamId = teamService.create(user.getId(), createRequest);

		//then
		verify(userService, times(1)).findById(user.getId());
		verify(teamRepository, times(1)).save(any(Team.class));

		assertThat(teamId).isEqualTo(newTeam.getId());
	}

	@Test
	@DisplayName("팀 ID로 해당 팀의 프로필 조회가 가능합니다.")
	void findByIdSuccess() {
		//given
		given(teamRepository.findById(TEAM.getId())).willReturn(Optional.of(TEAM));
		given(teamConverter.toTeamResponse(TEAM, USER_RESPONSE, Collections.emptyList(), RECORD, REVIEW)).willReturn(
			RESPONSE);
		given(teamMemberGiver.findAllByTeamId(TEAM.getId())).willReturn(Collections.emptyList());
		given(matchRecordGiver.findByTeamTotalRecord(TEAM.getId())).willReturn(RECORD);
		given(matchReviewGiver.findByTeamTotalReview(TEAM.getId())).willReturn(REVIEW);
		given(userConverter.toUserResponse(USER)).willReturn(USER_RESPONSE);

		//when
		TeamResponse foundTeam = teamService.findById(TEAM.getId());

		//then
		verify(teamRepository, times(1)).findById(TEAM.getId());
		verify(teamConverter, times(1)).toTeamResponse(TEAM, USER_RESPONSE, Collections.emptyList(), RECORD, REVIEW);

		assertThat(foundTeam.id()).isEqualTo(TEAM.getId());
	}

	@Test
	@DisplayName("존재하지 않은 ID로 조회 시 EntityNotFoundException 예외 발생")
	void findByIdFail() {
		Long invalidId = 1000L;
		given(teamRepository.findById(invalidId)).willReturn(Optional.empty());

		assertThatThrownBy(() -> teamService.findById(invalidId)).isInstanceOf(EntityNotFoundException.class);
	}

	public User getDemoUser() {
		User demoUser = User.builder()
			.username("test1234")
			.nickname("nickname")
			.password("test")
			.build();

		ReflectionTestUtils.setField(demoUser, "id", 10L);

		return demoUser;
	}

	public UserResponse toUserResponse(User user) {
		return UserResponse.builder()
			.id(user.getId())
			.username(user.getUsername())
			.nickname(user.getNickname())
			.location(user.getLocation())
			.build();
	}
}