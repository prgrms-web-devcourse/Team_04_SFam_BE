package com.kdt.team04.domain.user.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.util.List;
import java.util.stream.LongStream;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kdt.team04.common.ApiResponse;
import com.kdt.team04.domain.user.dto.UserResponse;
import com.kdt.team04.domain.user.entity.User;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@PersistenceContext
	private EntityManager entityManager;

	PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	@Test
	@Transactional
	@DisplayName("회원 프로필을 조회한다.")
	void testFindProfile() throws Exception {
		// given
		User newUser = new User("test00", "nk-test00", passwordEncoder.encode("1234"));
		entityManager.persist(newUser);

		UserResponse.FindProfile userResponse = new UserResponse.FindProfile(newUser.getId(), newUser.getUsername(),
			newUser.getNickname());
		String response = objectMapper.writeValueAsString(new ApiResponse<>(userResponse));

		// when
		ResultActions result = mockMvc.perform(
			get("/api/users/" + newUser.getId())
				.accept(MediaType.APPLICATION_JSON)
		);

		// then
		result.andDo(print())
			.andExpect(status().isOk())
			.andExpect(handler().handlerType(UserController.class))
			.andExpect(handler().methodName("findProfile"))
			.andExpect(content().string(response));
	}

	@Test
	@Transactional
	@DisplayName("회원 프로필 닉네임이 포함된 유저들을 조회한다.")
	void testFindAllByNickname() throws Exception {
		// given
		String nickname = "test";
		LongStream.range(1, 6)
			.mapToObj(id ->
				new User("test00" + id, "test00" + id, passwordEncoder.encode("12345"))
			)
			.forEach(user -> entityManager.persist(user));

		List<UserResponse.UserFindResponse> responses = LongStream.range(1, 6)
			.mapToObj(id ->
				new UserResponse.UserFindResponse(id, "test00" + id, "test00" + id))
			.toList();

		String response = objectMapper.writeValueAsString(new ApiResponse<>(responses));

		// when
		ResultActions result = mockMvc.perform(
			get("/api/users")
				.param("nickname",nickname)
				.accept(MediaType.APPLICATION_JSON)
		);

		// then
		result.andDo(print())
			.andExpect(status().isOk())
			.andExpect(handler().handlerType(UserController.class))
			.andExpect(handler().methodName("findUsers"))
			.andExpect(content().string(response));
	}

	@Test
	@Transactional
	@DisplayName("닉네임이 포함된 사용자가 없다면 비어있는 리스트를 반환한다.")
	void testFindAllByNicknameEmpty() throws Exception {
		// given
		String nickname = "notfound";
		LongStream.range(1, 6)
			.mapToObj(id ->
				new User("test00" + id, "test00" + id, passwordEncoder.encode("12345"))
			)
			.forEach(user -> entityManager.persist(user));

		List<UserResponse.UserFindResponse> responses = Collections.emptyList();

		String response = objectMapper.writeValueAsString(new ApiResponse<>(responses));

		// when
		ResultActions result = mockMvc.perform(
			get("/api/users")
				.param("nickname",nickname)
				.accept(MediaType.APPLICATION_JSON)
		);

		// then
		result.andDo(print())
			.andExpect(status().isOk())
			.andExpect(handler().handlerType(UserController.class))
			.andExpect(handler().methodName("findUsers"))
			.andExpect(content().string(response));
	}

	@Test
	@Transactional
	@DisplayName("닉네임이 null 혹은 빈값으로 요청된다면 예외를 반환한다.")
	void testFindAllByNicknameException() throws Exception {
		// given
		LongStream.range(1, 6)
			.mapToObj(id ->
				new User("test00" + id, "test00" + id, passwordEncoder.encode("12345"))
			)
			.forEach(user -> entityManager.persist(user));

		// when
		ResultActions result = mockMvc
			.perform(get("/api/users"));

		// then
		result.andDo(print())
			.andExpect(status().isBadRequest());
	}

}