package com.kdt.team04.feign.kakao.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConstructorBinding
@ConfigurationProperties(prefix = "kakao")
public record KakaoApiProperties(LocalApiProperties local) {

	public record LocalApiProperties(String header, String accessKey) {
	}
}
