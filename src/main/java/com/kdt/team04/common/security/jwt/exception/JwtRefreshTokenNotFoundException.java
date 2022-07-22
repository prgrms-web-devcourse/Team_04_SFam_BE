package com.kdt.team04.common.security.jwt.exception;

public class JwtRefreshTokenNotFoundException extends JwtTokenNotFoundException {
	public JwtRefreshTokenNotFoundException() {
		super();
	}

	public JwtRefreshTokenNotFoundException(String message) {
		super(message);
	}

	public JwtRefreshTokenNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}
