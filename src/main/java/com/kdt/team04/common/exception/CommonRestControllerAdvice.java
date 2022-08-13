package com.kdt.team04.common.exception;

import java.util.Arrays;

import javax.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;

@RestControllerAdvice
public class CommonRestControllerAdvice {

	private final Logger log = LoggerFactory.getLogger(getClass());

	private ResponseEntity<ErrorResponse<ErrorCode>> newResponse(ErrorCode errorCode) {
		return new ResponseEntity<>(new ErrorResponse<>(errorCode), errorCode.getStatus());
	}

	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<ErrorResponse<ErrorCode>> handleBusinessException(BusinessException e) {
		log.warn("Service error occurred : {}", e.getMessage(), e);
		ErrorCode errorCode = e.errorCode();

		return newResponse(errorCode);
	}

	@ExceptionHandler(BindException.class)
	public ResponseEntity<ErrorResponse<ErrorCode>> handleBindException(BindException e) {
		log.warn("Binding error occurred : {}", e.getMessage(), e);

		return newResponse(ErrorCode.BIND_ERROR);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse<ErrorCode>> handleMethodArgumentNotValidException(
		MethodArgumentNotValidException e
	) {
		log.warn("Method argument not valid error occurred : {}", e.getMessage(), e);

		return newResponse(ErrorCode.METHOD_ARGUMENT_NOT_VALID);
	}

	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<ErrorResponse<ErrorCode>> handleMissingServletRequestParameterException(
		MissingServletRequestParameterException e) {
		log.warn("Missing servlet request parameter error occurred : {}", e.getMessage(), e);

		return newResponse(ErrorCode.METHOD_ARGUMENT_NOT_VALID);
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ErrorResponse<ErrorCode>> handleMethodArgumentTypeMismatchException(
		MethodArgumentTypeMismatchException e
	) {
		log.warn("Method argument type mismatch error occurred: {}", e.getMessage(), e);

		return newResponse(ErrorCode.METHOD_ARGUMENT_TYPE_MISMATCH_EXCEPTION);
	}

	@ExceptionHandler({TransactionSystemException.class, ConstraintViolationException.class})
	public ResponseEntity<ErrorResponse<ErrorCode>> handleConstraintViolation(ConstraintViolationException e) {
		log.warn("Constraint violation error occurred: {}", e.getMessage(), e);

		return newResponse(ErrorCode.CONSTRAINT_VIOLATION);
	}

	@ExceptionHandler(DomainException.class)
	public ResponseEntity<ErrorResponse<ErrorCode>> handleDomainException(DomainException e) {
		log.warn("Domain error occurred : {}", e.getMessage(), e);
		ErrorCode errorCode = e.getErrorCode();

		return newResponse(errorCode);
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ErrorResponse<ErrorCode>> handleDataIntegrityViolationException(
		DataIntegrityViolationException e) {
		log.warn("DataIntegrityViolation error occurred : {}", e.getMessage(), e);

		return newResponse(ErrorCode.DATA_INTEGRITY_VIOLATION);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErrorResponse<ErrorCode>> handleHttpMessageNotReadableException(
		HttpMessageNotReadableException e
	) {
		if (e.getCause() instanceof InvalidFormatException) {
			InvalidFormatException invalidFormat = (InvalidFormatException) e.getCause();
			if (invalidFormat.getTargetType().isEnum()) {
				log.warn("Invalid enum value: {} for the field: {}, The value must be one of: {}",
					invalidFormat.getValue(),
					invalidFormat.getPath().get(0).getFieldName(),
					Arrays.toString(invalidFormat.getTargetType().getEnumConstants()),
					e);

				return newResponse(ErrorCode.INVALID_ENUM_VALUE);
			}
		}

		log.warn("Unacceptable JSON error occurred : {}", e.getMessage(), e);

		return newResponse(ErrorCode.UNACCEPTABLE_JSON_ERROR);
	}

	@ExceptionHandler(NotAuthenticationException.class)
	public ResponseEntity<ErrorResponse<ErrorCode>> handleNotAuthenticationException(NotAuthenticationException e) {
		log.warn("NotAuthentication error occurred : {}", e.getMessage(), e);

		return newResponse(ErrorCode.NOT_AUTHENTICATED);
	}

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ErrorResponse<ErrorCode>> handleRuntimeException(RuntimeException e) {
		log.warn("Unexpected error occurred : {}", e.getMessage(), e);

		return newResponse(ErrorCode.RUNTIME_EXCEPTION);
	}
}
