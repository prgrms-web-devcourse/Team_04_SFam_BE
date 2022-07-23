package com.kdt.team04.domain.auth.service;

import java.text.MessageFormat;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kdt.team04.common.exception.BusinessException;
import com.kdt.team04.common.exception.ErrorCode;
import com.kdt.team04.common.security.jwt.Jwt;
import com.kdt.team04.common.security.jwt.JwtAuthentication;
import com.kdt.team04.common.security.jwt.JwtAuthenticationToken;
import com.kdt.team04.domain.auth.dto.AuthRequest;
import com.kdt.team04.domain.auth.dto.AuthResponse;
import com.kdt.team04.domain.auth.dto.TokenDto;
import com.kdt.team04.domain.user.Role;
import com.kdt.team04.domain.user.dto.UserRequest;
import com.kdt.team04.domain.user.dto.UserResponse;
import com.kdt.team04.domain.user.service.UserService;

@Service
@Transactional(readOnly = true)
public class JwtAuthService implements AuthService {

	private final UserService userService;
	private final PasswordEncoder passwordEncoder;
	private final TokenService tokenService;
	private final Jwt jwt;

	public JwtAuthService(UserService userService, PasswordEncoder passwordEncoder, TokenService tokenService,
		Jwt jwt) {
		this.userService = userService;
		this.passwordEncoder = passwordEncoder;
		this.tokenService = tokenService;
		this.jwt = jwt;
	}

	@Override
	public AuthResponse.SignInResponse signIn(String username, String password) {
		UserResponse foundMember = this.userService.findByUsername(username);
		if (!this.passwordEncoder.matches(password, foundMember.password())) {
			throw new BusinessException(ErrorCode.AUTHENTICATION_FAILED,
				MessageFormat.format("Password = {0}", password));
		}

		Jwt.Claims claims = Jwt.Claims.builder()
			.userId(foundMember.id())
			.roles(new String[] {String.valueOf(Role.USER)})
			.username(foundMember.username())
			.build();
		String accessToken = this.jwt.generateAccessToken(claims);
		String refreshToken = this.jwt.generateRefreshToken();
		JwtAuthentication authentication = new JwtAuthentication(accessToken, foundMember.id(), foundMember.username());
		JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken(authentication, null,
			this.jwt.getAuthorities(this.jwt.verify(accessToken)));
		this.tokenService.save(refreshToken, foundMember.id());

		return new AuthResponse.SignInResponse(
			foundMember.id(),
			username,
			new TokenDto(this.jwt.accessTokenProperties().header(), accessToken),
			new TokenDto(this.jwt.refreshTokenProperties().header(), refreshToken),
			authenticationToken
		);
	}

	@Override
	@Transactional
	public AuthResponse.SignUpResponse signUp(AuthRequest.SignUpRequest request) {
		String encodedPassword = this.passwordEncoder.encode(request.password());
		Long userId = this.userService.create(
			new UserRequest.CreateRequest(request.username(), encodedPassword, request.nickname()));

		return new AuthResponse.SignUpResponse(userId);
	}
}
