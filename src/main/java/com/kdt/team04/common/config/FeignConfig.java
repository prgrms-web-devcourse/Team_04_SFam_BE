package com.kdt.team04.common.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.kdt.team04.feign.division.config.DivisionApiProperties;
import com.kdt.team04.feign.kakao.config.KakaoApiProperties;

@Configuration
@EnableConfigurationProperties({DivisionApiProperties.class, KakaoApiProperties.class})
public class FeignConfig {
}
