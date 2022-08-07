package com.kdt.team04.common.security.oauth;

import static org.springframework.http.HttpHeaders.SET_COOKIE;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.kdt.team04.common.security.CookieConfigProperties;
import com.kdt.team04.domain.auth.dto.JwtClaimsAttributes;
import com.kdt.team04.domain.auth.dto.TokenDto;
import com.kdt.team04.domain.auth.service.AuthService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {
	private final AuthService authService;
	private final CookieConfigProperties cookieConfigProperties;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) {
		OAuth2User oAuth2User = (OAuth2User)authentication.getPrincipal();
		JwtClaimsAttributes jwtClaimsAttributes = authService.saveOrUpdate(oAuth2User.getAttributes());
		TokenDto accessToken = authService.generateAccessToken(jwtClaimsAttributes);
		TokenDto refreshToken = authService.generateRefreshToken(jwtClaimsAttributes.id());
		ResponseCookie accessTokenCookie = createCookie(accessToken.header(), accessToken.token(),
			refreshToken.expirySeconds());
		ResponseCookie refreshTokenCookie = createCookie(refreshToken.header(), refreshToken.token(),
			refreshToken.expirySeconds());
		writeTokenResponse(response, accessTokenCookie, refreshTokenCookie);
	}

	private ResponseCookie createCookie(String header, String token, int expirySeconds) {
		return ResponseCookie.from(header, token)
			.path("/")
			.httpOnly(true)
			.secure(cookieConfigProperties.secure())
			.maxAge(expirySeconds)
			.sameSite(cookieConfigProperties.sameSite().attributeValue())
			.build();
	}

	private void writeTokenResponse(HttpServletResponse response, ResponseCookie accessTokenCookie,
		ResponseCookie refreshTokenCookie) {
		response.setHeader(SET_COOKIE, accessTokenCookie.toString());
		response.addHeader(SET_COOKIE, refreshTokenCookie.toString());
	}
}
