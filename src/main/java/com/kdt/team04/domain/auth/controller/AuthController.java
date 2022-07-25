package com.kdt.team04.domain.auth.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.http.ResponseCookie;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kdt.team04.common.ApiResponse;
import com.kdt.team04.domain.auth.dto.AuthRequest;
import com.kdt.team04.domain.auth.dto.AuthResponse;
import com.kdt.team04.domain.auth.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "로그인/회원가입 API")
@RestController
@RequestMapping("/api/users")
public class AuthController {

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@Operation(summary = "로그인", description = "로그인을 통해 토큰을 획득합니다.")
	@PostMapping("/signin")
	public ApiResponse<AuthResponse.SignInResponse> signIn(HttpServletRequest request, HttpServletResponse response,
		@RequestBody @Valid AuthRequest.SignInRequest signInRequest) {
		AuthResponse.SignInResponse signInResponse = this.authService.signIn(signInRequest.username(),
			signInRequest.password());
		ResponseCookie accessTokenCookie = ResponseCookie.from(signInResponse.accessToken().header(),
			signInResponse.accessToken().token()).path("/").build();
		ResponseCookie refreshTokenCookie = ResponseCookie.from(signInResponse.refreshToken().header(),
			signInResponse.refreshToken().token()).path("/").build();
		signInResponse.jwtAuthenticationToken().setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
		SecurityContextHolder.getContext().setAuthentication(signInResponse.jwtAuthenticationToken());
		response.setHeader("Set-Cookie", accessTokenCookie.toString());
		response.addHeader("Set-Cookie", refreshTokenCookie.toString());

		return new ApiResponse<>(signInResponse);
	}

	@Operation(summary = "회원가입")
	@PostMapping("/signup")
	public ApiResponse<AuthResponse.SignUpResponse> signUp(@RequestBody @Valid AuthRequest.SignUpRequest request) {
		return new ApiResponse<>(authService.signUp(request));
	}
}
