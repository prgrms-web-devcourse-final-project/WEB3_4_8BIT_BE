package com.backend.domain.reservationdate.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.domain.reservationdate.repository.ReservationDateRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservationDateScheduler {

	private final ReservationDateRepository reservationDateRepository;

	/**
	 * 매주 월요일 새벽 2시에 게시글 정보가 없는 예약 일자 데이터 삭제 스케쥴러 메서드
	 *
	 */
	@Scheduled(cron = "0 0 2 ? * 1", zone = "Asia/Seoul")
	@Transactional
	public void deleteOrphanReservationDate() {

		reservationDateRepository.deleteOrphanReservationDate();
	}
}
