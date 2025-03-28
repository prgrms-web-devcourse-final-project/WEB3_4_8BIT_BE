package com.backend.global.advice;

import com.backend.domain.member.exception.MemberException;
import com.backend.global.auth.exception.JwtAuthenticationException;
import com.backend.global.exception.GlobalErrorCode;
import com.backend.global.exception.GlobalException;
import com.backend.global.response.ErrorDetail;
import com.backend.global.response.GenericResponse;
import com.backend.global.storage.exception.StorageException;

import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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
			globalException.getGlobalErrorCode().getCode(),
			globalException.getMessage()
		);

		return ResponseEntity.status(globalException.getStatus().value())
			.body(genericResponse);
	}

	/**
	 * JwtAuthenticationException 처리 핸들러입니다.
	 *
	 * @param jwtAuthenticationException {@link JwtAuthenticationException}
	 * @return {@link ResponseEntity<GenericResponse>}
	 */
	@ExceptionHandler(JwtAuthenticationException.class)
	public ResponseEntity<GenericResponse<Void>> handleJwtAuthenticationException(
		JwtAuthenticationException jwtAuthenticationException) {
		log.error("handleJwtAuthenticationException: ", jwtAuthenticationException);

		GenericResponse<Void> genericResponse = GenericResponse.fail(
			jwtAuthenticationException.getJwtAuthenticationErrorCode().getCode(),
			jwtAuthenticationException.getMessage()
		);

		return ResponseEntity.status(jwtAuthenticationException.getStatus().value())
			.body(genericResponse);
	}

	/**
	 * MembersException 처리 핸들러입니다.
	 *
	 * @param memberException {@link MemberException}
	 * @return {@link ResponseEntity<GenericResponse>}
	 */
	@ExceptionHandler(MemberException.class)
	public ResponseEntity<GenericResponse<Void>> handleMembersException(
		MemberException memberException) {
		log.error("handleMembersException: ", memberException);

		GenericResponse<Void> genericResponse = GenericResponse.fail(
			memberException.getMemberErrorCode().getCode(),
			memberException.getMessage()
		);

		return ResponseEntity.status(memberException.getStatus().value())
			.body(genericResponse);
	}


	/**
	 * StorageException 처리 핸들러 입니다.
	 *
	 * @param storageException {@link StorageException}
	 * @return {@link ResponseEntity<GenericResponse>}
	 */
	@ExceptionHandler(StorageException.class)
	public ResponseEntity<GenericResponse<Void>> handlerStorageException(
		StorageException storageException) {
		log.error("handlerStorageException: ", storageException);

		GenericResponse<Void> genericResponse = GenericResponse.fail(
			storageException.getStorageErrorCode().getCode(),
			storageException.getMessage()
		);

		return ResponseEntity.status(storageException.getStatus().value())
			.body(genericResponse);
	}

	/**
	 * Validation 예외 처리 핸들러 입니다.
	 *
	 * @param ex      Exception
	 * @param request HttpServletRequest
	 * @return {@link ResponseEntity<GenericResponse<List<com.backend.global.response.ErrorDetail>}
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<GenericResponse<List<ErrorDetail>>> handlerMethodArgumentNotValidException(
		MethodArgumentNotValidException ex,
		HttpServletRequest request) {
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