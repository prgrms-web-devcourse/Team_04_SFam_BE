package com.kdt.team04.domain.auth.service;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.kdt.team04.common.exception.EntityNotFoundException;
import com.kdt.team04.common.security.oauth.CustomOAuth2User;
import com.kdt.team04.domain.auth.dto.JwtClaimsAttributes;
import com.kdt.team04.domain.auth.dto.OAuthAttributes;
import com.kdt.team04.domain.user.Role;
import com.kdt.team04.domain.user.dto.UserRequest;
import com.kdt.team04.domain.user.dto.UserResponse;
import com.kdt.team04.domain.user.service.UserService;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

	private final UserService userService;
	private final TagSequenceService tagSequenceService;

	public CustomOAuth2UserService(UserService userService, TagSequenceService tagSequenceService) {
		this.userService = userService;
		this.tagSequenceService = tagSequenceService;
	}

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
		OAuth2User oAuth2User = delegate.loadUser(userRequest);

		String registrationId = userRequest.getClientRegistration().getRegistrationId();
		String userNameAttributeName = userRequest.getClientRegistration()
			.getProviderDetails()
			.getUserInfoEndpoint()
			.getUserNameAttributeName();
		OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName,
			oAuth2User.getAttributes());
		JwtClaimsAttributes jwtClaimsAttributes = saveOrUpdate(oAuth2User.getAttributes());

		return new CustomOAuth2User(
			Collections.singleton(new SimpleGrantedAuthority(Role.USER.getKey())),
			attributes.getAttributes(),
			attributes.getNameAttributeKey(),
			jwtClaimsAttributes.id(),
			jwtClaimsAttributes.username(),
			jwtClaimsAttributes.email(),
			jwtClaimsAttributes.role());
	}

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
		String nickname = generateOAuthNickname(username);
		String usernameWithUUID = username + "_" + UUID.randomUUID().toString().replace('-', '_');
		String profileImageUrl = (String)attributes.get("picture");
		String encodedRandomPassword = UUID.randomUUID().toString();

		return new UserRequest.CreateRequest(usernameWithUUID, encodedRandomPassword, nickname, email, profileImageUrl,
			Role.USER);
	}

	private String generateOAuthNickname(String username) {
		Long nextSequence = tagSequenceService.nextSequenceByKey(username);
		String zeroFilledSuffix = String.format("%04d", nextSequence);

		return username + "#" + zeroFilledSuffix;
	}
}