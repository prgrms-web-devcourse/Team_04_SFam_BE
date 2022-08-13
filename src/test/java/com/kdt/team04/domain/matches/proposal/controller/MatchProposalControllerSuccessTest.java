package com.kdt.team04.domain.matches.proposal.controller;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

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
import com.kdt.team04.domain.matches.proposal.dto.request.CreateProposalRequest;
import com.kdt.team04.domain.matches.proposal.dto.request.ReactProposalRequest;
import com.kdt.team04.domain.matches.proposal.dto.response.ChatRoomResponse;
import com.kdt.team04.domain.matches.proposal.dto.response.ProposalChatResponse;
import com.kdt.team04.domain.matches.proposal.entity.MatchProposalStatus;
import com.kdt.team04.domain.matches.proposal.service.MatchProposalService;
import com.kdt.team04.domain.security.WithMockJwtAuthentication;

@WithMockJwtAuthentication
@WebMvcTest({MatchProposalController.class, WebSecurityConfig.class})
class MatchProposalControllerSuccessTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@MockBean
	MatchProposalService matchProposalService;

	@MockBean
	TokenService tokenService;

	@MockBean
	Jwt jwt;

	final String BASE_END_POINT = "/api/matches";

	final Long DEFAULT_AUTH_ID = 1L;
	final Long DEFAULT_TEAM_ID = 1L;
	final Long DEFAULT_PROPOSAL_ID = 1L;
	final Long DEFAULT_MATCH_ID = 1L;

	@Test
	@DisplayName("대결 신청을 보내고, 상태 코드 200을 반환한다.")
	void propose() throws Exception {
		//given
		CreateProposalRequest request = new CreateProposalRequest(DEFAULT_TEAM_ID, "함 떠요");
		given(matchProposalService.create(DEFAULT_AUTH_ID, DEFAULT_MATCH_ID, request)).willReturn(DEFAULT_PROPOSAL_ID);

		//when
		ResultActions resultActions = mockMvc.perform(
			post(BASE_END_POINT + "/{matchId}/proposals", DEFAULT_MATCH_ID)
				.content(objectMapper.writeValueAsString(request))
				.contentType(MediaType.APPLICATION_JSON)
		).andDo(print());

		//then
		verify(matchProposalService).create(DEFAULT_AUTH_ID, DEFAULT_MATCH_ID, request);

		resultActions.andExpect(status().isOk());
	}

	@Test
	@DisplayName("매칭 공고자는 대결 신청을 수락하고 상태코드 200을 반환합니다.")
	void proposeApproveReact() throws Exception {
		//given
		ReactProposalRequest request = new ReactProposalRequest(MatchProposalStatus.APPROVED);

		given(
			matchProposalService.approveOrRefuse(DEFAULT_AUTH_ID, DEFAULT_MATCH_ID, DEFAULT_PROPOSAL_ID,
				MatchProposalStatus.APPROVED)).willReturn(
			MatchProposalStatus.APPROVED);

		//when
		ResultActions resultActions = mockMvc.perform(
			patch(BASE_END_POINT + "/{matchId}/proposals/{id}", DEFAULT_MATCH_ID, DEFAULT_PROPOSAL_ID)
				.content(objectMapper.writeValueAsString(request))
				.contentType(MediaType.APPLICATION_JSON)
		).andDo(print());

		//then
		verify(matchProposalService).approveOrRefuse(DEFAULT_AUTH_ID, DEFAULT_MATCH_ID, DEFAULT_PROPOSAL_ID,
			MatchProposalStatus.APPROVED);

		resultActions.andExpect(status().isOk());
	}

	@Test
	@DisplayName("매칭 공고자는 대결 신청을 거절하고 상태코드 200을 반환합니다.")
	void proposeRefuseReact() throws Exception {
		//given
		ReactProposalRequest request = new ReactProposalRequest(MatchProposalStatus.REFUSE);

		given(
			matchProposalService.approveOrRefuse(DEFAULT_AUTH_ID, DEFAULT_MATCH_ID, DEFAULT_PROPOSAL_ID,
				MatchProposalStatus.REFUSE)).willReturn(
			MatchProposalStatus.REFUSE);

		//when
		ResultActions resultActions = mockMvc.perform(
			patch(BASE_END_POINT + "/{matchId}/proposals/{id}", DEFAULT_MATCH_ID, DEFAULT_PROPOSAL_ID)
				.content(objectMapper.writeValueAsString(request))
				.contentType(MediaType.APPLICATION_JSON)
		).andDo(print());

		//then
		verify(matchProposalService).approveOrRefuse(DEFAULT_AUTH_ID, DEFAULT_MATCH_ID, DEFAULT_PROPOSAL_ID,
			MatchProposalStatus.REFUSE);

		resultActions.andExpect(status().isOk());
	}

	@Test
	@DisplayName("해당 대결의 신청 목록을 조회하고 상태 코드 200을 반환한다.")
	void findAllChats() throws Exception {
		//given
		List<ChatRoomResponse> values = List.of(new ChatRoomResponse(DEFAULT_PROPOSAL_ID, "content", null, null, null,
			LocalDateTime.now()));
		String response = objectMapper.writeValueAsString(new ApiResponse<>(values));

		given(matchProposalService.findAllProposalChats(DEFAULT_MATCH_ID, DEFAULT_AUTH_ID)).willReturn(values);

		//when
		ResultActions resultActions = mockMvc.perform(
			get(BASE_END_POINT + "/{matchId}/proposals", DEFAULT_MATCH_ID)
				.contentType(MediaType.APPLICATION_JSON)
		).andDo(print());

		//then
		verify(matchProposalService).findAllProposalChats(DEFAULT_MATCH_ID, DEFAULT_AUTH_ID);

		resultActions
			.andExpect(status().isOk())
			.andExpect(content().string(response));
	}

	@Test
	@DisplayName("로그인된 사용자의 신청 목록을 전체조회하고 상태 코드 200을 반환한다.")
	void findAllChatsByUserId() throws Exception {
		//given
		List<ChatRoomResponse> values = List.of(new ChatRoomResponse(DEFAULT_PROPOSAL_ID, "content", null, null, null,
			LocalDateTime.now()));
		String response = objectMapper.writeValueAsString(new ApiResponse<>(values));

		given(matchProposalService.findAllProposals(DEFAULT_AUTH_ID)).willReturn(values);

		//when
		ResultActions resultActions = mockMvc.perform(
			get(BASE_END_POINT + "/proposals")
				.contentType(MediaType.APPLICATION_JSON)
		).andDo(print());

		//then
		verify(matchProposalService).findAllProposals(DEFAULT_AUTH_ID);

		resultActions
			.andExpect(status().isOk())
			.andExpect(content().string(response));

	}

	@Test
	@DisplayName("해당 ID의 신청을 조회하고 상태코드 200을 반환한다.")
	void getProposalById() throws Exception {
		//given
		ProposalChatResponse value = new ProposalChatResponse(DEFAULT_PROPOSAL_ID, MatchProposalStatus.WAITING,
			"content", true);
		String response = objectMapper.writeValueAsString(new ApiResponse<>(value));

		given(matchProposalService.findById(DEFAULT_PROPOSAL_ID, DEFAULT_AUTH_ID)).willReturn(value);

		//when
		ResultActions resultActions = mockMvc.perform(
			get(BASE_END_POINT + "/proposals/{id}", DEFAULT_PROPOSAL_ID)
		).andDo(print());

		//then
		verify(matchProposalService).findById(DEFAULT_PROPOSAL_ID, DEFAULT_AUTH_ID);

		resultActions
			.andExpect(status().isOk())
			.andExpect(content().string(response));
	}
}
