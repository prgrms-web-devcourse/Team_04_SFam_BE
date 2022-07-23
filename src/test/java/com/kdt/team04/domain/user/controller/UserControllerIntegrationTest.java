package com.kdt.team04.domain.user.controller;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.kdt.team04.domain.user.entity.User;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@PersistenceContext
	private EntityManager entityManager;

	@Test
	@Transactional
	@DisplayName("회원 프로필을 조회한다.")
	void testFindProfile() throws Exception {
		// given
		User newUser = new User("test00", "nk-test00", "1234");
		entityManager.persist(newUser);

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
			.andExpect(jsonPath("$.response.id", is(newUser.getId()), Long.class))
			.andExpect(jsonPath("$.response.username", is(newUser.getUsername())))
			.andExpect(jsonPath("$.response.nickname", is(newUser.getNickname())));
	}
}