package com.backend.global.advice;

import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.backend.global.dto.response.ErrorDetail;
import com.backend.global.dto.response.GenericResponse;
import com.backend.domain.captain.exception.CaptainException;
import com.backend.domain.fish.exception.FishException;
import com.backend.domain.fishencyclopedia.exception.FishEncyclopediaException;
import com.backend.domain.member.exception.MemberException;
import com.backend.domain.reservation.exception.ReservationException;
import com.backend.domain.review.exception.ReviewException;
import com.backend.domain.ship.exception.ShipException;
import com.backend.domain.shipfishingpost.exception.ShipFishingPostException;
import com.backend.global.auth.exception.JwtAuthenticationException;
import com.backend.global.dto.response.ErrorDetail;
import com.backend.global.dto.response.GenericResponse;
import com.backend.global.exception.GlobalErrorCode;
import com.backend.global.exception.GlobalException;
import com.backend.global.storage.exception.StorageException;

import lombok.extern.slf4j.Slf4j;

/**
 * GlobalControllerAdvice
 * <p>공통 예외 처리를 담당하는 클래스 입니다.</p>
 *
 * @author Kim Dong O
 */
@RestControllerAdvice
@Slf4j
public class GlobalControllerAdvice {

	/**
	 * GlobalException 처리 핸들러 입니다.
	 *
	 * @param globalException {@link GlobalException}
	 * @return {@link ResponseEntity<GenericResponse>}
	 */
	@ExceptionHandler(GlobalException.class)
	public ResponseEntity<GenericResponse<Void>> handlerGlobalException(
		GlobalException globalException) {
		log.error("handlerGlobalException: ", globalException);

		GenericResponse<Void> genericResponse = GenericResponse.fail(
			globalException.getErrorCode().getCode(),
			globalException.getMessage()
		);

		return ResponseEntity.status(globalException.getStatus().value())
			.body(genericResponse);
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<GenericResponse<Void>> handleDataIntegrityViolationException(
		DataIntegrityViolationException ex) {
		log.error("DataIntegrityViolationException caught: ", ex);

		GenericResponse<Void> response = GenericResponse.fail(
			GlobalErrorCode.DATA_INTEGRITY_VIOLATION.getCode(),
			GlobalErrorCode.DATA_INTEGRITY_VIOLATION.getMessage()
		);

		return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
	}

	/**
	 * Validation 예외 처리 핸들러 입니다.
	 *
	 * @param ex Exception
	 * @param ex      MethodArgumentNotValidException
	 * @return {@link ResponseEntity<GenericResponse<List<ErrorDetail>}
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<GenericResponse<List<ErrorDetail>>> handlerMethodArgumentNotValidException(
		MethodArgumentNotValidException ex
	) {
		log.error("handlerMethodArgumentNotValidException: ", ex);

		BindingResult bindingResult = ex.getBindingResult();
		List<ErrorDetail> errors = new ArrayList<>();
		GlobalErrorCode globalErrorCode = GlobalErrorCode.NOT_VALID;

		//Field 에러 처리
		for (FieldError error : bindingResult.getFieldErrors()) {
			ErrorDetail customError = ErrorDetail.of(error.getField(), error.getDefaultMessage());

			errors.add(customError);
		}

		//Object 에러 처리
		for (ObjectError globalError : bindingResult.getGlobalErrors()) {
			ErrorDetail customError = ErrorDetail.of(
				globalError.getObjectName(),
				globalError.getDefaultMessage()
			);

			errors.add(customError);
		}

		GenericResponse<List<ErrorDetail>> genericResponse = GenericResponse.fail(
			globalErrorCode.getCode(),
			errors,
			globalErrorCode.getMessage()
		);

		return ResponseEntity.status(globalErrorCode.getHttpStatus().value())
			.body(genericResponse);
	}
}