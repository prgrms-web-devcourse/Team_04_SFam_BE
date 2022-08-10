package com.kdt.team04.domain.teams.team.controller;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
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
import com.kdt.team04.domain.matches.review.dto.response.MatchRecordTotalResponse;
import com.kdt.team04.domain.matches.review.dto.response.MatchReviewTotalResponse;
import com.kdt.team04.domain.security.WithMockJwtAuthentication;
import com.kdt.team04.domain.teams.team.dto.request.CreateTeamRequest;
import com.kdt.team04.domain.teams.team.dto.response.TeamResponse;
import com.kdt.team04.domain.teams.team.model.SportsCategory;
import com.kdt.team04.domain.teams.team.service.TeamService;
import com.kdt.team04.domain.teams.teammember.dto.response.TeamMemberResponse;
import com.kdt.team04.domain.user.dto.response.UserResponse;

@WithMockJwtAuthentication
@WebMvcTest({TeamController.class, WebSecurityConfig.class})
class TeamControllerTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@MockBean
	TeamService teamService;

	@MockBean
	TokenService tokenService;

	@MockBean
	Jwt jwt;

	final String BASE_END_POINT = "/api/teams";

	final Long DEFAULT_AUTH_ID = 1L;

	@Test
	@DisplayName("인가된 사용자만이 팀을 생성할 수 있다.")
	void successTeamCreate() throws Exception {
		//given
		CreateTeamRequest request = new CreateTeamRequest("Deam", "축지법", SportsCategory.SOCCER);
		Long creatingTeamId = 1L;

		given(teamService.create(DEFAULT_AUTH_ID, request)).willReturn(creatingTeamId);

		//when
		ResultActions perform = mockMvc.perform(
			post(BASE_END_POINT).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
		).andDo(print());

		//then
		BDDMockito.verify(teamService, times(1)).create(DEFAULT_AUTH_ID, request);

		perform.andExpect(status().isOk());
	}

	@Test
	@DisplayName("팀 프로필을 조회한다.")
	void successGettingTeamProfile() throws Exception {
		//given
		Long teamId = 1L;
		List<TeamMemberResponse> members = Collections.emptyList();
		TeamResponse response = TeamResponse.builder()
			.id(teamId)
			.name("치투더킨")
			.description("chick")
			.sportsCategory(SportsCategory.BADMINTON)
			.members(members)
			.matchRecord(new MatchRecordTotalResponse(1, 1, 0))
			.matchReview(new MatchReviewTotalResponse(2, 0, 0))
			.leader(
				UserResponse.builder()
					.id(1L)
					.nickname("kkyu")
					.build()
			).build();
		ApiResponse<TeamResponse> expectedRealResponse = new ApiResponse<>(response);

		given(teamService.findById(teamId)).willReturn(response);

		//when
		ResultActions perform = mockMvc.perform(
			get(BASE_END_POINT + "/" + teamId)
		).andDo(print());

		//then
		String realResponse = perform.andExpect(status().isOk())
			.andReturn()
			.getResponse()
			.getContentAsString(StandardCharsets.UTF_8);

		Assertions.assertThat(realResponse).isEqualTo(
			objectMapper.writeValueAsString(expectedRealResponse)
		);
	}

	@Nested
	@DisplayName("팀을 생성 할 때")
	class CreateTeamRequestValidationTest {

		@Test
		@DisplayName("팀 이름이 2자 미만 이거나 혹은 11자 일떄와 공백으로만 이루어진 문자열들에 입력에 대해서는 실패한다.")
		void failToInvalidTeamName() throws Exception {
			//given
			List<String> invalids = List.of("우", "우영우 우영우 우영우", "", "    ");
			List<CreateTeamRequest> invalidedRequests = invalids.stream()
				.map(invalid -> new CreateTeamRequest(invalid, "동 to the 뀨 to the 롸미", SportsCategory.BADMINTON))
				.toList();

			invalidedRequests.forEach(request -> {
				try {
					//when
					//then
					action(request).andExpect(status().isBadRequest());
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			});
		}

		@Test
		@DisplayName("팀 설명이 101자 이상일 때와 공백으로만 이루어진 문자열들에 입력에 대해서는 실패한다.")
		void failToInvalidDescription() {
			//given
			String longerThan100Letter = Stream.generate(() -> "우")
				.limit(101).collect(Collectors.joining());

			List<String> invalids = List.of("", "    ", longerThan100Letter);
			List<CreateTeamRequest> invalidedRequests = invalids.stream()
				.map(invalid -> new CreateTeamRequest(invalid, "동 to the 뀨 to the 롸미", SportsCategory.BADMINTON))
				.toList();

			//when
			invalidedRequests.forEach(request -> {
				try {
					//when
					//then
					action(request).andExpect(status().isBadRequest());
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			});
		}

		@Test
		@DisplayName("팀 종목이 선택되지 않으면 실패한다.")
		void failToNullSportCategory() throws Exception {
			//given
			CreateTeamRequest request = new CreateTeamRequest("우영우김밥", "동 to the 뀨 to the 롸미", null);

			//when
			//then
			action(request).andExpect(status().isBadRequest());
		}

		private ResultActions action(CreateTeamRequest request) throws Exception {
			return mockMvc.perform(
				post(BASE_END_POINT)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(request))
			);
		}
	}

}