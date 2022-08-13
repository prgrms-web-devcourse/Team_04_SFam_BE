package com.kdt.team04.domain.matches.review.controller;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
import com.kdt.team04.common.exception.BusinessException;
import com.kdt.team04.common.exception.ErrorCode;
import com.kdt.team04.common.exception.ErrorResponse;
import com.kdt.team04.common.security.WebSecurityConfig;
import com.kdt.team04.common.security.jwt.Jwt;
import com.kdt.team04.domain.auth.service.TokenService;
import com.kdt.team04.domain.matches.review.dto.request.CreateMatchReviewRequest;
import com.kdt.team04.domain.matches.review.model.MatchReviewValue;
import com.kdt.team04.domain.matches.review.service.MatchReviewService;
import com.kdt.team04.domain.security.WithMockJwtAuthentication;

@WithMockJwtAuthentication
@WebMvcTest({MatchReviewController.class, WebSecurityConfig.class})
class MatchReviewControllerTest {

	private final static String BASE_END_POINT = "/api/matches/{matchId}/review";
	private final static Long DEFAULT_AUTH_ID = 1L;
	private final static Long DEFAULT_MATCH_ID = 2L;
	private final static Long INVALID_MATCH_ID = 3L;
	private final static Long ALREADY_MATCH_EHD_ID = 4L;
	private final static Long DEFAULT_NOT_END_MATCH_ID = 5L;
	private final static Long DEFAULT_REVIEW_ID = 6L;

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@MockBean
	MatchReviewService matchReviewService;

	@MockBean
	TokenService tokenService;

	@MockBean
	Jwt jwt;

	@Test
	@DisplayName("경기가 완료된 매치라면 후기를 등록할 수 있으며 등록 후 200 상태코드를 반환한다.")
	void reviewSuccess() throws Exception {
		// given
		CreateMatchReviewRequest matchReviewRequest = new CreateMatchReviewRequest(MatchReviewValue.BEST);
		String request = objectMapper.writeValueAsString(matchReviewRequest);

		given(matchReviewService.review(DEFAULT_MATCH_ID, matchReviewRequest.review(), DEFAULT_AUTH_ID))
			.willReturn(DEFAULT_REVIEW_ID);

		// when
		ResultActions resultActions = mockMvc
			.perform(
				post(BASE_END_POINT, DEFAULT_MATCH_ID)
					.contentType(MediaType.APPLICATION_JSON)
					.content(request)
			)
			.andDo(print());

		// then
		verify(matchReviewService, times(1))
			.review(DEFAULT_MATCH_ID, matchReviewRequest.review(), DEFAULT_AUTH_ID);

		resultActions
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("후기를 등록에 필요한 값이 없으면 400 상태코드를 반환한다.")
	void reviewNotArgument() throws Exception {
		// given
		ErrorCode errorCode = ErrorCode.RUNTIME_EXCEPTION;
		String response = objectMapper.writeValueAsString(new ErrorResponse<>(errorCode));

		// when
		ResultActions resultActions = mockMvc
			.perform(
				post(BASE_END_POINT, DEFAULT_MATCH_ID)
			)
			.andDo(print());

		// then
		verify(matchReviewService, never())
			.review(null, null, null);

		resultActions
			.andExpect(status().isBadRequest())
			.andExpect(content().string(response));
	}

	@Test
	@DisplayName("후기를 등록에 필요한 값이 null 이면 400 상태코드를 반환한다.")
	void reviewArgumentNull() throws Exception {
		// given
		CreateMatchReviewRequest matchReviewRequest = new CreateMatchReviewRequest(null);
		String request = objectMapper.writeValueAsString(matchReviewRequest);

		ErrorCode errorCode = ErrorCode.METHOD_ARGUMENT_NOT_VALID;
		String response = objectMapper.writeValueAsString(new ErrorResponse<>(errorCode));

		// when
		ResultActions resultActions = mockMvc
			.perform(
				post(BASE_END_POINT, DEFAULT_MATCH_ID)
					.contentType(MediaType.APPLICATION_JSON)
					.content(request)
			)
			.andDo(print());

		// then
		verify(matchReviewService, never())
			.review(null, null, null);

		resultActions
			.andExpect(status().isBadRequest())
			.andExpect(content().string(response));
	}


	@Test
	@DisplayName("매치 상태가 END가 아니라면 후기를 등록할 수 없으며 400 상태코드를 반환한다.")
	void matchNotEnd() throws Exception {
		// given
		CreateMatchReviewRequest matchReviewRequest = new CreateMatchReviewRequest(MatchReviewValue.BEST);
		String request = objectMapper.writeValueAsString(matchReviewRequest);

		ErrorCode errorCode = ErrorCode.MATCH_NOT_ENDED;
		String response = objectMapper.writeValueAsString(new ErrorResponse<>(errorCode));

		given(matchReviewService.review(DEFAULT_NOT_END_MATCH_ID, matchReviewRequest.review(), DEFAULT_AUTH_ID))
			.willThrow(new BusinessException(errorCode));

		// when
		ResultActions resultActions = mockMvc
			.perform(
				post(BASE_END_POINT, DEFAULT_NOT_END_MATCH_ID)
					.contentType(MediaType.APPLICATION_JSON)
					.content(request)
			)
			.andDo(print());

		// then
		verify(matchReviewService, times(1))
			.review(DEFAULT_NOT_END_MATCH_ID, matchReviewRequest.review(), DEFAULT_AUTH_ID);

		resultActions
			.andExpect(status().isBadRequest())
			.andExpect(content().string(response));
	}

	@Test
	@DisplayName("매치 대상자가 아니면 후기를 등록 할 수 없으며 403 상태코드를 반환한다.")
	void reviewAccessDenied() throws Exception {
		// given
		CreateMatchReviewRequest matchReviewRequest = new CreateMatchReviewRequest(MatchReviewValue.BEST);
		String request = objectMapper.writeValueAsString(matchReviewRequest);

		ErrorCode errorCode = ErrorCode.MATCH_ACCESS_DENIED;
		String response = objectMapper.writeValueAsString(new ErrorResponse<>(errorCode));

		given(matchReviewService.review(INVALID_MATCH_ID, matchReviewRequest.review(), DEFAULT_AUTH_ID))
			.willThrow(new BusinessException(errorCode));

		// when
		ResultActions resultActions = mockMvc
			.perform(
				post(BASE_END_POINT, INVALID_MATCH_ID)
					.contentType(MediaType.APPLICATION_JSON)
					.content(request)
			)
			.andDo(print());

		// then
		verify(matchReviewService, times(1))
			.review(INVALID_MATCH_ID, matchReviewRequest.review(), DEFAULT_AUTH_ID);

		resultActions
			.andExpect(status().isForbidden())
			.andExpect(content().string(response));
	}

	@Test
	@DisplayName("이미 후기가 있다면 등록 할 수 없으며 400 상태코드를 반환한다.")
	void alreadyReview() throws Exception {
		// given
		CreateMatchReviewRequest matchReviewRequest = new CreateMatchReviewRequest(MatchReviewValue.BEST);
		String request = objectMapper.writeValueAsString(matchReviewRequest);

		ErrorCode errorCode = ErrorCode.MATCH_REVIEW_ALREADY_EXISTS;
		String response = objectMapper.writeValueAsString(new ErrorResponse<>(errorCode));

		given(matchReviewService.review(ALREADY_MATCH_EHD_ID, matchReviewRequest.review(), DEFAULT_AUTH_ID))
			.willThrow(new BusinessException(errorCode));

		// when
		ResultActions resultActions = mockMvc
			.perform(
				post(BASE_END_POINT, ALREADY_MATCH_EHD_ID)
					.contentType(MediaType.APPLICATION_JSON)
					.content(request)
			)
			.andDo(print());

		// then
		verify(matchReviewService, times(1))
			.review(ALREADY_MATCH_EHD_ID, matchReviewRequest.review(), DEFAULT_AUTH_ID);

		resultActions
			.andExpect(status().isBadRequest())
			.andExpect(content().string(response));
	}

}