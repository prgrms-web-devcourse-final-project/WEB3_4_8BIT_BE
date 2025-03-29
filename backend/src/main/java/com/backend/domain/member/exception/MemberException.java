package com.backend.domain.member.exception;

import org.springframework.http.HttpStatus;
import lombok.Getter;

@Getter
public class MemberException extends RuntimeException {
	private final MemberErrorCode memberErrorCode;

	public MemberException(MemberErrorCode memberErrorCode) {
		super(memberErrorCode.getMessage());
		this.memberErrorCode = memberErrorCode;
	}

	public HttpStatus getStatus() {
		return memberErrorCode.getHttpStatus();
	}
}
