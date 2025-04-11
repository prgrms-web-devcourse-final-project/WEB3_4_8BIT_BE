package com.backend.domain.activityhistory.service;

import org.springframework.stereotype.Service;

import com.backend.domain.activityhistory.converter.ActivityHistoryConverter;
import com.backend.domain.activityhistory.domain.ActivityType;
import com.backend.domain.activityhistory.entity.ActivityHistory;
import com.backend.domain.activityhistory.repository.ActivityHistoryRepository;
import com.backend.domain.activityhistory.util.ActivityDescriptionBuilder;
import com.backend.domain.fish.repository.FishRepository;
import com.backend.domain.fishencyclopedia.entity.FishEncyclopedia;
import com.backend.domain.fishingtrippost.entity.FishingTripPost;
import com.backend.domain.fishpoint.repository.FishPointRepository;
import com.backend.domain.reservation.entity.Reservation;
import com.backend.domain.shipfishingpost.repository.ShipFishingPostRepository;

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

	private ActivityHistory getActivityHistory(Object target) {
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
				description
			);
		}

		// 동출 활동 기록 생성
		if (target instanceof FishingTripPost fishingTripPost) {

			String subject = fishingTripPost.getSubject();

			return ActivityHistoryConverter.from(
				ActivityType.FISHING_TRIP_POST,
				fishingTripPost.getFishingTripPostId(),
				subject
			);
		}

		// 예약 활동 기록 생성
		if (target instanceof Reservation reservation) {
			Long shipFishingPostId = reservation.getShipFishingPostId();

			String subject = shipFishingPostRepository.findSubjectByShipFishingPostId(
				shipFishingPostId);

			String reservationDate = reservation.getReservationDate().toString();

			String description = ActivityDescriptionBuilder.createReservation(subject, reservationDate);

			return ActivityHistoryConverter.from(ActivityType.RESERVATION, reservation.getReservationId(), description);
		}

		return null;
	}
}
