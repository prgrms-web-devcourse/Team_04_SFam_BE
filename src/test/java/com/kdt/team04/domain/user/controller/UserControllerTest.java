package com.kdt.team04.domain.user.controller;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kdt.team04.common.ApiResponse;
import com.kdt.team04.common.security.WebSecurityConfig;
import com.kdt.team04.common.security.jwt.Jwt;
import com.kdt.team04.domain.auth.service.TokenService;
import com.kdt.team04.domain.user.service.UserService;

@WebMvcTest({UserController.class, WebSecurityConfig.class})
class UserControllerTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@MockBean
	UserService userService;

	@MockBean
	TokenService tokenService;

	@MockBean
	Jwt jwt;

	@Test
	@DisplayName("닉네임 중복시 true값 반환")
	void testNicknameDuplicationCheckTrue() throws Exception {
		//given
		String duplicatedNickname = "test1234";
		given(userService.nicknameDuplicationCheck(duplicatedNickname)).willReturn(true);

		String response = objectMapper.writeValueAsString(new ApiResponse<>(true));

		//when
		ResultActions perform = mockMvc.perform(get("/api/users/nickname/duplication")
			.param("input", duplicatedNickname)
			.contentType(MediaType.APPLICATION_JSON)
		).andDo(print());

		//then
		verify(userService, times(1)).nicknameDuplicationCheck(duplicatedNickname);

		perform
			.andExpect(status().isOk())
			.andExpect(content().string(response));
	}

	@Test
	@DisplayName("닉네임 중복아닐시 false값 반환")
	void testNicknameDuplicationCheckFalse() throws Exception {
		//given
		String notDuplicatedNickname = "test1234";
		given(userService.nicknameDuplicationCheck(notDuplicatedNickname)).willReturn(false);

		String response = objectMapper.writeValueAsString(new ApiResponse<>(false));

		//when
		ResultActions perform = mockMvc.perform(get("/api/users/nickname/duplication")
			.param("input", notDuplicatedNickname)
			.contentType(MediaType.APPLICATION_JSON)
		).andDo(print());

		//then
		verify(userService, times(1)).nicknameDuplicationCheck(notDuplicatedNickname);

		perform
			.andExpect(status().isOk())
			.andExpect(content().string(response));
	}

	@Test
	@DisplayName("유저네임 중복아닐시 false값 반환")
	void testUsernameDuplicationCheckFalse() throws Exception {
		//given
		String notDuplicatedUsername = "test1234";
		given(userService.usernameDuplicationCheck(notDuplicatedUsername)).willReturn(false);

		String response = objectMapper.writeValueAsString(new ApiResponse<>(false));

		//when
		ResultActions perform = mockMvc.perform(get("/api/users/username/duplication")
			.param("input", notDuplicatedUsername)
			.contentType(MediaType.APPLICATION_JSON)
		).andDo(print());

		//then
		verify(userService, times(1)).usernameDuplicationCheck(notDuplicatedUsername);

		perform
			.andExpect(status().isOk())
			.andExpect(content().string(response));
	}

	@Test
	@DisplayName("유저네임 중복 시 true값 반환")
	void testUsernameDuplicationCheckTrue() throws Exception {
		//given
		String duplicatedUsername = "test1234";
		given(userService.usernameDuplicationCheck(duplicatedUsername)).willReturn(true);

		String response = objectMapper.writeValueAsString(new ApiResponse<>(true));

		//when
		ResultActions perform = mockMvc.perform(get("/api/users/username/duplication")
			.param("input", duplicatedUsername)
			.contentType(MediaType.APPLICATION_JSON)
		).andDo(print());

		//then
		verify(userService, times(1)).usernameDuplicationCheck(duplicatedUsername);

		perform
			.andExpect(status().isOk())
			.andExpect(content().string(response));
	}
}
