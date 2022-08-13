package com.kdt.team04.domain.matches.proposal.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import com.kdt.team04.common.exception.ErrorCode;
import com.kdt.team04.common.exception.ErrorResponse;
import com.kdt.team04.common.security.WebSecurityConfig;
import com.kdt.team04.common.security.jwt.Jwt;
import com.kdt.team04.domain.auth.service.TokenService;
import com.kdt.team04.domain.matches.proposal.dto.request.CreateProposalRequest;
import com.kdt.team04.domain.matches.proposal.dto.request.ReactProposalRequest;
import com.kdt.team04.domain.matches.proposal.entity.MatchProposalStatus;
import com.kdt.team04.domain.matches.proposal.service.MatchProposalService;
import com.kdt.team04.domain.security.WithMockJwtAuthentication;

@WithMockJwtAuthentication
@WebMvcTest({MatchProposalController.class, WebSecurityConfig.class})
class MatchProposalControllerFailTest {

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
	final Long DEFAULT_TEAM_ID = 1L;
	final Long DEFAULT_PROPOSAL_ID = 1L;
	final Long DEFAULT_MATCH_ID = 1L;

	@Test
	@DisplayName("대결 신청 request의 content가 blank 일 경우, 상태 코드 400을 반환한다.")
	void propose_fail_1() throws Exception {
		//given
		CreateProposalRequest request = new CreateProposalRequest(DEFAULT_TEAM_ID, " ");

		ErrorCode errorCode = ErrorCode.METHOD_ARGUMENT_NOT_VALID;
		String response = objectMapper.writeValueAsString(new ErrorResponse<>(errorCode));

		//when
		ResultActions resultActions = mockMvc.perform(
			post(BASE_END_POINT + "/{matchId}/proposals", DEFAULT_MATCH_ID)
				.content(objectMapper.writeValueAsString(request))
				.contentType(MediaType.APPLICATION_JSON)
		).andDo(print());

		//then
		resultActions
			.andExpect(status().isBadRequest())
			.andExpect(content().string(response));
	}

	@Test
	@DisplayName("대결 신청 request의 content의 size가 2보다 작을 경우, 상태 코드 400을 반환한다.")
	void propose_fail_2() throws Exception {
		//given
		CreateProposalRequest request = new CreateProposalRequest(DEFAULT_TEAM_ID, "ㄱ");

		ErrorCode errorCode = ErrorCode.METHOD_ARGUMENT_NOT_VALID;
		String response = objectMapper.writeValueAsString(new ErrorResponse<>(errorCode));

		//when
		ResultActions resultActions = mockMvc.perform(
			post(BASE_END_POINT + "/{matchId}/proposals", DEFAULT_MATCH_ID)
				.content(objectMapper.writeValueAsString(request))
				.contentType(MediaType.APPLICATION_JSON)
		).andDo(print());

		//then
		resultActions
			.andExpect(status().isBadRequest())
			.andExpect(content().string(response));
	}

	@Test
	@DisplayName("대결 신청 request의 content의 size가 30보다 클 경우, 상태 코드 400을 반환한다.")
	void propose_fail_3() throws Exception {
		//given
		CreateProposalRequest request = new CreateProposalRequest(DEFAULT_TEAM_ID,
			"대결 신청의 Request의 content가 30자 이상일 경우 bad request, 400 이 반환됩니다. ");

		ErrorCode errorCode = ErrorCode.METHOD_ARGUMENT_NOT_VALID;
		String response = objectMapper.writeValueAsString(new ErrorResponse<>(errorCode));

		//when
		ResultActions resultActions = mockMvc.perform(
			post(BASE_END_POINT + "/{matchId}/proposals", DEFAULT_MATCH_ID)
				.content(objectMapper.writeValueAsString(request))
				.contentType(MediaType.APPLICATION_JSON)
		).andDo(print());

		//then
		resultActions
			.andExpect(status().isBadRequest())
			.andExpect(content().string(response));
	}

	@Test
	@DisplayName("대결 신청을 수락 또는 거절 할 때 request의 status가 null일 경우, 400 상태코드를 반환한다.")
	void proposeApproveReact_fail_1() throws Exception {
		//give
		ReactProposalRequest request = new ReactProposalRequest(null);

		ErrorCode errorCode = ErrorCode.METHOD_ARGUMENT_NOT_VALID;
		String response = objectMapper.writeValueAsString(new ErrorResponse<>(errorCode));

		//when
		ResultActions resultActions = mockMvc.perform(
			patch(BASE_END_POINT + "/{matchId}/proposals/{id}", DEFAULT_MATCH_ID, DEFAULT_PROPOSAL_ID)
				.content(objectMapper.writeValueAsString(request))
				.contentType(MediaType.APPLICATION_JSON)
		).andDo(print());

		//then
		resultActions
			.andExpect(status().isBadRequest())
			.andExpect(content().string(response));
	}

	@Test
	@DisplayName("대결 신청을 수락 또는 거절 할 때 matchId가 null일 경우, 400 상태코드를 반환한다.")
	void proposeApproveReact_fail_2() throws Exception {
		//give
		Long invalidMatchId = null;
		ReactProposalRequest request = new ReactProposalRequest(MatchProposalStatus.WAITING);

		ErrorCode errorCode = ErrorCode.METHOD_ARGUMENT_TYPE_MISMATCH_EXCEPTION;
		String response = objectMapper.writeValueAsString(new ErrorResponse<>(errorCode));

		//when
		ResultActions resultActions = mockMvc.perform(
			patch(BASE_END_POINT + "/" + null + "/proposals/{id}", DEFAULT_PROPOSAL_ID)
				.content(objectMapper.writeValueAsString(request))
				.contentType(MediaType.APPLICATION_JSON)
		).andDo(print());

		//then
		resultActions
			.andExpect(status().isBadRequest())
			.andExpect(content().string(response));
	}

	@Test
	@DisplayName("대결 신청을 수락 또는 거절 할 때 proposalId가 null일 경우, 400 상태코드를 반환한다.")
	void proposeApproveReact_fail_3() throws Exception {
		//give
		Long invalidProposalId = null;
		ReactProposalRequest request = new ReactProposalRequest(MatchProposalStatus.WAITING);

		ErrorCode errorCode = ErrorCode.METHOD_ARGUMENT_TYPE_MISMATCH_EXCEPTION;
		String response = objectMapper.writeValueAsString(new ErrorResponse<>(errorCode));

		//when
		ResultActions resultActions = mockMvc.perform(
			patch(BASE_END_POINT + "/{matchId}/proposals/" + null, DEFAULT_MATCH_ID, invalidProposalId)
				.content(objectMapper.writeValueAsString(request))
				.contentType(MediaType.APPLICATION_JSON)
		).andDo(print());

		//then
		resultActions
			.andExpect(status().isBadRequest())
			.andExpect(content().string(response));
	}

	@Test
	@DisplayName("해당 대결의 신청 목록을 조회할 때 matchId가 null일 경우, 상태 코드 400을 반환한다.")
	void findAllChats_fail_1() throws Exception {
		//given
		ErrorCode errorCode = ErrorCode.METHOD_ARGUMENT_TYPE_MISMATCH_EXCEPTION;
		String response = objectMapper.writeValueAsString(new ErrorResponse<>(errorCode));

		//when
		ResultActions resultActions = mockMvc.perform(
			get(BASE_END_POINT + "/" + null + "/proposals")
				.contentType(MediaType.APPLICATION_JSON)
		).andDo(print());

		//then
		resultActions
			.andExpect(status().isBadRequest())
			.andExpect(content().string(response));
	}

	@Test
	@DisplayName("매칭 신청을 조회할 때 proposalId가 null일 경우, 상태 코드 400을 반환한다.")
	void getProposalById_fail_1() throws Exception {
		//given
		ErrorCode errorCode = ErrorCode.METHOD_ARGUMENT_TYPE_MISMATCH_EXCEPTION;
		String response = objectMapper.writeValueAsString(new ErrorResponse<>(errorCode));

		//when
		ResultActions resultActions = mockMvc.perform(
			get(BASE_END_POINT + "/proposals/" + null)
		).andDo(print());

		//then
		resultActions
			.andExpect(status().isBadRequest())
			.andExpect(content().string(response));
	}
}
