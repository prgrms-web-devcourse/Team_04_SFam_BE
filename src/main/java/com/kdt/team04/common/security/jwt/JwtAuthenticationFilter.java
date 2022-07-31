package com.kdt.team04.common.security.jwt;

import static org.springframework.http.HttpHeaders.SET_COOKIE;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.kdt.team04.common.exception.EntityNotFoundException;
import com.kdt.team04.common.security.jwt.exception.JwtAccessTokenNotFoundException;
import com.kdt.team04.common.security.jwt.exception.JwtRefreshTokenNotFoundException;
import com.kdt.team04.common.security.jwt.exception.JwtTokenNotFoundException;
import com.kdt.team04.domain.auth.service.TokenService;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
	private final Jwt jwt;
	private final TokenService tokenService;

	private final Logger log = LoggerFactory.getLogger(getClass());

	public JwtAuthenticationFilter(Jwt jwt, TokenService tokenService) {
		this.jwt = jwt;
		this.tokenService = tokenService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		logRequest(request);

		try {
			authenticate(getAccessToken(request), request, response);
		} catch (JwtTokenNotFoundException e) {
			this.log.warn(e.getMessage());
		}
		filterChain.doFilter(request, response);
	}

	private void logRequest(HttpServletRequest request) {
		log.info(String.format(
			"[%s] %s %s",
			request.getMethod(),
			request.getRequestURI().toLowerCase(),
			request.getQueryString() == null ? "" : request.getQueryString())
		);
	}

	private String getAccessToken(HttpServletRequest request) {
		if (request.getCookies() == null) {
			throw new JwtAccessTokenNotFoundException("AccessToken is not found.");
		}
		return Arrays.stream(request.getCookies())
			.filter(cookie -> cookie.getName().equals(this.jwt.accessTokenProperties().header()))
			.findFirst()
			.map(Cookie::getValue)
			.orElseThrow(() -> new JwtAccessTokenNotFoundException("AccessToken is not found"));
	}

	private void authenticate(String accessToken, HttpServletRequest request, HttpServletResponse response) {
		try {
			Jwt.Claims claims = verify(accessToken);
			JwtAuthenticationToken authentication = createAuthenticationToken(claims, request, accessToken);
			SecurityContextHolder.getContext().setAuthentication(authentication);
		} catch (TokenExpiredException exception) {
			Cookie cookie = new Cookie(jwt.accessTokenProperties().header(), "");
			cookie.setPath("/");
			cookie.setMaxAge(0);
			cookie.setHttpOnly(true);
			cookie.setSecure(true);
			response.addCookie(cookie);
			this.log.warn(exception.getMessage());
			refreshAuthentication(accessToken, request, response);
		} catch (JWTVerificationException exception) {
			log.warn(exception.getMessage());
		}
	}

	private JwtAuthenticationToken createAuthenticationToken(Jwt.Claims claims, HttpServletRequest request,
		String accessToken) {
		List<GrantedAuthority> authorities = this.jwt.getAuthorities(claims);
		if (claims.userId != null && !authorities.isEmpty()) {
			JwtAuthentication authentication = new JwtAuthentication(accessToken, claims.userId, claims.username);
			JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken(authentication, null, authorities);
			authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

			return authenticationToken;
		} else {
			throw new JWTDecodeException("Decode Error");
		}
	}

	private void refreshAuthentication(String accessToken, HttpServletRequest request, HttpServletResponse response) {
		try {
			String refreshToken = getRefreshToken(request);
			if (isValidRefreshToken(refreshToken, accessToken)) {
				String reIssuedAccessToken = accessTokenReIssue(accessToken);
				Jwt.Claims reIssuedClaims = verify(reIssuedAccessToken);
				JwtAuthenticationToken authentication = createAuthenticationToken(reIssuedClaims, request,
					reIssuedAccessToken);
				SecurityContextHolder.getContext().setAuthentication(authentication);
				ResponseCookie cookie = ResponseCookie.from(this.jwt.accessTokenProperties().header(), reIssuedAccessToken)
					.path("/")
					.httpOnly(true)
					.sameSite("none")
					.secure(true)
					.maxAge(this.jwt.accessTokenProperties().expirySeconds())
					.build();
				response.addHeader(SET_COOKIE, cookie.toString());
			} else {
				log.warn("refreshToken expired");
			}
		} catch (JwtTokenNotFoundException | JWTVerificationException e) {
			this.log.warn(e.getMessage());
		}
	}

	private String getRefreshToken(HttpServletRequest request) {
		if (request.getCookies() != null) {
			return Arrays.stream(request.getCookies())
				.filter(cookie -> cookie.getName().equals(this.jwt.refreshTokenProperties().header()))
				.findFirst()
				.map(Cookie::getValue)
				.orElseThrow(() -> new JwtRefreshTokenNotFoundException("RefreshToken is not found."));
		} else {
			throw new JwtRefreshTokenNotFoundException();
		}
	}

	private boolean isValidRefreshToken(String refreshToken, String accessToken) {
		try {
			TokenResponse foundRefreshToken = this.tokenService.findByToken(refreshToken);
			Long userId = this.jwt.decode(accessToken).userId;
			if (userId.equals(foundRefreshToken.userId())) {
				this.jwt.verify(foundRefreshToken.token());

				return true;
			}
		} catch (EntityNotFoundException | JWTVerificationException e) {
			log.warn(e.getMessage());

			return false;
		}
		return false;
	}

	private String accessTokenReIssue(String accessToken) {
		return jwt.generateAccessToken(this.jwt.decode(accessToken));
	}

	private Jwt.Claims verify(String token) {
		return jwt.verify(token);
	}
}