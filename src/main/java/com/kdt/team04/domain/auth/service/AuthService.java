package com.kdt.team04.domain.auth.service;

import java.text.MessageFormat;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kdt.team04.common.exception.BusinessException;
import com.kdt.team04.common.exception.EntityNotFoundException;
import com.kdt.team04.common.exception.ErrorCode;
import com.kdt.team04.common.security.jwt.Jwt;
import com.kdt.team04.common.security.jwt.JwtAuthentication;
import com.kdt.team04.common.security.jwt.JwtAuthenticationToken;
import com.kdt.team04.common.security.jwt.JwtConfig;
import com.kdt.team04.domain.auth.dto.AuthRequest;
import com.kdt.team04.domain.auth.dto.AuthResponse;
import com.kdt.team04.domain.auth.dto.JwtClaimsAttributes;
import com.kdt.team04.domain.auth.dto.TokenDto;
import com.kdt.team04.domain.user.Role;
import com.kdt.team04.domain.user.dto.UserRequest;
import com.kdt.team04.domain.user.dto.UserResponse;
import com.kdt.team04.domain.user.service.UserService;

@Service
public class AuthService {

	private final UserService userService;
	private final TokenService tokenService;
	private final PasswordEncoder passwordEncoder;
	private final Jwt jwt;

	public AuthService(UserService userService, TokenService tokenService, PasswordEncoder passwordEncoder,
		Jwt jwt) {
		this.userService = userService;
		this.tokenService = tokenService;
		this.passwordEncoder = passwordEncoder;
		this.jwt = jwt;
	}

	@Transactional
	public AuthResponse.SignInResponse signIn(String username, String password) {
		UserResponse foundUser;
		try {
			foundUser = userService.findByUsername(username);
		} catch (EntityNotFoundException e) {
			throw new BusinessException(ErrorCode.AUTHENTICATION_FAILED,
				MessageFormat.format("username : {0} not found", username));
		}
		if (!passwordEncoder.matches(password, foundUser.password())) {
			throw new BusinessException(ErrorCode.AUTHENTICATION_FAILED,
				MessageFormat.format("Password = {0}", password));
		}

		Jwt.Claims claims = Jwt.Claims.builder()
			.userId(foundUser.id())
			.roles(new String[] {String.valueOf(Role.USER)})
			.username(foundUser.username())
			.build();
		String accessToken = jwt.generateAccessToken(claims);
		String refreshToken = jwt.generateRefreshToken();
		JwtAuthentication authentication = new JwtAuthentication(accessToken, foundUser.id(), foundUser.username(),
			foundUser.email());
		JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken(authentication, null,
			jwt.getAuthorities(jwt.verify(accessToken)));
		tokenService.save(refreshToken, foundUser.id());

		return new AuthResponse.SignInResponse(
			foundUser.id(),
			username,
			foundUser.nickname(),
			foundUser.email(),
			foundUser.profileImageUrl(),
			foundUser.role(),
			new TokenDto(jwt.accessTokenProperties().header(), accessToken,
				jwt.accessTokenProperties().expirySeconds()),
			new TokenDto(jwt.refreshTokenProperties().header(), refreshToken,
				jwt.refreshTokenProperties().expirySeconds()),
			authenticationToken
		);
	}

	@Transactional
	public AuthResponse.SignUpResponse signUp(AuthRequest.SignUpRequest request) {
		String encodedPassword = passwordEncoder.encode(request.password());
		Long userId = userService.create(
			new UserRequest.CreateRequest(
				request.username(),
				encodedPassword,
				request.nickname(),
				request.email(),
				null,
				Role.USER));

		return new AuthResponse.SignUpResponse(userId);
	}

	public TokenDto generateAccessToken(JwtClaimsAttributes jwtClaimsAttributes) {
		Jwt.Claims claims = Jwt.Claims.builder()
			.userId(jwtClaimsAttributes.id())
			.username(jwtClaimsAttributes.username())
			.email(jwtClaimsAttributes.email())
			.roles(new String[] {String.valueOf(jwtClaimsAttributes.role())})
			.build();

		String accessToken = jwt.generateAccessToken(claims);

		JwtConfig.TokenProperties accessTokenProperties = jwt.accessTokenProperties();

		return new TokenDto(accessTokenProperties.header(), accessToken,
			accessTokenProperties.expirySeconds());
	}

	@Transactional
	public TokenDto generateRefreshToken(Long userId) {
		JwtConfig.TokenProperties refreshTokenProperties = jwt.refreshTokenProperties();
		String refreshToken = jwt.generateRefreshToken();

		tokenService.save(refreshToken, userId);

		return new TokenDto(refreshTokenProperties.header(), refreshToken,
			refreshTokenProperties.expirySeconds());
	}

	@Transactional
	public JwtClaimsAttributes saveOrUpdate(Map<String, Object> attributes) {
		JwtClaimsAttributes jwtClaimsAttributes;
		try {
			UserResponse foundUserResponse = userService.findByEmail((String)attributes.get("email"));
			UserRequest.Update updateRequest = new UserRequest.Update(null, null, (String)attributes.get("email"));
			userService.update(foundUserResponse.id(), updateRequest);
			jwtClaimsAttributes = new JwtClaimsAttributes(
				foundUserResponse.id(),
				foundUserResponse.username(),
				foundUserResponse.email(),
				foundUserResponse.role()
			);

			return jwtClaimsAttributes;
		} catch (EntityNotFoundException e) {
			UserRequest.CreateRequest createRequest = attributeToCreateUserRequest(attributes);
			Long userId = userService.create(createRequest);

			jwtClaimsAttributes = new JwtClaimsAttributes(
				userId,
				createRequest.username(),
				createRequest.email(),
				createRequest.role()
			);

			return jwtClaimsAttributes;
		}
	}

	private UserRequest.CreateRequest attributeToCreateUserRequest(Map<String, Object> attributes) {
		String email = (String)attributes.get("email");
		String username = email.split("@")[0];
		String nickname = generateRandomNickname(username);
		String usernameWithUUID = username + "_" + UUID.randomUUID().toString().replace('-', '_');
		String profileImageUrl = (String)attributes.get("picture");
		String encodedRandomPassword = UUID.randomUUID().toString();
		nickname = generateRandomNicknameRecursive(nickname);

		return new UserRequest.CreateRequest(usernameWithUUID, encodedRandomPassword, nickname, email, profileImageUrl,
			Role.USER);
	}

	private String generateRandomNickname(String username) {
		int randomSuffix = new Random().nextInt(9999);
		String zeroFilledSuffix = String.format("%04d", randomSuffix);

		return username + "#" + zeroFilledSuffix;
	}

	private String generateRandomNicknameRecursive(String nickname) {
		return userService.nicknameDuplicationCheck(nickname) ? generateRandomNicknameRecursive(nickname) : nickname;
	}
}
