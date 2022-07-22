package com.kdt.team04.domain.auth.service;

import java.text.MessageFormat;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kdt.team04.common.exception.EntityNotFoundException;
import com.kdt.team04.common.exception.ErrorCode;
import com.kdt.team04.common.security.jwt.TokenResponse;
import com.kdt.team04.domain.auth.entity.Token;
import com.kdt.team04.domain.auth.repository.TokenRepository;

@Service
@Transactional(readOnly = true)
public class JpaTokenService implements TokenService {

	private final TokenRepository tokenRepository;

	public JpaTokenService(TokenRepository tokenRepository) {
		this.tokenRepository = tokenRepository;
	}

	@Override
	public TokenResponse findByToken(String token) {
		Token foundToken = tokenRepository.findById(token).orElseThrow(() -> new EntityNotFoundException(
			ErrorCode.TOKEN_NOT_FOUND, MessageFormat.format("Token = {0}", token)));

		return new TokenResponse(foundToken.token(), foundToken.getUserId());
	}

	@Override
	@Transactional
	public String save(String refreshToken, Long userId) {
		return this.tokenRepository.save(new Token(refreshToken, userId)).token();
	}
}
