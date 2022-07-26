package com.kdt.team04.domain.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import com.kdt.team04.common.exception.BusinessException;
import com.kdt.team04.common.exception.EntityNotFoundException;
import com.kdt.team04.common.security.jwt.Jwt;
import com.kdt.team04.common.security.jwt.JwtConfig;
import com.kdt.team04.domain.auth.dto.AuthRequest;
import com.kdt.team04.domain.auth.dto.AuthResponse;
import com.kdt.team04.domain.user.Role;
import com.kdt.team04.domain.user.dto.UserRequest;
import com.kdt.team04.domain.user.dto.UserResponse;
import com.kdt.team04.domain.user.service.UserService;

@Transactional
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

	@InjectMocks
	AuthService authService;

	@Mock
	UserService userService;

	@Mock
	TokenService tokenService;

	@Mock
	Jwt jwt;

	@Mock
	PasswordEncoder passwordEncoder;

	@Test
	void testSignInSuccess() {
		//given
		String password = "@Test1234";
		String encodedPassword = "$2a$12$JB1zYmj1TfoylCds8Tt5ue//BQTWE2xO5HZn.MjZcpo.z.7LKagZ.";
		UserResponse userResponse = new UserResponse(1L, "test00", encodedPassword, "nickname");
		List<GrantedAuthority> authorities = new ArrayList<>(
			Collections.singleton(new SimpleGrantedAuthority("USER")));
		Jwt.Claims claims = Jwt.Claims.builder()
			.userId(userResponse.id())
			.roles(new String[] {String.valueOf(Role.USER)})
			.username(userResponse.username())
			.build();
		given(passwordEncoder.matches(password, encodedPassword)).willReturn(true);
		given(userService.findByUsername(userResponse.username())).willReturn(userResponse);
		given(jwt.generateAccessToken(any(Jwt.Claims.class))).willReturn("accessToken");
		given(jwt.verify("accessToken")).willReturn(claims);
		given(jwt.generateRefreshToken()).willReturn("refreshToken");
		given(jwt.getAuthorities(any(Jwt.Claims.class))).willReturn(authorities);
		given(jwt.accessTokenProperties()).willReturn(new JwtConfig.TokenProperties("accessToken", 60));
		given(jwt.refreshTokenProperties()).willReturn(new JwtConfig.TokenProperties("refreshToken", 120));

		//when
		AuthResponse.SignInResponse signInResponse = authService.signIn(userResponse.username(), password);

		//then
		verify(passwordEncoder, times(1)).matches(password, encodedPassword);
		verify(userService, times(1)).findByUsername(userResponse.username());
		verify(jwt, times(1)).verify("accessToken");
		verify(jwt, times(1)).generateAccessToken(any(Jwt.Claims.class));
		verify(jwt, times(1)).generateRefreshToken();
		verify(jwt, times(1)).accessTokenProperties();
		verify(jwt, times(1)).refreshTokenProperties();
		verify(tokenService, times(1)).save("refreshToken", userResponse.id());

		assertThat(signInResponse.id()).isEqualTo(userResponse.id());
		assertThat(signInResponse.username()).isEqualTo(userResponse.username());
		assertThat(signInResponse.nickname()).isEqualTo(userResponse.nickname());
	}

	@Test
	void testSignInFail() {
		//given
		String password = "@Test1234";
		String encodedPassword = "$2a$12$JB1zYmj1TfoylCds8Tt5ue//BQTWE2xO5HZn.MjZcpo.z.7LKagZ.";
		UserResponse userResponse = new UserResponse(1L, "test00", encodedPassword, "nickname");
		given(userService.findByUsername(userResponse.username())).willReturn(userResponse);
		given(passwordEncoder.matches(password, encodedPassword)).willReturn(false);
		//when, then
		assertThatThrownBy(()->authService.signIn(userResponse.username(), password)).isInstanceOf(BusinessException.class);

		verify(userService, times(1)).findByUsername(userResponse.username());
		verify(passwordEncoder, times(1)).matches(password, encodedPassword);
	}

	@Test
	void testSignInWithNotFoundUser() {
		//given
		String password = "@Test1234";
		String encodedPassword = "$2a$12$JB1zYmj1TfoylCds8Tt5ue//BQTWE2xO5HZn.MjZcpo.z.7LKagZ.";
		String notExistUsername = "noname";
		UserResponse userResponse = new UserResponse(1L, "test00", encodedPassword, "nickname");
		given(userService.findByUsername(notExistUsername)).willThrow(EntityNotFoundException.class);
		//when, then
		assertThatThrownBy(()->authService.signIn("noname", password)).isInstanceOf(BusinessException.class).hasMessageContaining(
			MessageFormat.format("username : {0} not found", notExistUsername));

		verify(userService, times(1)).findByUsername(notExistUsername);
	}

	@Test
	void testSignUpSuccess() {
		//given
		String password = "@Test1234";
		String encodedPassword = "$2a$12$JB1zYmj1TfoylCds8Tt5ue//BQTWE2xO5HZn.MjZcpo.z.7LKagZ.";
		Long userId = 1L;
		UserRequest.CreateRequest createRequest = new UserRequest.CreateRequest("username", encodedPassword,
			"nickname");
		AuthRequest.SignUpRequest signUpRequest = new AuthRequest.SignUpRequest("username", password, "nickname");

		given(passwordEncoder.encode(password)).willReturn(encodedPassword);
		given(userService.create(createRequest)).willReturn(userId);

		//when
		AuthResponse.SignUpResponse signUpResponse = authService.signUp(signUpRequest);

		//then
		assertThat(signUpResponse.id()).isEqualTo(userId);
	}

}