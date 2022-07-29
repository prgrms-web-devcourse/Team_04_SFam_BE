package com.kdt.team04.common.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
	//COMMON
	INTERNAL_SEVER_ERROR("C001", "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR),
	NOT_FOUND_EXCEPTION("C0002", "Not found exception", HttpStatus.NOT_FOUND),
	BIND_ERROR("C0003", "Binding Exception", HttpStatus.BAD_REQUEST),
	RUNTIME_EXCEPTION("C004", "Runtime error", HttpStatus.BAD_REQUEST),
	METHOD_ARGUMENT_TYPE_MISMATCH_EXCEPTION("C005", "Method argument type mismatch exception", HttpStatus.BAD_REQUEST),

	//VALIDATION
	METHOD_ARGUMENT_NOT_VALID("V0001", "Validation error", HttpStatus.BAD_REQUEST),
	CONSTRAINT_VIOLATION("V0002", "Validation error", HttpStatus.BAD_REQUEST),
	DOMAIN_EXCEPTION("V0003", "Domain constraint violation", HttpStatus.BAD_REQUEST),
	DATA_INTEGRITY_VIOLATION("V0004", "Data integrity violation", HttpStatus.BAD_REQUEST),

	//AUTHENTICATION & TOKEN
	AUTHENTICATION_FAILED("A0001", "Authentication failed", HttpStatus.BAD_REQUEST),
	TOKEN_NOT_FOUND("A0002", "Not found token", HttpStatus.NOT_FOUND),

	//USER
	USER_NOT_FOUND("U0001", "Not found user", HttpStatus.NOT_FOUND),
	LOCATION_UPDATE_FAIL("U0002", "user location update failed", HttpStatus.BAD_REQUEST),

	//TEAM
	TEAM_NOT_FOUND("T0001", "Not found team", HttpStatus.NOT_FOUND),
	NOT_TEAM_LEADER("T0002", "Not team leader", HttpStatus.FORBIDDEN),

	//TEAM_INVITATION
	TEAM_INVITATION_NOT_FOUND("I0001", "Not found invitation", HttpStatus.NOT_FOUND),
	INVALID_INVITATION("I0002", "Invalid invitation", HttpStatus.BAD_REQUEST),

	//TEAM_MEMBER
	ALREADY_TEAM_MEMBER("TM0001", "Already member of team", HttpStatus.BAD_REQUEST),

	//MATCHES
	MATCH_PARTICIPANTS("M0001", "Invalid match participants", HttpStatus.BAD_REQUEST);

	private final String code;
	private final String message;
	private final HttpStatus httpStatus;

	ErrorCode(String code, String message, HttpStatus httpStatus) {
		this.code = code;
		this.message = message;
		this.httpStatus = httpStatus;
	}

	public String getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

	public HttpStatus getStatus() {
		return httpStatus;
	}

	@Override
	public String toString() {
		return "ErrorCode[" +
			"type='" + name() + '\'' +
			",code='" + code + '\'' +
			", message='" + message + '\'' +
			']';
	}
}
