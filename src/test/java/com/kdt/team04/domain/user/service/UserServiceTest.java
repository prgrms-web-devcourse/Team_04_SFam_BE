package com.kdt.team04.domain.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
import com.kdt.team04.domain.user.dto.UserRequest;
import com.kdt.team04.domain.user.dto.UserResponse;
import com.kdt.team04.domain.user.entity.User;
import com.kdt.team04.domain.user.repository.UserRepository;

@Transactional
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	@InjectMocks
	UserService userService;

	@Mock
	UserRepository userRepository;

	PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	@Test
	void testCreateSuccess() {
		//given
		UserRequest.CreateRequest request = new UserRequest.CreateRequest("test00", "@Test1234", "nickname");
		User user = new User(1L, "test00", "nickname", passwordEncoder.encode(request.password()));
		given(userRepository.save(any(User.class))).willReturn(user);
		//when
		Long userId = userService.create(request);

		//then
		verify(userRepository, times(1)).save(any(User.class));

		assertThat(userId).isNotNull();
	}

	@Test
	void testFindByUsernameSuccess() {
		//given
		UserRequest.CreateRequest request = new UserRequest.CreateRequest("test00", "@Test1234", "nickname");
		User user = new User(1L, "test00", "nickname", passwordEncoder.encode(request.password()));
		given(userRepository.findByUsername(user.getUsername())).willReturn(Optional.of(user));

		//when
		UserResponse userResponse = userService.findByUsername(user.getUsername());

		//then
		assertThat(userResponse.id()).isEqualTo(user.getId());
		assertThat(userResponse.username()).isEqualTo(user.getUsername());
		assertThat(userResponse.nickname()).isEqualTo(user.getNickname());
		assertThat(userResponse.password()).isEqualTo(user.getPassword());
	}

	@Test
	@DisplayName("사용지 프로필을 닉네임으로 조회한다.")
	void testFindByNicknameSuccess() {
		// given
		String nickname = "test";
		List<User> users = LongStream.range(1, 6)
			.mapToObj(id ->
				new User(id, passwordEncoder.encode("12345"),
					"test0" + id, "test0" + id)
			)
			.toList();
		List<UserResponse.UserFindResponse> responses = LongStream.range(1, 6)
			.mapToObj(id ->
				new UserResponse.UserFindResponse(id, "test0" + id, "test0" + id))
			.toList();

		given(userRepository.findByNicknameContaining(nickname)).willReturn(users);

		// when
		List<UserResponse.UserFindResponse> findResponses = userService.findAllByNickname(nickname);

		// then
		verify(userRepository, times(1)).findByNicknameContaining(nickname);

		MatcherAssert.assertThat(findResponses, samePropertyValuesAs(responses));
	}

	@Test
	@DisplayName("사용자 프로필 조회한다.")
	void testFindProfile() {
		// given
		Long requestId = 1L;

		User user = new User(requestId, passwordEncoder.encode("1234"), "test00", "nk-test00");
		UserResponse.FindProfile response =
			new UserResponse.FindProfile(user.getId(), user.getUsername(), user.getNickname());

		given(userRepository.findById(any(Long.class))).willReturn(Optional.of(user));

		// when
		UserResponse.FindProfile userResponse = userService.findProfileById(requestId);

		// then
		verify(userRepository, times(1)).findById(requestId);

		MatcherAssert.assertThat(userResponse, samePropertyValuesAs(response));
	}

	@Test
	void testFindByIdSuccess() {
		//given
		UserRequest.CreateRequest request = new UserRequest.CreateRequest("test00", "@Test1234", "nickname");
		User user = new User(1L, "test00", "nickname", passwordEncoder.encode(request.password()));
		given(userRepository.findById(user.getId())).willReturn(Optional.of(user));

		//when
		UserResponse userResponse = userService.findById(user.getId());

		//then
		assertThat(userResponse.id()).isEqualTo(user.getId());
		assertThat(userResponse.username()).isEqualTo(user.getUsername());
		assertThat(userResponse.nickname()).isEqualTo(user.getNickname());
		assertThat(userResponse.password()).isEqualTo(user.getPassword());
	}

	@Test
	@DisplayName("존재하지 않은 ID로 조회 시 EntityNotFoundException 예외 발생")
	void testFindByIdFail() {
		Long invalidId = 1000L;
		given(userRepository.findById(invalidId)).willReturn(Optional.empty());

		assertThatThrownBy(() -> userService.findById(invalidId)).isInstanceOf(EntityNotFoundException.class);
	}

	@Test
	void testFindByNotExistUsername() {
		//given
		String notExistUsername = "------";
		given(userRepository.findByUsername(notExistUsername)).willThrow(EntityNotFoundException.class);

		//when, then
		assertThatThrownBy(() -> userService.findByUsername(notExistUsername)).isInstanceOf(
			EntityNotFoundException.class);
	}
}
