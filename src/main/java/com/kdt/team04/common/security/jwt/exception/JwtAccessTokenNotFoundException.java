package com.kdt.team04.common.security.jwt.exception;

public class JwtAccessTokenNotFoundException extends JwtTokenNotFoundException {
	public JwtAccessTokenNotFoundException() {
	}

	public JwtAccessTokenNotFoundException(String message) {
		super(message);
	}

	public JwtAccessTokenNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}
