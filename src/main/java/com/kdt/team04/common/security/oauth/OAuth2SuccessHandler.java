package com.kdt.team04.common.security.oauth;

import static org.springframework.http.HttpHeaders.SET_COOKIE;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.kdt.team04.common.security.CookieConfigProperties;
import com.kdt.team04.common.security.jwt.Jwt;
import com.kdt.team04.domain.user.Role;

@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {
	private final Jwt jwt;
	private final CookieConfigProperties cookieConfigProperties;

	public OAuth2SuccessHandler(Jwt jwt, CookieConfigProperties cookieConfigProperties) {
		this.jwt = jwt;
		this.cookieConfigProperties = cookieConfigProperties;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) {
		CustomOAuth2User oAuth2User = (CustomOAuth2User)authentication.getPrincipal();
		Jwt.Claims claims = Jwt.Claims.builder()
			.userId(oAuth2User.userId())
			.roles(new String[] {String.valueOf(Role.USER)})
			.username(oAuth2User.username())
			.email(oAuth2User.email())
			.build();

		String accessToken = jwt.generateAccessToken(claims);
		String refreshToken = jwt.generateRefreshToken();

		ResponseCookie accessTokenCookie = createCookie(jwt.accessTokenProperties().header(), accessToken,
			jwt.refreshTokenProperties().expirySeconds());
		ResponseCookie refreshTokenCookie = createCookie(jwt.refreshTokenProperties().header(), refreshToken,
			jwt.refreshTokenProperties().expirySeconds());

		response.setHeader(SET_COOKIE, accessTokenCookie.toString());
		response.addHeader(SET_COOKIE, refreshTokenCookie.toString());
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
}
