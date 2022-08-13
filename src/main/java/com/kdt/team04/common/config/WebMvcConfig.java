package com.kdt.team04.common.config;

import java.util.List;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.kdt.team04.common.config.resolver.CustomAuthenticationPrincipalArgumentResolver;
import com.kdt.team04.feign.division.config.DivisionApiProperties;
import com.kdt.team04.feign.kakao.config.KakaoApiProperties;

@Configuration
@EnableConfigurationProperties({CorsConfigProperties.class, DivisionApiProperties.class, KakaoApiProperties.class})
public class WebMvcConfig implements WebMvcConfigurer {

	private final CorsConfigProperties corsConfigProperties;
	private final CustomAuthenticationPrincipalArgumentResolver authenticationPrincipalArgumentResolver;

	public WebMvcConfig(CorsConfigProperties corsConfigProperties,
		CustomAuthenticationPrincipalArgumentResolver authenticationPrincipalArgumentResolver) {
		this.corsConfigProperties = corsConfigProperties;
		this.authenticationPrincipalArgumentResolver = authenticationPrincipalArgumentResolver;
	}


	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping(corsConfigProperties.api())
			.allowedOrigins(corsConfigProperties.origin())
			.allowedMethods(corsConfigProperties.method())
			.allowCredentials(true).maxAge(3600)
		;
	}

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		resolvers.add(authenticationPrincipalArgumentResolver);
	}
}
