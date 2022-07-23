package com.kdt.team04.domain.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Optional;

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
	void testFindByNotExistUsername() {
		//given
		String notExistUsername = "------";
		given(userRepository.findByUsername(notExistUsername)).willThrow(EntityNotFoundException.class);

		//when, then
		assertThatThrownBy(() -> userService.findByUsername(notExistUsername)).isInstanceOf(
			EntityNotFoundException.class);
	}
}