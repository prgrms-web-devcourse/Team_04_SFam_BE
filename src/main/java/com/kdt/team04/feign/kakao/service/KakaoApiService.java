package com.kdt.team04.feign.kakao.service;

import org.springframework.stereotype.Service;

import com.kdt.team04.feign.kakao.client.KakaoClient;
import com.kdt.team04.feign.kakao.config.KakaoApiProperties;
import com.kdt.team04.feign.kakao.dto.CoordToAddressResponse;

@Service
public class KakaoApiService {
	private final KakaoApiProperties kakaoApiProperties;
	private final KakaoClient kakaoClient;

	public KakaoApiService(KakaoClient kakaoClient, KakaoApiProperties kakaoApiProperties) {
		this.kakaoClient = kakaoClient;
		this.kakaoApiProperties = kakaoApiProperties;
	}

	public CoordToAddressResponse coordToAddressResponse(Double x, Double y) {
		KakaoApiProperties.LocalApiProperties localApiProperties = kakaoApiProperties.local();

		final String kakaoAkHeaderValue = localApiProperties.header()
			+ " "
			+ localApiProperties.accessKey();

		return kakaoClient.coordToAddress(kakaoAkHeaderValue, x, y);
	}
}
