package com.kdt.team04.common.exception;

import java.time.LocalDateTime;

public class ErrorResponse<T extends ErrorCode> {
	private final String code;
	private final String message;
	private final LocalDateTime dateTime;

	public ErrorResponse(T errorCode) {
		this.code = errorCode.getCode();
		this.message = errorCode.getMessage();
		this.dateTime = LocalDateTime.now();
	}

	public String getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

	public LocalDateTime dateTime() {
		return dateTime;
	}
}
