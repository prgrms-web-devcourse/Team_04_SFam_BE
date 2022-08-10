package com.kdt.team04.domain.teams.team.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import com.kdt.team04.common.exception.EntityNotFoundException;
import com.kdt.team04.domain.matches.review.dto.response.MatchRecordTotalResponse;
import com.kdt.team04.domain.matches.review.dto.response.MatchReviewTotalResponse;
import com.kdt.team04.domain.matches.review.service.MatchRecordGiverService;
import com.kdt.team04.domain.matches.review.service.MatchReviewGiverService;
import com.kdt.team04.domain.teams.team.dto.TeamConverter;
import com.kdt.team04.domain.teams.team.dto.request.CreateTeamRequest;
import com.kdt.team04.domain.teams.team.dto.response.TeamResponse;
import com.kdt.team04.domain.teams.team.model.SportsCategory;
import com.kdt.team04.domain.teams.team.model.entity.Team;
import com.kdt.team04.domain.teams.team.repository.TeamRepository;
import com.kdt.team04.domain.teams.teammember.dto.response.TeamMemberResponse;
import com.kdt.team04.domain.teams.teammember.service.TeamMemberGiverService;
import com.kdt.team04.domain.user.UserConverter;
import com.kdt.team04.domain.user.dto.response.UserResponse;
import com.kdt.team04.domain.user.entity.User;
import com.kdt.team04.domain.user.service.UserService;

@Transactional
@ExtendWith(MockitoExtension.class)
class TeamServiceTest {
	@InjectMocks
	TeamService teamService;

	@Mock
	UserService userService;

	@Mock
	TeamConverter teamConverter;

	@Mock
	UserConverter userConverter;

	@Mock
	TeamRepository teamRepository;

	@Mock
	TeamMemberGiverService teamMemberGiver;

	@Mock
	MatchRecordGiverService matchRecordGiver;

	@Mock
	MatchReviewGiverService matchReviewGiver;

	@Test
	@DisplayName("팀 생성에 성공합니다.")
	void testCreateSuccess() {
		//given
		User user = getDemoUser();
		UserResponse userResponse = toUserResponse(user);
		Team newTeam = getDemoTeam(null);
		CreateTeamRequest teamCreateRequest = new CreateTeamRequest("team1", "first team",
			SportsCategory.BADMINTON);

		given(userService.findById(any(Long.class))).willReturn(userResponse);
		given(userConverter.toUser(userResponse)).willReturn(user);
		given(teamRepository.save(any(Team.class))).willReturn(newTeam);

		//when
		Long teamId = teamService.create(user.getId(), teamCreateRequest);

		//then
		verify(userService, times(1)).findById(user.getId());
		verify(teamRepository, times(1)).save(any(Team.class));

		assertThat(teamId).isEqualTo(newTeam.getId());
	}

	@Test
	@DisplayName("팀 ID로 해당 팀의 프로필 조회가 가능합니다.")
	void findByIdSuccess() {

		User leader = getDemoUser();
		List<TeamMemberResponse> teamMemberResponses = Collections.emptyList();
		Team team = getDemoTeam(leader);

		MatchRecordTotalResponse record = new MatchRecordTotalResponse(1, 1, 1);
		MatchReviewTotalResponse review = new MatchReviewTotalResponse(1, 1, 1);
		UserResponse userResponse = toUserResponse(leader);
		TeamResponse response = new TeamResponse(team.getId(), team.getName(), team.getDescription(),
			team.getSportsCategory(), teamMemberResponses, record, review, userResponse, null);

		//given
		given(teamRepository.findById(team.getId())).willReturn(Optional.of(team));
		given(teamMemberGiver.findAllByTeamId(team.getId())).willReturn(teamMemberResponses);
		given(matchRecordGiver.findByTeamTotalRecord(team.getId())).willReturn(record);
		given(matchReviewGiver.findByTeamTotalReview(team.getId())).willReturn(review);
		given(userConverter.toUserResponse(team.getLeader())).willReturn(userResponse);
		given(teamConverter.toTeamResponse(team, userResponse, teamMemberResponses, record, review))
			.willReturn(response);

		//when
		TeamResponse foundTeam = teamService.findById(team.getId());

		//then
		verify(teamRepository, times(1)).findById(team.getId());
		verify(teamConverter, times(1)).toTeamResponse(team, userResponse, teamMemberResponses, record, review);

		MatcherAssert.assertThat(foundTeam, Matchers.samePropertyValuesAs(response));
	}

	@Test
	@DisplayName("존재하지 않은 ID로 프로필 조회 시 EntityNotFoundException 예외 발생")
	void findProfileByIdFail() {
		/// given
		Long invalidId = 1000L;

		given(teamRepository.findById(invalidId)).willReturn(Optional.empty());

		// when
		// then
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

	private Team getDemoTeam(User user) {
		return Team.builder()
			.id(1L)
			.name("team1")
			.description("first team")
			.sportsCategory(SportsCategory.BADMINTON)
			.leader(user)
			.build();
	}

	public UserResponse toUserResponse(User user) {
		return UserResponse.builder()
			.id(user.getId())
			.username(user.getUsername())
			.nickname(user.getNickname())
			.build();
	}
}