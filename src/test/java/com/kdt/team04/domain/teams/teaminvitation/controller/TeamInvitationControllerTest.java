package com.kdt.team04.domain.teams.teaminvitation.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import com.kdt.team04.domain.security.WithMockJwtAuthentication;
import com.kdt.team04.domain.teams.teaminvitation.dto.TeamInvitationCursor;
import com.kdt.team04.domain.teams.teaminvitation.dto.request.TeamInvitationRefuseRequest;
import com.kdt.team04.domain.teams.teaminvitation.dto.request.TeamInvitationRequest;
import com.kdt.team04.domain.teams.teaminvitation.dto.response.TeamInvitationResponse;
import com.kdt.team04.domain.teams.teaminvitation.dto.response.TeamInviteResponse;
import com.kdt.team04.domain.teams.teaminvitation.model.InvitationStatus;
import com.kdt.team04.domain.teams.teaminvitation.service.TeamInvitationService;

@WithMockJwtAuthentication
@WebMvcTest({TeamInvitationController.class, WebSecurityConfig.class})
class TeamInvitationControllerTest {
	private final static String BASE_END_POINT = "/api/teams";
	private final static Long DEFAULT_AUTH_ID = 1L;
	private final static Long DEFAULT_AUTH_TEAM_ID = 2L;
	private final static Long DEFAULT_TARGET_USER_ID = 3L;
	private final static Long DEFAULT_TARGET_USER_TEAM_ID = 4L;
	private final static Long DEFAULT_TEAM_INVITATION_ID = 5L;
	private final static Long DEFAULT_NOT_FOUND_INVITATION_ID = 6L;

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@MockBean
	TeamInvitationService teamInvitationService;

	@MockBean
	TokenService tokenService;

	@MockBean
	Jwt jwt;

	@Test
	@DisplayName("사용자가 원하는 갯수 만큼 초대 목록과 200 상태코드를 반환한다.")
	void getInvites() throws Exception {
		// given
		PageDto.TeamInvitationCursorPageRequest cursorRequest =
			new PageDto.TeamInvitationCursorPageRequest(null, null, 5, InvitationStatus.WAITING);

		List<TeamInvitationResponse> teamInvitationResponses = LongStream.range(1, 6)
			.mapToObj(
				index -> new TeamInvitationResponse(
					index,
					index,
					"team00" + index,
					null,
					LocalDateTime.now()
				)
			).toList();
		boolean hasNext = false;
		TeamInvitationCursor cursor = new TeamInvitationCursor(null, null);

		PageDto.CursorResponse<TeamInvitationResponse, TeamInvitationCursor> pageResponse =
			new PageDto.CursorResponse<>(teamInvitationResponses, hasNext, cursor);
		String response = objectMapper.writeValueAsString(new ApiResponse<>(pageResponse));

		given(teamInvitationService.getInvitations(any(), any()))
			.willReturn(pageResponse);

		// when
		ResultActions resultActions = mockMvc
			.perform(
				get(BASE_END_POINT + "/invitations?"
						+ "size={size}"
						+ "&status={status}",
					cursorRequest.getSize(), cursorRequest.getStatus()
				)
					.contentType(MediaType.APPLICATION_JSON))
			.andDo(print());

		// then
		verify(teamInvitationService, times(1))
			.getInvitations(any(), any());

		resultActions
			.andExpect(status().isOk())
			.andExpect(content().string(response));
	}

	@Test
	@DisplayName("초대목록 조회 시 데이터에 가져올 개수와 상태가 없다면 400 상태코드를 반환한다.")
	void getInvitesArgumentsNull() throws Exception {
		// given
		ErrorCode errorCode = ErrorCode.BIND_ERROR;
		String response = objectMapper.writeValueAsString(new ErrorResponse<>(errorCode));

		// when
		ResultActions resultActions = mockMvc
			.perform(
				get(BASE_END_POINT + "/invitations")
					.contentType(MediaType.APPLICATION_JSON))
			.andDo(print());

		// then
		verify(teamInvitationService, times(0)).getInvitations(DEFAULT_AUTH_ID, null);

		resultActions
			.andExpect(status().isBadRequest())
			.andExpect(content().string(response));
	}

	@Test
	@DisplayName("팀 리더는 팀원을 초대 후 200 상태코드를 반환한다.")
	void invite() throws Exception {
		// given
		TeamInvitationRequest teamInvitationRequest = new TeamInvitationRequest(DEFAULT_TARGET_USER_ID);
		String request = objectMapper.writeValueAsString(teamInvitationRequest);

		TeamInviteResponse teamInviteResponse = new TeamInviteResponse(DEFAULT_TEAM_INVITATION_ID);
		String response = objectMapper.writeValueAsString(new ApiResponse<>(teamInviteResponse));

		given(teamInvitationService.invite(DEFAULT_AUTH_ID, DEFAULT_AUTH_TEAM_ID, teamInvitationRequest.targetUserId()))
			.willReturn(teamInviteResponse);

		// when
		ResultActions resultActions = mockMvc.perform(
			post(BASE_END_POINT + "/{teamId}/invitations", DEFAULT_AUTH_TEAM_ID)
				.contentType(MediaType.APPLICATION_JSON)
				.content(request)
		).andDo(print());

		// then
		verify(teamInvitationService, times(1))
			.invite(DEFAULT_AUTH_ID, DEFAULT_AUTH_TEAM_ID, teamInvitationRequest.targetUserId());

		resultActions
			.andExpect(status().isOk())
			.andExpect(content().string(response));
	}

	@Test
	@DisplayName("팀 초대에 필요한 데이터가 없다면 400 상태코드를 반환한다.")
	void inviteArgumentNull() throws Exception {
		// given
		ErrorCode errorCode = ErrorCode.UNACCEPTABLE_JSON_ERROR;
		String response = objectMapper.writeValueAsString(new ErrorResponse<>(errorCode));

		// when
		ResultActions resultActions = mockMvc.perform(
			post(BASE_END_POINT + "/{teamId}/invitations", DEFAULT_AUTH_TEAM_ID)
				.contentType(MediaType.APPLICATION_JSON)
		).andDo(print());

		// then
		verify(teamInvitationService, never())
			.invite(null, null, null);

		resultActions
			.andExpect(status().isBadRequest())
			.andExpect(content().string(response));
	}

	@Test
	@DisplayName("이미 초대 대기상태나 수락 상태라면 400 상태코드를 반환한다.")
	void alreadyInvited() throws Exception {
		// given
		TeamInvitationRequest teamInvitationRequest = new TeamInvitationRequest(DEFAULT_TARGET_USER_ID);
		String request = objectMapper.writeValueAsString(teamInvitationRequest);

		ErrorCode errorCode = ErrorCode.ALREADY_INVITED_USER;
		String response = objectMapper.writeValueAsString(new ErrorResponse<>(errorCode));
		doThrow(new BusinessException(errorCode))
			.when(teamInvitationService)
			.invite(DEFAULT_AUTH_ID, DEFAULT_AUTH_TEAM_ID, teamInvitationRequest.targetUserId());

		// when
		ResultActions resultActions = mockMvc.perform(
				post(BASE_END_POINT + "/{teamId}/invitations", DEFAULT_AUTH_TEAM_ID)
					.contentType(MediaType.APPLICATION_JSON)
					.content(request))
			.andDo(print());

		// then
		verify(teamInvitationService, times(1))
			.invite(DEFAULT_AUTH_ID, DEFAULT_AUTH_TEAM_ID, teamInvitationRequest.targetUserId());

		resultActions
			.andExpect(status().isBadRequest())
			.andExpect(content().string(response));
	}

	@Test
	@DisplayName("이미 팀의 멤버라면 초대가 불가능하며 400 상태코드를 반환한다.")
	void alreadyTeamMember() throws Exception {
		// given
		TeamInvitationRequest teamInvitationRequest = new TeamInvitationRequest(DEFAULT_TARGET_USER_ID);
		String request = objectMapper.writeValueAsString(teamInvitationRequest);

		ErrorCode errorCode = ErrorCode.ALREADY_TEAM_MEMBER;
		String response = objectMapper.writeValueAsString(new ErrorResponse<>(errorCode));
		doThrow(new BusinessException(errorCode))
			.when(teamInvitationService)
			.invite(DEFAULT_AUTH_ID, DEFAULT_AUTH_TEAM_ID, teamInvitationRequest.targetUserId());

		// when
		ResultActions resultActions = mockMvc.perform(
				post(BASE_END_POINT + "/{teamId}/invitations", DEFAULT_AUTH_TEAM_ID)
					.contentType(MediaType.APPLICATION_JSON)
					.content(request))
			.andDo(print());

		// then
		verify(teamInvitationService, times(1))
			.invite(DEFAULT_AUTH_ID, DEFAULT_AUTH_TEAM_ID, teamInvitationRequest.targetUserId());

		resultActions
			.andExpect(status().isBadRequest())
			.andExpect(content().string(response));
	}

	@Test
	@DisplayName("팀의 리더가 아니라면 초대가 불가능하며 403 상태코드를 반환한다.")
	void notTeamLeader() throws Exception {
		// given
		TeamInvitationRequest teamInvitationRequest = new TeamInvitationRequest(DEFAULT_TARGET_USER_ID);
		String request = objectMapper.writeValueAsString(teamInvitationRequest);

		ErrorCode errorCode = ErrorCode.NOT_TEAM_LEADER;
		String response = objectMapper.writeValueAsString(new ErrorResponse<>(errorCode));
		doThrow(new BusinessException(errorCode))
			.when(teamInvitationService)
			.invite(DEFAULT_AUTH_ID, DEFAULT_TARGET_USER_TEAM_ID, teamInvitationRequest.targetUserId());

		// when
		ResultActions resultActions = mockMvc.perform(
				post(BASE_END_POINT + "/{teamId}/invitations", DEFAULT_TARGET_USER_TEAM_ID)
					.contentType(MediaType.APPLICATION_JSON)
					.content(request))
			.andDo(print());

		// then
		verify(teamInvitationService, times(1))
			.invite(DEFAULT_AUTH_ID, DEFAULT_TARGET_USER_TEAM_ID, teamInvitationRequest.targetUserId());

		resultActions
			.andExpect(status().isForbidden())
			.andExpect(content().string(response));
	}

	@Test
	@DisplayName("초대를 받은 사용자는 초대를 거절할 수 있으며 상태 변경 후 200 상태코드를 반환한다.")
	void inviteRefuse() throws Exception {
		// given
		TeamInvitationRefuseRequest refuseRequest = new TeamInvitationRefuseRequest(DEFAULT_AUTH_ID);
		String request = objectMapper.writeValueAsString(refuseRequest);

		willDoNothing().given(teamInvitationService)
			.refuse(DEFAULT_AUTH_ID, DEFAULT_TARGET_USER_TEAM_ID, DEFAULT_TEAM_INVITATION_ID, refuseRequest);

		// when
		ResultActions resultActions = mockMvc.perform(
			patch(BASE_END_POINT + "/{teamId}/invitation/{invitationId}",
				DEFAULT_TARGET_USER_TEAM_ID, DEFAULT_TEAM_INVITATION_ID
			)
				.contentType(MediaType.APPLICATION_JSON)
				.content(request)
		).andDo(print());

		// then
		verify(teamInvitationService, times(1))
			.refuse(DEFAULT_AUTH_ID, DEFAULT_TARGET_USER_TEAM_ID, DEFAULT_TEAM_INVITATION_ID, refuseRequest);

		resultActions
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("다른 사용자의 초대를 거절할 시 403 상태코드를 반환한다.")
	void accessDeniedInvitation() throws Exception {
		// given
		TeamInvitationRefuseRequest refuseRequest = new TeamInvitationRefuseRequest(DEFAULT_TARGET_USER_ID);
		String request = objectMapper.writeValueAsString(refuseRequest);

		ErrorCode errorCode = ErrorCode.TEAM_INVITATION_ACCESS_DENIED;
		String response = objectMapper.writeValueAsString(new ErrorResponse<>(errorCode));
		doThrow(new BusinessException(errorCode))
			.when(teamInvitationService)
			.refuse(DEFAULT_AUTH_ID, DEFAULT_AUTH_TEAM_ID, DEFAULT_TEAM_INVITATION_ID, refuseRequest);

		// when
		ResultActions resultActions = mockMvc.perform(
			patch(BASE_END_POINT + "/{teamId}/invitation/{invitationId}",
				DEFAULT_AUTH_TEAM_ID, DEFAULT_TEAM_INVITATION_ID
			)
				.contentType(MediaType.APPLICATION_JSON)
				.content(request)
		).andDo(print());

		// then
		verify(teamInvitationService, times(1))
			.refuse(DEFAULT_AUTH_ID, DEFAULT_AUTH_TEAM_ID, DEFAULT_TEAM_INVITATION_ID, refuseRequest);

		resultActions
			.andExpect(status().isForbidden())
			.andExpect(content().string(response));
	}

	@Test
	@DisplayName("초대장을 찾지 못할 경우 404 상태코드를 반환한다.")
	void notFoundInvitation() throws Exception {
		// given
		TeamInvitationRefuseRequest refuseRequest = new TeamInvitationRefuseRequest(DEFAULT_AUTH_ID);
		String request = objectMapper.writeValueAsString(refuseRequest);

		ErrorCode errorCode = ErrorCode.TEAM_INVITATION_NOT_FOUND;
		String response = objectMapper.writeValueAsString(new ErrorResponse<>(errorCode));
		doThrow(new BusinessException(errorCode))
			.when(teamInvitationService)
			.refuse(DEFAULT_AUTH_ID, DEFAULT_AUTH_TEAM_ID, DEFAULT_NOT_FOUND_INVITATION_ID, refuseRequest);

		// when
		ResultActions resultActions = mockMvc.perform(
			patch(BASE_END_POINT + "/{teamId}/invitation/{invitationId}",
				DEFAULT_AUTH_TEAM_ID, DEFAULT_NOT_FOUND_INVITATION_ID
			)
				.contentType(MediaType.APPLICATION_JSON)
				.content(request)
		).andDo(print());

		// then
		verify(teamInvitationService, times(1))
			.refuse(DEFAULT_AUTH_ID, DEFAULT_AUTH_TEAM_ID, DEFAULT_NOT_FOUND_INVITATION_ID, refuseRequest);

		resultActions
			.andExpect(status().isNotFound())
			.andExpect(content().string(response));
	}

}
