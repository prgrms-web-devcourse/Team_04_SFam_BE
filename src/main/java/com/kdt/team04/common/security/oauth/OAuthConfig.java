package com.kdt.team04.common.security.oauth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2LoginConfigurer;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Configuration
@Profile("!test")
public class OAuthConfig {
	private final OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService;
	private final OAuth2SuccessHandler successHandler;

	public OAuthConfig(OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService,
		OAuth2SuccessHandler successHandler) {
		this.oAuth2UserService = oAuth2UserService;
		this.successHandler = successHandler;
	}

	@Bean
	public OAuth2LoginConfigurer<HttpSecurity> oAuth2LoginConfigurer() {
		return new OAuth2LoginConfigurer<HttpSecurity>()
			.successHandler(successHandler)
			.userInfoEndpoint()
			.userService(oAuth2UserService)
			.and();
	}
}
