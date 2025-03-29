package com.backend.domain.shipfishingpost.service;

import java.time.Duration;
import java.time.LocalTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.domain.shipfishingpost.converter.ShipFishingPostConverter;
import com.backend.domain.shipfishingpost.dto.request.ShipFishingPostRequest;
import com.backend.domain.shipfishingpost.entity.ShipFishingPost;
import com.backend.domain.shipfishingpost.repository.ShipFishingPostRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShipFishingPostServiceImpl implements ShipFishingPostService {

	private final ShipFishingPostRepository shipFishingPostRepository;

	@Override
	@Transactional
	public Long save(final ShipFishingPostRequest.Create requestDto) {

		String durationTime = durationToString(requestDto.startTime(), requestDto.endTime());

		ShipFishingPost entity = ShipFishingPostConverter.fromShipFishPostsRequestCreate(requestDto, durationTime);

		// Todo : 예약 불가날짜 관리 로직 필요
		// requestDto.unavailableDates();

		log.debug("Save ship fish posts: {}", entity);

		return shipFishingPostRepository.save(entity).getShipFishingPostId();
	}

	/**
	 * 시작시간, 마감시간 사이 기간 String 으로 변환 메서드
	 *
	 * @param startTime
	 * @param endTime
	 * @return {@link String duration}
	 */
	private String durationToString(final LocalTime startTime, final LocalTime endTime) {
		Duration durationTime = Duration.between(startTime, endTime);

		if (durationTime.isNegative()) {
			durationTime = durationTime.plusDays(1);
		}

		long totalMinutes = durationTime.toMinutes();
		long hours = totalMinutes / 60;
		long minutes = totalMinutes % 60;

		return String.format("%02d:%02d", hours, minutes);
	}
}