package com.backend.domain.member.exception;

import org.springframework.http.HttpStatus;
import lombok.Getter;

@Getter
public class MembersException extends RuntimeException {
	private final MembersErrorCode membersErrorCode;

	public MembersException(MembersErrorCode membersErrorCode) {
		super(membersErrorCode.getMessage());
		this.membersErrorCode = membersErrorCode;
	}

	public HttpStatus getStatus() {
		return membersErrorCode.getHttpStatus();
	}
}
