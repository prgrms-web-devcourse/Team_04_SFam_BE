package com.kdt.team04.common.exception;

public class NotAuthenticationException extends RuntimeException {
	public NotAuthenticationException(String message) {
		super(message);
	}

	public NotAuthenticationException(String message, Throwable cause) {
		super(message, cause);
	}
}
