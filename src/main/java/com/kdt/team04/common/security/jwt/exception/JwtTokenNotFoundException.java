package com.kdt.team04.common.security.jwt.exception;

public class JwtTokenNotFoundException extends RuntimeException {
	public JwtTokenNotFoundException() {
	}

	public JwtTokenNotFoundException(String message) {
		super(message);
	}

	public JwtTokenNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}
