package com.kdt.team04.domain.matches.match.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.LongStream;

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
import com.kdt.team04.common.PageDto;
import com.kdt.team04.common.exception.BusinessException;
import com.kdt.team04.common.exception.ErrorCode;
import com.kdt.team04.common.exception.ErrorResponse;
import com.kdt.team04.common.security.WebSecurityConfig;
import com.kdt.team04.common.security.jwt.Jwt;
import com.kdt.team04.domain.auth.service.TokenService;
import com.kdt.team04.domain.matches.match.dto.MatchPagingCursor;
import com.kdt.team04.domain.matches.match.dto.request.CreateMatchRequest;
import com.kdt.team04.domain.matches.match.dto.request.UpdateMatchStatusRequest;
import com.kdt.team04.domain.matches.match.dto.response.MatchListViewResponse;
import com.kdt.team04.domain.matches.match.dto.response.MatchResponse;
import com.kdt.team04.domain.matches.match.model.MatchStatus;
import com.kdt.team04.domain.matches.match.model.MatchType;
import com.kdt.team04.domain.matches.match.service.MatchService;
import com.kdt.team04.domain.security.WithMockJwtAuthentication;
import com.kdt.team04.domain.teams.team.model.SportsCategory;
import com.kdt.team04.domain.user.dto.response.AuthorResponse;
import com.kdt.team04.domain.user.entity.Location;

@WithMockJwtAuthentication
@WebMvcTest({MatchController.class, WebSecurityConfig.class})
class MatchControllerTest {

	private final static String BASE_END_POINT = "/api/matches";
	private final static String DEFAULT_AUTH_NICK_NAME = "test001";
	private final static Long DEFAULT_AUTH_ID = 1L;
	private final static Long DEFAULT_TEAM_ID = 2L;
	private final static Long DEFAULT_MATCH_ID = 3L;
	private final static Long DEFAULT_INVALID_MATCH_ID = -999L;

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@MockBean
	MatchService matchService;

	@MockBean
	TokenService tokenService;

	@MockBean
	Jwt jwt;

	@Test
	@DisplayName("사용자는 개인전 매치 공고글을 작성 할 수 있으며 200 상태코드를 반환한다.")
	void createIndividualMatch() throws Exception {
		// given
		CreateMatchRequest request = new CreateMatchRequest("배드민턴 뜨자", LocalDate.now(), MatchType.INDIVIDUAL_MATCH,
			null, 1, SportsCategory.BADMINTON, "배드민턴 재밌게 함 치실분 ~?");

		String response = objectMapper.writeValueAsString(new ApiResponse<>(DEFAULT_MATCH_ID));

		given(matchService.create(DEFAULT_AUTH_ID, request)).willReturn(DEFAULT_MATCH_ID);

		// when
		ResultActions resultActions = mockMvc.perform(
			post(BASE_END_POINT)
				.content(objectMapper.writeValueAsString(request))
				.contentType(MediaType.APPLICATION_JSON)
		).andDo(print());

		// then
		verify(matchService, times(1)).create(DEFAULT_AUTH_ID, request);

		resultActions
			.andExpect(status().isOk())
			.andExpect(content().string(response));
	}

	@Test
	@DisplayName("사용자는 팀 매치 공고글을 작성 할 수 있으며 200 상태코드를 반환한다.")
	void createTeamMatch() throws Exception {
		// given
		CreateMatchRequest request = new CreateMatchRequest("축구뜨자", LocalDate.now(),
			MatchType.INDIVIDUAL_MATCH, DEFAULT_TEAM_ID, 11, SportsCategory.SOCCER, "축구 재밌게 함 치실분 ~?");

		String response = objectMapper.writeValueAsString(new ApiResponse<>(DEFAULT_MATCH_ID));

		given(matchService.create(DEFAULT_AUTH_ID, request)).willReturn(DEFAULT_MATCH_ID);

		// when
		ResultActions resultActions = mockMvc.perform(
			post(BASE_END_POINT)
				.content(objectMapper.writeValueAsString(request))
				.contentType(MediaType.APPLICATION_JSON)
		).andDo(print());

		// then
		verify(matchService, times(1)).create(DEFAULT_AUTH_ID, request);

		resultActions
			.andExpect(status().isOk())
			.andExpect(content().string(response));
	}

	@Test
	@DisplayName("팀전 매치 글 작성 시 팀 정보가 없다면 400 상태코드를 반환한다.")
	void createTeamMatchFail() throws Exception {
		// given
		ErrorCode errorCode = ErrorCode.METHOD_ARGUMENT_NOT_VALID;
		String response = objectMapper.writeValueAsString(new ErrorResponse<>(errorCode));

		CreateMatchRequest request = new CreateMatchRequest("제목", LocalDate.now(), MatchType.TEAM_MATCH, null,
			1, SportsCategory.BADMINTON, "배드민턴 재밌게 함 치실분 ~?");

		given(matchService.create(DEFAULT_AUTH_ID, request))
			.willThrow(new BusinessException(ErrorCode.METHOD_ARGUMENT_NOT_VALID, "teamId is null"));

		// when
		ResultActions resultActions = mockMvc.perform(
			post(BASE_END_POINT)
				.content(objectMapper.writeValueAsString(request))
				.contentType(MediaType.APPLICATION_JSON)
		).andDo(print());

		// then
		verify(matchService, times(1)).create(DEFAULT_AUTH_ID, request);

		resultActions.
			andExpect(status().isBadRequest())
			.andExpect(content().string(response));
	}

	@Test
	@DisplayName("매치 신청 시 요청값이 null 이라면 400 상태코드를 반환한다.")
	void createMatchRequestNull() throws Exception {
		// given
		ErrorCode errorCode = ErrorCode.UNACCEPTABLE_JSON_ERROR;
		String response = objectMapper.writeValueAsString(new ErrorResponse<>(errorCode));

		CreateMatchRequest request = null;

		// when
		ResultActions resultActions = mockMvc
			.perform(
				post(BASE_END_POINT)
					.content(objectMapper.writeValueAsString(request))
					.contentType(MediaType.APPLICATION_JSON)
			).andDo(print());;

		// then
		verify(matchService, never()).create(DEFAULT_AUTH_ID, request);

		resultActions
			.andExpect(status().isBadRequest())
			.andExpect(content().string(response));
	}

	@Test
	@DisplayName("매치 생성 시 요청값 데이터가 요구사항에 맞지 않는다면 400 상태코드를 반환한다.")
	void createMatchInvalidParameter() throws Exception {
		// given
		ErrorCode errorCode = ErrorCode.METHOD_ARGUMENT_NOT_VALID;
		String response = objectMapper.writeValueAsString(new ErrorResponse<>(errorCode));

		CreateMatchRequest request = new CreateMatchRequest("", LocalDate.now(), MatchType.INDIVIDUAL_MATCH,
			null, 1, SportsCategory.BADMINTON, "배드민턴 재밌게 함 치실분 ~?");

		// when
		ResultActions resultActions = mockMvc.perform(
			post(BASE_END_POINT)
				.content(objectMapper.writeValueAsString(request))
				.contentType(MediaType.APPLICATION_JSON)
		).andDo(print());

		// then
		verify(matchService, never()).create(DEFAULT_AUTH_ID, request);

		resultActions
			.andExpect(status().isBadRequest())
			.andExpect(content().string(response));
	}

	@Test
	@DisplayName("매치 공고 조회를 원하는 갯수만큼 조회할 수 있으며 조회 후 200 상태코드를 반환한다. ")
	void getMatchesPagingCursor() throws Exception {
		// given
		PageDto.MatchCursorPageRequest request = PageDto.MatchCursorPageRequest.builder()
			.size(5)
			.build();

		List<MatchListViewResponse> matchListViewResponses = LongStream.range(1, 6)
			.mapToObj(index -> new MatchListViewResponse(
				index,
				"title" + index,
				SportsCategory.SOCCER,
				MatchType.INDIVIDUAL_MATCH,
				"content" + index,
				DEFAULT_AUTH_ID,
				DEFAULT_AUTH_NICK_NAME,
				30.0,
				LocalDate.now(),
				LocalDateTime.now()
			))
			.toList();

		MatchPagingCursor cursor = new MatchPagingCursor(LocalDateTime.now(), 5L);

		PageDto.CursorResponse<MatchListViewResponse, MatchPagingCursor> cursorResponse =
			new PageDto.CursorResponse(matchListViewResponses, true, cursor);

		ApiResponse<PageDto.CursorResponse<MatchListViewResponse, MatchPagingCursor>> apiResponse =
			new ApiResponse<>(cursorResponse);

		String response = objectMapper.writeValueAsString(apiResponse);

		given(matchService.findMatches(anyLong(), any())).willReturn(cursorResponse);

		// when
		ResultActions resultActions = mockMvc
			.perform(
				get(BASE_END_POINT + "?size={size}", request.getSize())
			).andDo(print());

		// then
		verify(matchService, times(1))
			.findMatches(anyLong(), any());

		resultActions
			.andExpect(status().isOk())
			.andExpect(content().string(response));
	}

	@Test
	@DisplayName("매치 공고 조회 시 가져올 갯수를 입력하지 않으면 400 상태코드를 반환한다.")
	void getMatchesPagingNotSize() throws Exception {
		// given
		ErrorCode errorCode = ErrorCode.BIND_ERROR;
		String response = objectMapper.writeValueAsString(new ErrorResponse<>(errorCode));

		// when
		ResultActions resultActions = mockMvc.perform(get(BASE_END_POINT))
			.andDo(print());

		// then
		verify(matchService, never())
			.findMatches(null, null);

		resultActions
			.andExpect(status().isBadRequest())
			.andExpect(content().string(response));
	}

	@Test
	@DisplayName("매치공고가 존재할 때 공고의 상세정보를 조회 후 200 상태코드를 반환한다.")
	void getMatchById() throws Exception {
		// given
		AuthorResponse authorResponse = new AuthorResponse(DEFAULT_AUTH_ID, DEFAULT_AUTH_NICK_NAME, null);
		MatchResponse matchResponse = MatchResponse.builder()
			.id(DEFAULT_MATCH_ID)
			.title("title1")
			.status(MatchStatus.WAITING)
			.sportsCategory(SportsCategory.BADMINTON)
			.participants(1)
			.author(authorResponse)
			.team(null)
			.matchDate(LocalDate.now())
			.matchType(MatchType.INDIVIDUAL_MATCH)
			.content("content1")
			.proposer(null)
			.location(new Location(45.0, 45.0))
			.build();

		String response = objectMapper.writeValueAsString(new ApiResponse<>(matchResponse));

		given(matchService.findById(DEFAULT_MATCH_ID, DEFAULT_AUTH_ID)).willReturn(matchResponse);

		// when
		ResultActions resultActions = mockMvc
			.perform(
				get(BASE_END_POINT + "/" + DEFAULT_MATCH_ID)
					.contentType(MediaType.APPLICATION_JSON))
			.andDo(print());

		// then
		verify(matchService, times(1)).findById(DEFAULT_MATCH_ID, DEFAULT_AUTH_ID);

		resultActions
			.andExpect(status().isOk())
			.andExpect(content().string(response));
	}

	@Test
	@DisplayName("매치공고가 존재하지 않을 때 404 상태코드를 반환한다.")
	void getMatchByIdNotFound() throws Exception {
		// given
		ErrorCode errorCode = ErrorCode.MATCH_NOT_FOUND;
		String response = objectMapper.writeValueAsString(new ErrorResponse<>(errorCode));

		given(matchService.findById(DEFAULT_INVALID_MATCH_ID, DEFAULT_AUTH_ID))
			.willThrow(new BusinessException(errorCode));

		// when
		ResultActions resultActions = mockMvc
			.perform(
				get(BASE_END_POINT + "/" + DEFAULT_INVALID_MATCH_ID)
					.contentType(MediaType.APPLICATION_JSON))
			.andDo(print());

		// then
		verify(matchService, times(1))
			.findById(DEFAULT_INVALID_MATCH_ID, DEFAULT_AUTH_ID);

		resultActions
			.andExpect(status().isNotFound())
			.andExpect(content().string(response));
	}

	@Test
	@DisplayName("공고 작성자는 공고글을 삭제할 수 있으며 삭제 후 200 상태코드를 반환합니다.")
	void deleteMatch() throws Exception {
		// given
		willDoNothing().given(matchService).delete(DEFAULT_AUTH_ID, DEFAULT_MATCH_ID);

		// when
		ResultActions resultActions = mockMvc
			.perform(
				delete(BASE_END_POINT + "/" + DEFAULT_MATCH_ID))
			.andDo(print());

		// then
		resultActions.andExpect(status().isOk());
	}

	@Test
	@DisplayName("다른 사용자가 공고 삭제를 시도 할 경우 403 상태코드를 반환한다.")
	void accessDeniedMatch() throws Exception {
		// given
		ErrorCode errorCode = ErrorCode.MATCH_ACCESS_DENIED;
		String response = objectMapper.writeValueAsString(new ErrorResponse<>(errorCode));

		doThrow(new BusinessException(errorCode)).when(matchService)
			.delete(DEFAULT_AUTH_ID, DEFAULT_INVALID_MATCH_ID);

		// when
		ResultActions resultActions = mockMvc
			.perform(
				delete(BASE_END_POINT + "/" + DEFAULT_INVALID_MATCH_ID))
			.andDo(print());

		// then
		verify(matchService, times(1))
			.delete(DEFAULT_AUTH_ID, DEFAULT_INVALID_MATCH_ID);

		resultActions
			.andExpect(status().isForbidden())
			.andExpect(content().string(response));
	}

	@Test
	@DisplayName("매치상태가 WAITING 이 아니라면 삭제할 수 없으며 400 상태코드를 반환한다.")
	void invalidStatusDelete() throws Exception {
		// given
		ErrorCode errorCode = ErrorCode.MATCH_INVALID_DELETE_REQUEST;
		String response = objectMapper.writeValueAsString(new ErrorResponse<>(errorCode));

		doThrow(new BusinessException(errorCode)).when(matchService)
			.delete(DEFAULT_AUTH_ID, DEFAULT_INVALID_MATCH_ID);

		// when
		ResultActions resultActions = mockMvc
			.perform(
				delete(BASE_END_POINT + "/" + DEFAULT_INVALID_MATCH_ID))
			.andDo(print());

		// then
		verify(matchService, times(1))
			.delete(DEFAULT_AUTH_ID, DEFAULT_INVALID_MATCH_ID);

		resultActions
			.andExpect(status().isBadRequest())
			.andExpect(content().string(response));
	}

	@Test
	@DisplayName("공고 작성자는 매치 모집 상태를 변경 할 수 있으며 200 상태코드를 반환한다.")
	void updateStatus() throws Exception {
		// given
		UpdateMatchStatusRequest updateRequest = new UpdateMatchStatusRequest(MatchStatus.IN_GAME);
		String request = objectMapper.writeValueAsString(updateRequest);

		willDoNothing().given(matchService)
			.updateStatusExceptEnd(DEFAULT_MATCH_ID, DEFAULT_AUTH_ID, updateRequest.status());

		// when
		ResultActions resultActions = mockMvc
			.perform(
				patch(BASE_END_POINT + "/" + DEFAULT_MATCH_ID)
					.content(request)
					.contentType(MediaType.APPLICATION_JSON))
			.andDo(print());

		// then
		verify(matchService, times(1))
			.updateStatusExceptEnd(DEFAULT_MATCH_ID, DEFAULT_AUTH_ID, updateRequest.status());

		resultActions
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("공고 작성자가 아니라면 모집 상태를 변경할 수 없으며 403 상태코드를 반환한다.")
	void accessDeniedMatchStatus() throws Exception {
		// given
		UpdateMatchStatusRequest updateRequest = new UpdateMatchStatusRequest(MatchStatus.IN_GAME);
		String request = objectMapper.writeValueAsString(updateRequest);

		ErrorCode errorCode = ErrorCode.MATCH_ACCESS_DENIED;
		String response = objectMapper.writeValueAsString(new ErrorResponse<>(errorCode));

		doThrow(new BusinessException(errorCode)).when(matchService)
			.updateStatusExceptEnd(DEFAULT_INVALID_MATCH_ID, DEFAULT_AUTH_ID, updateRequest.status());

		// when
		ResultActions resultActions = mockMvc
			.perform(
				patch(BASE_END_POINT + "/" + DEFAULT_INVALID_MATCH_ID)
					.content(request)
					.contentType(MediaType.APPLICATION_JSON))
			.andDo(print());

		// then
		verify(matchService, times(1))
			.updateStatusExceptEnd(DEFAULT_INVALID_MATCH_ID, DEFAULT_AUTH_ID, updateRequest.status());

		resultActions
			.andExpect(status().isForbidden())
			.andExpect(content().string(response));
	}

	@Test
	@DisplayName("상태 변경 요청값이 END일 경우 변경할 수 없으며 400 상태코드를 반환합니다.")
	void endUpdateStatus() throws Exception {
		// given
		UpdateMatchStatusRequest updateRequest = new UpdateMatchStatusRequest(MatchStatus.IN_GAME);
		String request = objectMapper.writeValueAsString(updateRequest);

		ErrorCode errorCode = ErrorCode.MATCH_CANNOT_UPDATE_END;
		String response = objectMapper.writeValueAsString(new ErrorResponse<>(errorCode));

		doThrow(new BusinessException(errorCode)).when(matchService)
			.updateStatusExceptEnd(DEFAULT_MATCH_ID, DEFAULT_AUTH_ID, updateRequest.status());

		// when
		ResultActions resultActions = mockMvc
			.perform(
				patch(BASE_END_POINT + "/" + DEFAULT_MATCH_ID)
					.content(request)
					.contentType(MediaType.APPLICATION_JSON))
			.andDo(print());

		// then
		verify(matchService, times(1))
			.updateStatusExceptEnd(DEFAULT_MATCH_ID, DEFAULT_AUTH_ID, updateRequest.status());

		resultActions
			.andExpect(status().isBadRequest())
			.andExpect(content().string(response));
	}

	@Test
	@DisplayName("공고상태 변경 요청이 현재 상태와 동일하다면 400 상태코드를 반환한다.")
	void alreadyUpdateStatus() throws Exception {
		// given
		UpdateMatchStatusRequest updateRequest = new UpdateMatchStatusRequest(MatchStatus.IN_GAME);
		String request = objectMapper.writeValueAsString(updateRequest);

		ErrorCode errorCode = ErrorCode.MATCH_ALREADY_CHANGED_STATUS;
		String response = objectMapper.writeValueAsString(new ErrorResponse<>(errorCode));

		doThrow(new BusinessException(errorCode)).when(matchService)
			.updateStatusExceptEnd(DEFAULT_MATCH_ID, DEFAULT_AUTH_ID, updateRequest.status());

		// when
		ResultActions resultActions = mockMvc
			.perform(
				patch(BASE_END_POINT + "/" + DEFAULT_MATCH_ID)
					.content(request)
					.contentType(MediaType.APPLICATION_JSON))
			.andDo(print());

		// then
		verify(matchService, times(1))
			.updateStatusExceptEnd(DEFAULT_MATCH_ID, DEFAULT_AUTH_ID, updateRequest.status());

		resultActions
			.andExpect(status().isBadRequest())
			.andExpect(content().string(response));
	}

	@Test
	@DisplayName("이미 경기가 끝난 공고라면 상태를 변경할 수 없으며 400 상태코드를 반환한다.")
	void alreadyEndStatus() throws Exception {
		// given
		UpdateMatchStatusRequest updateRequest = new UpdateMatchStatusRequest(MatchStatus.WAITING);
		String request = objectMapper.writeValueAsString(updateRequest);

		ErrorCode errorCode = ErrorCode.MATCH_ENDED;
		String response = objectMapper.writeValueAsString(new ErrorResponse<>(errorCode));

		doThrow(new BusinessException(errorCode)).when(matchService)
			.updateStatusExceptEnd(DEFAULT_MATCH_ID, DEFAULT_AUTH_ID, updateRequest.status());

		// when
		ResultActions resultActions = mockMvc
			.perform(
				patch(BASE_END_POINT + "/" + DEFAULT_MATCH_ID)
					.content(request)
					.contentType(MediaType.APPLICATION_JSON))
			.andDo(print());

		// then
		verify(matchService, times(1))
			.updateStatusExceptEnd(DEFAULT_MATCH_ID, DEFAULT_AUTH_ID, updateRequest.status());

		resultActions
			.andExpect(status().isBadRequest())
			.andExpect(content().string(response));
	}

}
