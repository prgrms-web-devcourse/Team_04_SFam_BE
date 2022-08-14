package com.kdt.team04.domain.matches.review.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import com.kdt.team04.common.ApiResponse;
import com.kdt.team04.common.exception.ErrorCode;
import com.kdt.team04.common.exception.ErrorResponse;
import com.kdt.team04.common.security.WebSecurityConfig;
import com.kdt.team04.common.security.jwt.Jwt;
import com.kdt.team04.domain.auth.service.TokenService;
import com.kdt.team04.domain.matches.review.dto.request.CreateMatchRecordRequest;
import com.kdt.team04.domain.matches.review.dto.response.MatchRecordTotalResponse;
import com.kdt.team04.domain.matches.review.model.MatchRecordValue;
import com.kdt.team04.domain.matches.review.service.MatchRecordService;
import com.kdt.team04.domain.security.WithMockJwtAuthentication;
import com.kdt.team04.domain.teams.team.model.SportsCategory;

@WithMockJwtAuthentication
@WebMvcTest({MatchRecordController.class, WebSecurityConfig.class})
class MatchRecordControllerTest {

	private final static String BASE_END_POINT = "/api/matches";
	private final static Long DEFAULT_AUTH_ID = 1L;
	private final static Long DEFAULT_MATCH_PROPOSAL_ID = 2L;
	private final static Long DEFAULT_MATCH_ID = 3L;

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@MockBean
	MatchRecordService matchRecordService;

	@MockBean
	TokenService tokenService;

	@MockBean
	Jwt jwt;

	@Test
	@DisplayName("공고 작성자는 경기결과를 등록할 수 있으며 등록 후 200 상태코드를 반환한다.")
	void endGameAuthor() throws Exception {
		// given
		CreateMatchRecordRequest recordRequest =
			new CreateMatchRecordRequest(DEFAULT_MATCH_PROPOSAL_ID, MatchRecordValue.WIN);
		String request = objectMapper.writeValueAsString(recordRequest);

		willDoNothing().given(matchRecordService)
			.endGame(DEFAULT_MATCH_ID, recordRequest.proposalId(), recordRequest.result(), DEFAULT_AUTH_ID);

		// when
		ResultActions resultActions = mockMvc
			.perform(
				post(BASE_END_POINT + "/" + DEFAULT_MATCH_ID + "/records")
					.contentType(MediaType.APPLICATION_JSON)
					.content(request)
			)
			.andDo(print());

		// then
		verify(matchRecordService, times(1))
			.endGame(DEFAULT_MATCH_ID, recordRequest.proposalId(), recordRequest.result(), DEFAULT_AUTH_ID);

		resultActions
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("경기 결과 등록에 필요한 데이터가 없는 경우 400 상태코드를 반환한다.")
	void endGameNotAuthor() throws Exception {
		// given
		ErrorCode errorCode = ErrorCode.UNACCEPTABLE_JSON_ERROR;
		String response = objectMapper.writeValueAsString(new ErrorResponse<>(errorCode));

		// when
		ResultActions resultActions = mockMvc
			.perform(
				post(BASE_END_POINT + "/" + DEFAULT_MATCH_ID + "/records")
			)
			.andDo(print());

		// then
		verify(matchRecordService, never())
			.endGame(null, null, null, null);

		resultActions
			.andExpect(status().isBadRequest())
			.andExpect(content().string(response));
	}

	@Test
	@DisplayName("사용자는 전적을 조회할 수 있으며 조회 후 200 상태코드를 반환한다.")
	void getRecords() throws Exception {
		// given
		QueryMatchRecordRequest recordRequest =
			new QueryMatchRecordRequest(SportsCategory.BADMINTON, DEFAULT_AUTH_ID, null);

		MatchRecordTotalResponse totalResponse =
			new MatchRecordTotalResponse(10, 5, 2);

		String response = objectMapper.writeValueAsString(new ApiResponse<>(totalResponse));

		given(matchRecordService.findMatchRecordTotal(any(QueryMatchRecordRequest.class)))
			.willReturn(totalResponse);

		// when
		ResultActions resultActions = mockMvc
			.perform(
				get(BASE_END_POINT + "/records?"
						+ "sportsCategory={category}"
						+ "&userId={userId}",
					recordRequest.getSportsCategory(), recordRequest.getUserId()
				)
					.contentType(MediaType.APPLICATION_JSON)
			).andDo(print());

		// then
		verify(matchRecordService, times(1))
			.findMatchRecordTotal(any(QueryMatchRecordRequest.class));

		resultActions
			.andExpect(status().isOk())
			.andExpect(content().string(response));
	}

}