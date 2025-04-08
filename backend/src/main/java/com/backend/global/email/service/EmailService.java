package com.backend.global.email.service;

public interface EmailService {

	/**
	 * 낚시 동출 모집 완료 안내 메일을 비동기적으로 전송합니다.
	 *
	 * <p>해당 메일은 모집 상태가 {@code COMPLETED}로 변경된 게시글에 대해,
	 * 신청자에게 안내하기 위한 용도로 사용됩니다. 제목에는 게시글 제목이 포함되며,
	 * 고정된 본문 내용으로 전송됩니다.</p>
	 *
	 * <p>Spring의 {@code @Async}를 사용해 별도의 스레드에서 메일을 비동기 전송합니다.</p>
	 *
	 * @param subject 메일 제목에 포함될 낚시 게시글 제목
	 * @param receiver 수신자의 이메일 주소
	 * @see org.springframework.scheduling.annotation.Async
	 * @see org.springframework.mail.SimpleMailMessage
	 */
	void sendFishingTripPostEmail(final String subject, final String receiver);
}
