package com.kdt.team04.domain.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.LongStream;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import com.kdt.team04.common.exception.EntityNotFoundException;
import com.kdt.team04.domain.matches.review.dto.response.MatchReviewTotalResponse;
import com.kdt.team04.domain.matches.review.service.MatchReviewGiverService;
import com.kdt.team04.domain.team.SportsCategory;
import com.kdt.team04.domain.team.dto.response.TeamSimpleResponse;
import com.kdt.team04.domain.team.service.TeamGiverService;
import com.kdt.team04.domain.user.Role;
import com.kdt.team04.domain.user.UserConverter;
import com.kdt.team04.domain.user.dto.request.UserCreateRequest;
import com.kdt.team04.domain.user.dto.response.FindProfileResponse;
import com.kdt.team04.domain.user.dto.response.UserFindResponse;
import com.kdt.team04.domain.user.dto.response.UserResponse;
import com.kdt.team04.domain.user.entity.User;
import com.kdt.team04.domain.user.repository.UserRepository;

@Transactional
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	@InjectMocks
	UserService userService;

	@Mock
	UserRepository userRepository;

	@Mock
	MatchReviewGiverService matchReviewGiver;

	@Mock
	TeamGiverService teamGiver;

	@Mock
	UserConverter userConverter;

	PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	@Test
	@DisplayName("유저 생성 성공 테스트")
	void testCreateSuccess() {
		//given
		UserCreateRequest request = new UserCreateRequest("test00", "@Test1234", "nickname",
			"test00@gmail.com", null, Role.USER);
		User user = User.builder()
			.id(1L)
			.username(request.username())
			.nickname(request.nickname())
			.password(passwordEncoder.encode(request.password()))
			.email(request.email())
			.profileImageUrl(null)
			.role(request.role())
			.build();

		given(userConverter.toUser(request)).willReturn(user);
		given(userRepository.save(any(User.class))).willReturn(user);

		//when
		Long userId = userService.create(request);

		//then
		verify(userConverter, times(1)).toUser(request);
		verify(userRepository, times(1)).save(any(User.class));

		assertThat(userId).isNotNull();
	}

	@Test
	@DisplayName("username으로 유저 조회 성공 테스트")
	void testFindByUsernameSuccess() {
		//given
		UserCreateRequest request = new UserCreateRequest("test00", "@Test1234", "nickname",
			"test00@gmail.com", null, Role.USER);
		User user = User.builder()
			.id(1L)
			.username(request.username())
			.nickname(request.nickname())
			.password(passwordEncoder.encode(request.password()))
			.email(request.email())
			.profileImageUrl(null)
			.role(request.role())
			.build();

		UserResponse response = UserResponse.builder()
			.id(user.getId())
			.username(user.getUsername())
			.nickname(request.nickname())
			.password(user.getPassword())
			.email(user.getEmail())
			.profileImageUrl(user.getProfileImageUrl())
			.role(user.getRole())
			.build();

		given(userRepository.findByUsername(user.getUsername())).willReturn(Optional.of(user));
		given(userConverter.toUserResponse(user)).willReturn(response);

		//when
		UserResponse userResponse = userService.findByUsername(user.getUsername());

		//then
		verify(userConverter, times(1)).toUserResponse(user);
		verify(userRepository, times(1)).findByUsername(user.getUsername());

		assertThat(userResponse.id()).isEqualTo(user.getId());
		assertThat(userResponse.username()).isEqualTo(user.getUsername());
		assertThat(userResponse.nickname()).isEqualTo(user.getNickname());
		assertThat(userResponse.password()).isEqualTo(user.getPassword());
		assertThat(userResponse.email()).isEqualTo(user.getEmail());
		assertThat(userResponse.profileImageUrl()).isEqualTo(user.getProfileImageUrl());
		assertThat(userResponse.role()).isEqualTo(user.getRole());
	}

	@Test
	@DisplayName("사용지 프로필을 닉네임으로 조회한다.")
	void testFindByNicknameSuccess() {
		// given
		String nickname = "test";
		List<User> users = LongStream.range(1, 6)
			.mapToObj(id ->
				new User(id, passwordEncoder.encode("12345"),
					"test0" + id, "test0" + id, null, "test0" + id + "@gmail.com", null, Role.USER)
			)
			.toList();
		List<UserFindResponse> responses = LongStream.range(1, 6)
			.mapToObj(id ->
				new UserFindResponse(id, "test0" + id, "test0" + id, "test0" + id))
			.toList();

		given(userRepository.findByNicknameContaining(nickname)).willReturn(users);

		// when
		List<UserFindResponse> findResponses = userService.findAllByNickname(nickname);

		// then
		verify(userRepository, times(1)).findByNicknameContaining(nickname);

		MatcherAssert.assertThat(findResponses, samePropertyValuesAs(responses));
	}

	@Test
	@DisplayName("사용자 프로필 조회한다.")
	void testFindProfile() {
		// given
		Long requestId = 1L;

		User user = new User(requestId, passwordEncoder.encode("1234"), "test00", "nk-test00", null, "test00@gmail.com",
			null, Role.USER);

		MatchReviewTotalResponse review = new MatchReviewTotalResponse(1, 1, 1);
		List<TeamSimpleResponse> teams = Arrays.asList(
			new TeamSimpleResponse(1L, "축구왕", SportsCategory.SOCCER, null),
			new TeamSimpleResponse(2L, "야구왕", SportsCategory.BASEBALL, null)
		);
		FindProfileResponse response = new FindProfileResponse(user.getUsername(), user.getProfileImageUrl(),
			review, teams);

		given(userRepository.findById(any(Long.class))).willReturn(Optional.of(user));
		given(matchReviewGiver.findTotalReviewByUserId(any(Long.class))).willReturn(review);
		given(teamGiver.findAllByTeamMemberUserId(any(Long.class))).willReturn(teams);

		// when
		FindProfileResponse userResponse = userService.findProfileById(requestId);

		// then
		verify(userRepository, times(1)).findById(requestId);
		verify(matchReviewGiver, times(1)).findTotalReviewByUserId(requestId);
		verify(teamGiver, times(1)).findAllByTeamMemberUserId(requestId);

		MatcherAssert.assertThat(userResponse, samePropertyValuesAs(response));
	}

	@Test
	@DisplayName("존재하지 않는 ID로 프로필 조회 시, 오류가 발생한다.")
	void testFindProfileFail() {
		Long invalidId = 1000L;
		given(userRepository.findById(invalidId)).willReturn(Optional.empty());

		assertThatThrownBy(() ->
			userService.findProfileById(invalidId)
		).isInstanceOf(EntityNotFoundException.class);
	}

	@Test
	@DisplayName("id로 유저 조회 성공 테스트")
	void testFindByIdSuccess() {
		//given
		UserCreateRequest request = new UserCreateRequest("test00", "@Test1234", "nickname",
			"test00@gmail.com", null, Role.USER);
		User user = User.builder()
			.id(1L)
			.username(request.username())
			.nickname(request.nickname())
			.password(passwordEncoder.encode(request.password()))
			.email(request.email())
			.profileImageUrl(null)
			.role(request.role())
			.build();

		UserResponse response = UserResponse.builder()
			.id(user.getId())
			.username(user.getUsername())
			.nickname(request.nickname())
			.password(user.getPassword())
			.email(user.getEmail())
			.profileImageUrl(user.getProfileImageUrl())
			.role(user.getRole())
			.build();

		given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
		given(userConverter.toUserResponse(user)).willReturn(response);

		//when
		UserResponse userResponse = userService.findById(user.getId());

		//then
		verify(userConverter, times(1)).toUserResponse(user);
		verify(userRepository, times(1)).findById(user.getId());

		assertThat(userResponse.id()).isEqualTo(user.getId());
		assertThat(userResponse.username()).isEqualTo(user.getUsername());
		assertThat(userResponse.nickname()).isEqualTo(user.getNickname());
		assertThat(userResponse.password()).isEqualTo(user.getPassword());
		assertThat(userResponse.email()).isEqualTo(user.getEmail());
		assertThat(userResponse.profileImageUrl()).isEqualTo(user.getProfileImageUrl());
		assertThat(userResponse.role()).isEqualTo(user.getRole());
	}

	@Test
	@DisplayName("존재하지 않은 ID로 조회 시 EntityNotFoundException 예외 발생")
	void testFindByIdFail() {
		Long invalidId = 1000L;
		given(userRepository.findById(invalidId)).willReturn(Optional.empty());

		assertThatThrownBy(() -> userService.findById(invalidId)).isInstanceOf(EntityNotFoundException.class);
	}

	@Test
	@DisplayName("존재하지 않는 Usernamedm로 조회 시 EntityNotFoundException 예외 발생")
	void testFindByNotExistUsername() {
		//given
		String notExistUsername = "------";
		given(userRepository.findByUsername(notExistUsername)).willThrow(EntityNotFoundException.class);

		//when, then
		assertThatThrownBy(() -> userService.findByUsername(notExistUsername)).isInstanceOf(
			EntityNotFoundException.class);
	}
}
