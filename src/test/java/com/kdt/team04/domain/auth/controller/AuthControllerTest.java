package com.kdt.team04.domain.auth.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kdt.team04.common.ApiResponse;
import com.kdt.team04.common.exception.ErrorCode;
import com.kdt.team04.common.exception.ErrorResponse;
import com.kdt.team04.common.security.WebSecurityConfig;
import com.kdt.team04.common.security.jwt.Jwt;
import com.kdt.team04.common.security.jwt.JwtAuthentication;
import com.kdt.team04.common.security.jwt.JwtAuthenticationToken;
import com.kdt.team04.domain.auth.dto.TokenDto;
import com.kdt.team04.domain.auth.dto.request.SignInRequest;
import com.kdt.team04.domain.auth.dto.request.SignUpRequest;
import com.kdt.team04.domain.auth.dto.response.SignInResponse;
import com.kdt.team04.domain.auth.dto.response.SignUpResponse;
import com.kdt.team04.domain.auth.service.AuthService;
import com.kdt.team04.domain.auth.service.TokenService;
import com.kdt.team04.domain.user.Role;
import com.kdt.team04.domain.user.entity.User;

@WebMvcTest({AuthController.class, WebSecurityConfig.class})
class AuthControllerTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@MockBean
	AuthService authService;

	@MockBean
	TokenService tokenService;

	@MockBean
	Jwt jwt;

	@Test
	@DisplayName("signIn 성공 테스트")
	void testSignInSuccess() throws Exception {
		//given
		String username = "test1234";
		String password = "!Password1234";
		String encodedPassword = "$2a$12$VBMdI3AHeZK.1iPAK97kaO1K/YPNjoTjBjEfolydYMXpFHpr1ZljS";
		User user = new User(
			1L,
			username,
			encodedPassword,
			"nickName",
			null,
			"test1234@gmail.com",
			null,
			Role.USER
		);

		SignInRequest signInRequest = new SignInRequest(username, password);
		List<GrantedAuthority> authorities = new ArrayList<>();
		SimpleGrantedAuthority role_anonymous = new SimpleGrantedAuthority("ROLE_MEMBER");
		authorities.add(role_anonymous);
		SignInResponse signInResponse = new SignInResponse(
			user.getId(),
			username,
			user.getNickname(),
			null,
			null,
			Role.USER,
			null,
			null,
			null,
			new TokenDto("accessToken", "accessToken", 3600),
			new TokenDto("refreshToken", "refreshToken", 3600),
			new JwtAuthenticationToken(new JwtAuthentication("accessToken", user.getId(), username, user.getEmail()),
				null,
				authorities
			));

		String request = objectMapper.writeValueAsString(signInRequest);
		String response = objectMapper.writeValueAsString(new ApiResponse<>(signInResponse));

		given(authService.signIn(signInRequest.username(), signInRequest.password())).willReturn(signInResponse);

		//when
		ResultActions perform = mockMvc.perform(post("/api/users/signin")
			.content(request)
			.contentType(MediaType.APPLICATION_JSON)
		).andDo(print());

		//then
		verify(authService, times(1)).signIn(signInRequest.username(), signInRequest.password());

		perform
			.andExpect(status().isOk())
			.andExpect(content().string(response));
	}

	@Test
	@DisplayName("회원 가입 성공 테스트")
	void testSignUpSuccess() throws Exception {
		//given
		String username = "test1234";
		String password = "!Password1234";
		String encodedPassword = "$2a$12$VBMdI3AHeZK.1iPAK97kaO1K/YPNjoTjBjEfolydYMXpFHpr1ZljS";
		User user = new User(
			1L,
			username,
			encodedPassword,
			"nickName",
			null,
			"test1234@gmail.com",
			null,
			Role.USER
		);
		SignUpRequest signUpRequest = new SignUpRequest(username, password, user.getNickname(),
			user.getEmail());
		SignUpResponse signUpResponse = new SignUpResponse(user.getId());

		String request = objectMapper.writeValueAsString(signUpRequest);
		String response = objectMapper.writeValueAsString(new ApiResponse<>(signUpResponse));

		given(authService.signUp(signUpRequest)).willReturn(signUpResponse);

		//when
		ResultActions perform = mockMvc.perform(post("/api/users/signup")
			.content(request)
			.contentType(MediaType.APPLICATION_JSON)
		).andDo(print());

		//then
		verify(authService, times(1)).signUp(any(SignUpRequest.class));

		perform
			.andExpect(status().isOk())
			.andExpect(content().string(response));
	}

	@Test
	@DisplayName("회원 가입 Null Username 전달 시 실패")
	void testSignUpWithNullUsername() throws Exception {
		//given
		String username = "test1234";
		String password = "!Password1234";
		String encodedPassword = "$2a$12$VBMdI3AHeZK.1iPAK97kaO1K/YPNjoTjBjEfolydYMXpFHpr1ZljS";
		User user = new User(
			1L,
			username,
			encodedPassword,
			"nickName",
			null,
			"test1234@gmail.com",
			null,
			Role.USER
		);
		SignUpRequest signUpRequest = new SignUpRequest(null, password, user.getNickname(),
			user.getEmail());

		String request = objectMapper.writeValueAsString(signUpRequest);
		String response = objectMapper.writeValueAsString(new ErrorResponse<>(ErrorCode.METHOD_ARGUMENT_NOT_VALID));

		//when
		ResultActions perform = mockMvc.perform(post("/api/users/signup")
			.content(request)
			.contentType(MediaType.APPLICATION_JSON)
		).andDo(print());

		//then
		perform
			.andExpect(status().isBadRequest())
			.andExpect(content().string(response));
	}

	@Test
	@DisplayName("회원가입 null 패스워드 전달 시 실패")
	void testSignUpWithNullPassword() throws Exception {
		//given
		String username = "test1234";
		String encodedPassword = "$2a$12$VBMdI3AHeZK.1iPAK97kaO1K/YPNjoTjBjEfolydYMXpFHpr1ZljS";
		String nickname = "nickname";
		String email = "test1234@gmail.com";
		SignUpRequest signUpRequest = new SignUpRequest(username, null, nickname, email);

		String request = objectMapper.writeValueAsString(signUpRequest);
		String response = objectMapper.writeValueAsString(new ErrorResponse<>(ErrorCode.METHOD_ARGUMENT_NOT_VALID));

		//when
		ResultActions perform = mockMvc.perform(post("/api/users/signup")
			.content(request)
			.contentType(MediaType.APPLICATION_JSON)
		).andDo(print());

		//then
		perform
			.andExpect(status().isBadRequest())
			.andExpect(content().string(response));
	}

	@Test
	@DisplayName("회원가입 Null Nickname 전달 시 실패")
	void testSignUpWithNullNickName() throws Exception {
		//given
		String username = "test1234";
		String password = "!Password1234";
		String email = "test1234@gmail.com";
		SignUpRequest signUpRequest = new SignUpRequest(username, password, null, email);

		String request = objectMapper.writeValueAsString(signUpRequest);
		String response = objectMapper.writeValueAsString(new ErrorResponse<>(ErrorCode.METHOD_ARGUMENT_NOT_VALID));

		//when
		ResultActions perform = mockMvc.perform(post("/api/users/signup")
			.content(request)
			.contentType(MediaType.APPLICATION_JSON)
		).andDo(print());

		//then
		perform
			.andExpect(status().isBadRequest())
			.andExpect(content().string(response));
	}

	@Test
	@DisplayName("회원가입 형식에 맞지않는 패스워드 전달 시 실패")
	void testSignUpWithViolatedPassword() throws Exception {
		//given
		String username = "test1234";
		String password = "password";
		String email = "test1234@gmail.com";
		SignUpRequest signUpRequest = new SignUpRequest(username, password, null, email);

		String request = objectMapper.writeValueAsString(signUpRequest);
		String response = objectMapper.writeValueAsString(new ErrorResponse<>(ErrorCode.METHOD_ARGUMENT_NOT_VALID));

		//when
		ResultActions perform = mockMvc.perform(post("/api/users/signup")
			.content(request)
			.contentType(MediaType.APPLICATION_JSON)
		).andDo(print());

		//then
		perform
			.andExpect(status().isBadRequest())
			.andExpect(content().string(response));
	}

	@Test
	@DisplayName("로그인 Null Username 전달 시 실패")
	void testSignInWithNullUsername() throws Exception {
		//given
		String password = "!Password1234";
		SignInRequest signInRequest = new SignInRequest(null, password);

		String request = objectMapper.writeValueAsString(signInRequest);
		String response = objectMapper.writeValueAsString(new ErrorResponse<>(ErrorCode.METHOD_ARGUMENT_NOT_VALID));

		//when
		ResultActions perform = mockMvc.perform(post("/api/users/signin")
			.content(request)
			.contentType(MediaType.APPLICATION_JSON)
		).andDo(print());

		//then
		perform
			.andExpect(status().isBadRequest())
			.andExpect(content().string(response));
	}

	@Test
	@DisplayName("로그인 Null Password 전달 시 실패")
	void testSignInWithNullPassword() throws Exception {
		//given
		SignInRequest signInRequest = new SignInRequest("test1234", null);

		String request = objectMapper.writeValueAsString(signInRequest);
		String response = objectMapper.writeValueAsString(new ErrorResponse<>(ErrorCode.METHOD_ARGUMENT_NOT_VALID));

		//when
		ResultActions perform = mockMvc.perform(post("/api/users/signin")
			.content(request)
			.contentType(MediaType.APPLICATION_JSON)
		).andDo(print());

		//then
		perform
			.andExpect(status().isBadRequest())
			.andExpect(content().string(response));
	}
}