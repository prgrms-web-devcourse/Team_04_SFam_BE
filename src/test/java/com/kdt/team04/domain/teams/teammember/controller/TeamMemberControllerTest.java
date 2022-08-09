package com.kdt.team04.domain.teams.teammember.controller;

import static org.mockito.Mockito.doThrow;
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
import com.kdt.team04.domain.security.WithMockJwtAuthentication;
import com.kdt.team04.domain.teams.teammember.dto.request.RegisterTeamMemberRequest;
import com.kdt.team04.domain.teams.teammember.service.TeamMemberService;

@WithMockJwtAuthentication
@WebMvcTest({TeamMemberController.class, WebSecurityConfig.class})
public class TeamMemberControllerTest {

	private final static String BASE_END_POINT = "/api/teams";
	private final static Long DEFAULT_INVITATION_MEMBER_ID = 1L;
	private final static Long DEFAULT_TEAM_ID = 2L;

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@MockBean
	TeamMemberService teamMemberService;

	@MockBean
	TokenService tokenService;

	@MockBean
	Jwt jwt;

	@Test
	@DisplayName("팀원으로 등록될 유저의 아이디가 null일 경우 400 상태코드를 반환한다.")
	void failTeamMemberRegister() throws Exception {
		// given
		RegisterTeamMemberRequest request = null;

		// when
		ResultActions resultActions = mockMvc.perform(
			post(BASE_END_POINT + "/" + DEFAULT_TEAM_ID + "/members")
				.content(objectMapper.writeValueAsString(request))
				.contentType(MediaType.APPLICATION_JSON)
		).andDo(print());

		// then
		verify(teamMemberService, times(0)).registerTeamMember(DEFAULT_TEAM_ID, request);
		resultActions.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("초대 받은 사용자라면 팀원에 등록 후 200 상태코드를 반환한다.")
	void successTeamMemberRegister() throws Exception {
		// given
		RegisterTeamMemberRequest request = new RegisterTeamMemberRequest(DEFAULT_INVITATION_MEMBER_ID);

		// when
		ResultActions resultActions = mockMvc.perform(
			post(BASE_END_POINT + "/" + DEFAULT_TEAM_ID + "/members")
				.content(objectMapper.writeValueAsString(request))
				.contentType(MediaType.APPLICATION_JSON)
		).andDo(print());

		// then
		verify(teamMemberService, times(1)).registerTeamMember(DEFAULT_TEAM_ID, request);
		//verify(teamMemberService, times(1)).registerTeamMember(DEFAULT_TEAM_ID, request);
		resultActions.andExpect(status().isOk());
	}

	@Test
	@DisplayName("이미 팀원이면 ALREADY_TEAM_MEMBER와 400 에러를 반환한다.")
	void registerFailIfAlreadyMember() throws Exception {
		// given
		ErrorCode errorCode = ErrorCode.ALREADY_TEAM_MEMBER;

		RegisterTeamMemberRequest request = new RegisterTeamMemberRequest(DEFAULT_INVITATION_MEMBER_ID);
		ErrorResponse<ErrorCode> response = new ErrorResponse<>(errorCode);

		doThrow(new BusinessException(errorCode)).when(teamMemberService)
			.registerTeamMember(DEFAULT_TEAM_ID, request);

		// when
		ResultActions resultActions = mockMvc.perform(
			post(BASE_END_POINT + "/" + DEFAULT_TEAM_ID + "/members")
				.content(objectMapper.writeValueAsString(request))
				.contentType(MediaType.APPLICATION_JSON)
		).andDo(print());

		// then
		verify(teamMemberService, times(1)).registerTeamMember(DEFAULT_TEAM_ID, request);

		resultActions.andExpect(status().isBadRequest())
			.andExpect(content().string(objectMapper.writeValueAsString(response)));
	}

	@Test
	@DisplayName("초대를 하지 않았다면 팀원으로 등록시 INVALID_TEAM_INVITATION과 400 에러를 반환한다.")
	void registerFailIfNotExistsInvitation() throws Exception {
		// given
		ErrorCode errorCode = ErrorCode.INVALID_TEAM_INVITATION;

		RegisterTeamMemberRequest request = new RegisterTeamMemberRequest(DEFAULT_INVITATION_MEMBER_ID);
		ErrorResponse<ErrorCode> response = new ErrorResponse<>(errorCode);

		doThrow(new BusinessException(errorCode)).when(teamMemberService).registerTeamMember(DEFAULT_TEAM_ID, request);

		// when
		ResultActions resultActions = mockMvc.perform(
			post(BASE_END_POINT + "/" + DEFAULT_TEAM_ID + "/members")
				.content(objectMapper.writeValueAsString(request))
				.contentType(MediaType.APPLICATION_JSON)
		).andDo(print());

		// then
		verify(teamMemberService, times(1)).registerTeamMember(DEFAULT_TEAM_ID, request);

		resultActions.andExpect(status().isBadRequest())
			.andExpect(content().string(objectMapper.writeValueAsString(response)));
	}
}
