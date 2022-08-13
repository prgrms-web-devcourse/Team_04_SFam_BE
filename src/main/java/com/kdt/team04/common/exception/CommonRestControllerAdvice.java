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

	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<ErrorResponse<ErrorCode>> handleBusinessException(BusinessException e) {
		this.log.info("{}", e.toString(), e);
		ErrorCode errorCode = e.errorCode();

		return new ResponseEntity<>(new ErrorResponse<>(errorCode), errorCode.getStatus());
	}

	@ExceptionHandler(BindException.class)
	public ResponseEntity<ErrorResponse<ErrorCode>> handleBindException(BindException e) {
		this.log.warn(e.getMessage(), e);
		ErrorCode errorCode = ErrorCode.BIND_ERROR;

		return new ResponseEntity<>(new ErrorResponse<>(errorCode), errorCode.getStatus());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse<ErrorCode>> handleMethodArgumentNotValidException(
		MethodArgumentNotValidException e) {
		this.log.warn(e.getMessage(), e);
		ErrorCode errorCode = ErrorCode.METHOD_ARGUMENT_NOT_VALID;

		return new ResponseEntity<>(new ErrorResponse<>(errorCode), errorCode.getStatus());
	}

	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<ErrorResponse<ErrorCode>> handleMissingServletRequestParameterException(
		MissingServletRequestParameterException e) {
		this.log.warn(e.getMessage(), e);
		ErrorCode errorCode = ErrorCode.METHOD_ARGUMENT_NOT_VALID;

		return new ResponseEntity<>(new ErrorResponse<>(errorCode), errorCode.getStatus());
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ErrorResponse<ErrorCode>> handleMethodArgumentTypeMismatchException(
		MethodArgumentTypeMismatchException e
	) {
		this.log.warn(e.getMessage(), e);
		ErrorCode errorCode = ErrorCode.METHOD_ARGUMENT_TYPE_MISMATCH_EXCEPTION;

		return new ResponseEntity<>(new ErrorResponse<>(errorCode), errorCode.getStatus());
	}

	@ExceptionHandler({TransactionSystemException.class, ConstraintViolationException.class})
	public ResponseEntity<ErrorResponse<ErrorCode>> handleConstraintViolation(ConstraintViolationException e) {
		this.log.warn(e.getMessage(), e);
		ErrorCode errorCode = ErrorCode.CONSTRAINT_VIOLATION;

		return new ResponseEntity<>(new ErrorResponse<>(errorCode), errorCode.getStatus());
	}

	@ExceptionHandler(DomainException.class)
	public ResponseEntity<ErrorResponse<ErrorCode>> handleDomainException(DomainException e) {
		this.log.warn("{}", e.toString(), e);
		ErrorCode errorCode = e.getErrorCode();

		return new ResponseEntity<>(new ErrorResponse<>(errorCode), errorCode.getStatus());
	}

	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ErrorResponse<ErrorCode>> handleDataIntegrityViolationException(
		DataIntegrityViolationException e) {
		this.log.warn("{}", e.toString(), e);
		ErrorCode errorCode = ErrorCode.DATA_INTEGRITY_VIOLATION;

		return new ResponseEntity<>(new ErrorResponse<>(errorCode), errorCode.getStatus());
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
					e
				);
				ErrorCode errorCode = ErrorCode.INVALID_ENUM_VALUE;

				return new ResponseEntity<>(new ErrorResponse<>(errorCode), errorCode.getStatus());
			}
		}

		log.warn("Unacceptable JSON : {}", e.getMessage(), e);
		ErrorCode errorCode = ErrorCode.UNACCEPTABLE_JSON_ERROR;

		return new ResponseEntity<>(new ErrorResponse<>(errorCode), errorCode.getStatus());
	}

	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ErrorResponse<ErrorCode>> handleRuntimeException(RuntimeException e) {
		this.log.warn("{}", e.toString(), e);
		ErrorCode errorCode = ErrorCode.RUNTIME_EXCEPTION;

		return new ResponseEntity<>(new ErrorResponse<>(errorCode), errorCode.getStatus());
	}

	@ResponseStatus(code = HttpStatus.UNAUTHORIZED)
	@ExceptionHandler(NotAuthenticationException.class)
	public ResponseEntity<ErrorResponse<ErrorCode>> handleNotAuthenticationException(NotAuthenticationException e) {
		this.log.warn("{}", e.toString(), e);
		ErrorCode errorCode = ErrorCode.NOT_AUTHENTICATED;

		return new ResponseEntity<>(new ErrorResponse<>(errorCode), errorCode.getStatus());
	}
}
