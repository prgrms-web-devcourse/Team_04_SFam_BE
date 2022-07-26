package com.kdt.team04.domain.team.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import com.kdt.team04.common.exception.EntityNotFoundException;
import com.kdt.team04.domain.team.SportsCategory;
import com.kdt.team04.domain.team.dto.TeamConverter;
import com.kdt.team04.domain.team.dto.TeamRequest;
import com.kdt.team04.domain.team.dto.TeamResponse;
import com.kdt.team04.domain.team.entity.Team;
import com.kdt.team04.domain.team.repository.TeamRepository;
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
	private TeamRepository teamRepository;

	private final User USER = new User(1L, "password", "username", "nickname");
	private final UserResponse USER_RESPONSE = new UserResponse(USER.getId(), USER.getUsername(), USER.getPassword(),
		USER.getNickname());
	private final TeamRequest.CreateRequest CREATE_REQUEST = new TeamRequest.CreateRequest("team1", "first team",
		SportsCategory.BADMINTON);
	private final Team TEAM = new Team(10L, CREATE_REQUEST.name(), CREATE_REQUEST.description(),
		CREATE_REQUEST.sportsCategory(), USER);
	private final TeamResponse RESPONSE = new TeamResponse(TEAM.getId(), TEAM.getTeamName(), TEAM.getDescription(),
		TEAM.getSportsCategory(), USER_RESPONSE, TEAM.getCreatedAt(), TEAM.getUpdatedAt());

	@Test
	@DisplayName("팀 생성에 성공합니다.")
	void createSuccess() {
		//given
		given(userService.findById(USER.getId())).willReturn(USER_RESPONSE);
		given(teamRepository.save(any(Team.class))).willReturn(TEAM);
		given(teamConverter.toTeamResponse(TEAM)).willReturn(RESPONSE);

		//when
		TeamResponse createdTeam = teamService.create(USER.getId(), CREATE_REQUEST.name(),
			CREATE_REQUEST.sportsCategory(),
			CREATE_REQUEST.description());

		//then
		verify(userService, times(1)).findById(USER.getId());
		verify(teamRepository, times(1)).save(any(Team.class));
		verify(teamConverter, times(1)).toTeamResponse(TEAM);

		assertThat(createdTeam.id()).isEqualTo(TEAM.getId());
	}

	@Test
	@DisplayName("팀 ID로 해당 팀의 프로필 조회가 가능합니다.")
	void findByIdSuccess() {
		//given
		given(teamRepository.findById(TEAM.getId())).willReturn(Optional.of(TEAM));
		given(teamConverter.toTeamResponse(TEAM)).willReturn(RESPONSE);

		//when
		TeamResponse foundTeam = teamService.findById(TEAM.getId());

		//then
		verify(teamRepository, times(1)).findById(TEAM.getId());
		verify(teamConverter, times(1)).toTeamResponse(TEAM);

		assertThat(foundTeam.id()).isEqualTo(TEAM.getId());
	}

	@Test
	@DisplayName("존재하지 않은 ID로 조회 시 EntityNotFoundException 예외 발생")
	void findByIdFail() {
		Long invalidId = 1000L;
		given(teamRepository.findById(invalidId)).willReturn(Optional.empty());

		assertThatThrownBy(() -> teamService.findById(invalidId)).isInstanceOf(EntityNotFoundException.class);
	}
}