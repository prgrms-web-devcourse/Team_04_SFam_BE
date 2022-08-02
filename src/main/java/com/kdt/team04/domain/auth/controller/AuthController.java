package com.kdt.team04.domain.auth.controller;

import static org.springframework.http.HttpHeaders.SET_COOKIE;

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
import com.kdt.team04.domain.auth.dto.TokenDto;
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
		AuthResponse.SignInResponse signInResponse = this.authService.signIn(
			signInRequest.username(),
			signInRequest.password()
		);

		signInResponse.jwtAuthenticationToken().setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
		SecurityContextHolder.getContext().setAuthentication(signInResponse.jwtAuthenticationToken());

		TokenDto accessToken = signInResponse.accessToken();
		TokenDto refreshToken = signInResponse.refreshToken();
		ResponseCookie accessTokenCookie = createSecureCookie(accessToken.header(), accessToken.token(), refreshToken.expirySecond());
		ResponseCookie refreshTokenCookie = createSecureCookie(refreshToken.header(), refreshToken.token(), refreshToken.expirySecond());
		response.setHeader(SET_COOKIE, accessTokenCookie.toString());
		response.addHeader(SET_COOKIE, refreshTokenCookie.toString());

		return new ApiResponse<>(signInResponse);
	}

	private ResponseCookie createSecureCookie(String header, String token, int expirySecond) {
		return ResponseCookie.from(header, token)
			.path("/")
			.httpOnly(true)
			.secure(true)
			.maxAge(expirySecond)
			.sameSite("none")
			.build();
	}

	@Operation(summary = "회원가입")
	@PostMapping("/signup")
	public ApiResponse<AuthResponse.SignUpResponse> signUp(@RequestBody @Valid AuthRequest.SignUpRequest request) {
		return new ApiResponse<>(authService.signUp(request));
	}
}
