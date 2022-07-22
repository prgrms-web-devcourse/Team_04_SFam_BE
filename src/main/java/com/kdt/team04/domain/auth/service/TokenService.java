package com.kdt.team04.domain.auth.service;

import com.kdt.team04.common.security.jwt.TokenResponse;

public interface TokenService {
	TokenResponse findByToken(String token);

	String save(String refreshToken, Long userId);
}
