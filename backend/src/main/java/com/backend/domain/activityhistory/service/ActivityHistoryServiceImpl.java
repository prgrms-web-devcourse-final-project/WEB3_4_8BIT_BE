package com.backend.domain.activityhistory.service;

import org.springframework.stereotype.Service;

import com.backend.domain.activityhistory.converter.ActivityHistoryConverter;
import com.backend.domain.activityhistory.domain.ActivityType;
import com.backend.domain.activityhistory.dto.request.ActivityHistoryRequest;
import com.backend.domain.activityhistory.dto.response.ActivityHistoryResponse;
import com.backend.domain.activityhistory.entity.ActivityHistory;
import com.backend.domain.activityhistory.repository.ActivityHistoryRepository;
import com.backend.domain.activityhistory.util.ActivityDescriptionBuilder;
import com.backend.domain.fish.repository.FishRepository;
import com.backend.domain.fishencyclopedia.entity.FishEncyclopedia;
import com.backend.domain.fishingtrippost.entity.FishingTripPost;
import com.backend.domain.fishpoint.repository.FishPointRepository;
import com.backend.domain.reservation.entity.Reservation;
import com.backend.domain.shipfishingpost.repository.ShipFishingPostRepository;
import com.backend.global.dto.request.GlobalRequest;
import com.backend.global.dto.response.ScrollResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ActivityHistoryServiceImpl implements ActivityHistoryService {

	private final ActivityHistoryRepository activityHistoryRepository;
	private final FishPointRepository fishPointRepository;
	private final FishRepository fishRepository;
	private final ShipFishingPostRepository shipFishingPostRepository;

	public void createActivityHistory(
		final Object target
	) {
		ActivityHistory activityHistory = getActivityHistory(target);

		if (activityHistory != null) {
			activityHistoryRepository.save(activityHistory);
		}
	}

	@Override
	public ScrollResponse<ActivityHistoryResponse.Detail> getDetailList(
		final GlobalRequest.CursorRequest cursorRequestDto,
		final ActivityHistoryRequest.Search requestDto,
		final Long memberId
	) {

		return activityHistoryRepository.findDetail(cursorRequestDto, requestDto, memberId);
	}

	private ActivityHistory getActivityHistory(final Object target) {
		//물고기 도감 활동 기록 생성
		if (target instanceof FishEncyclopedia fishEncyclopedia) {
			Long fishId = fishEncyclopedia.getFishId();
			Long fishPointId = fishEncyclopedia.getFishPointId();

			String fishPointDetailName = fishPointRepository.findFishPointDetailNameByFishPointId(fishPointId);
			String fishName = fishRepository.findNameById(fishId);
			Integer length = fishEncyclopedia.getLength();

			String description = ActivityDescriptionBuilder.createFishEncyclopedia(
				fishName,
				fishPointDetailName,
				length
			);

			return ActivityHistoryConverter.from(
				ActivityType.FISH_ENCYCLOPEDIA,
				fishEncyclopedia.getFishEncyclopediaId(),
				description,
				fishEncyclopedia.getMemberId()
			);
		}

		// 동출 활동 기록 생성
		if (target instanceof FishingTripPost fishingTripPost) {

			String subject = fishingTripPost.getSubject();

			return ActivityHistoryConverter.from(
				ActivityType.FISHING_TRIP_POST,
				fishingTripPost.getFishingTripPostId(),
				subject,
				fishingTripPost.getMemberId()
			);
		}

		// 예약 활동 기록 생성
		if (target instanceof Reservation reservation) {
			Long shipFishingPostId = reservation.getShipFishingPostId();

			String subject = shipFishingPostRepository.findSubjectByShipFishingPostId(
				shipFishingPostId);

			String reservationDate = reservation.getReservationDate().toString();

			String description = ActivityDescriptionBuilder.createReservation(subject, reservationDate);

			return ActivityHistoryConverter.from(
				ActivityType.RESERVATION,
				reservation.getReservationId(),
				description,
				reservation.getMemberId()
			);
		}

		return null;
	}
}
