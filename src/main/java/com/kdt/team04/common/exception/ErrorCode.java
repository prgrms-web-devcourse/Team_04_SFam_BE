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
	LOCATION_NOT_FOUND("U0003", "Not found location data", HttpStatus.BAD_REQUEST),

	//TEAM
	TEAM_NOT_FOUND("T0001", "Not found team", HttpStatus.NOT_FOUND),
	NOT_TEAM_LEADER("T0002", "Not team leader", HttpStatus.FORBIDDEN),

	//TEAM_INVITATION
	TEAM_INVITATION_NOT_FOUND("I0001", "Not found invitation", HttpStatus.NOT_FOUND),
	INVALID_INVITATION("I0002", "Invalid invitation", HttpStatus.BAD_REQUEST),

	//TEAM_MEMBER
	ALREADY_TEAM_MEMBER("TM0001", "Already member of team", HttpStatus.BAD_REQUEST),

	//MATCHES
	MATCH_NOT_FOUND("M0001", "Not found match", HttpStatus.NOT_FOUND),
	MATCH_PARTICIPANTS("M0003", "Invalid match participants", HttpStatus.BAD_REQUEST),
	INVALID_PARTICIPANTS("M0004", "Invalid match participants", HttpStatus.BAD_REQUEST),
	AUTHOR_NOT_MATCHED("M0005", "Author not matched", HttpStatus.BAD_REQUEST),
	MATCH_ACCESS_DENIED("M0006", "Don't have permission to access match", HttpStatus.FORBIDDEN),
	MATCH_ALREADY_CHANGED_STATUS("M0007", "Already been changed to that state.", HttpStatus.BAD_REQUEST),
	MATCH_ENDED("M0008", "Match Already ended.", HttpStatus.BAD_REQUEST),
	MATCH_CANNOT_UPDATE_END("M0009", "Match cannot update to end.", HttpStatus.BAD_REQUEST),
	MATCH_NOT_IN_GAME("M0010", "Match is not in game.", HttpStatus.BAD_REQUEST),
	MATCH_NOT_ENDED("M0011", "Match not ended", HttpStatus.BAD_REQUEST),

	//MATCH_PROPOSAL
	MATCH_PROPOSAL_NOT_FOUND("MP0001", "Not found proposal", HttpStatus.NOT_FOUND),
	INVALID_CREATE_REQUEST("MP0002", "Invalid proposal request", HttpStatus.BAD_REQUEST),
	MATCH_PROPOSAL_NOT_APPROVED("MP0003", "The request is not approved.", HttpStatus.BAD_REQUEST),
	ANOTHER_MATCH_PROPOSAL_ALREADY_FIXED("MP0004", "Fixed another proposal already exists.", HttpStatus.BAD_REQUEST),
	INVALID_REACT("MP0005", "Invalid react", HttpStatus.BAD_REQUEST),
	INVALID_DELETE_REQUEST("MP0006","Invalid delete request", HttpStatus.BAD_REQUEST),

	//MATCH_CHAT
	MATCH_CHAT_NOT_CORRECT_CHAT_PARTNER("MC0002", "The chat partner is incorrect.", HttpStatus.BAD_REQUEST),

	//MATCH_REVIEW
	MATCH_REVIEW_ALREADY_EXISTS("MR0002", "Match review already exists", HttpStatus.BAD_REQUEST),

	// FILE
	FILE_NOT_FOUND("F0001", "Not found file", HttpStatus.NOT_FOUND),
	INVALID_FILE_TYPE("F0002", "Invalid file type", HttpStatus.BAD_REQUEST),
	INVALID_FILE_SIGNATURE("F0003", "Invalid file signature", HttpStatus.BAD_REQUEST),
	FILE_IO("F0004", "File I/O fail", HttpStatus.BAD_REQUEST);


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
