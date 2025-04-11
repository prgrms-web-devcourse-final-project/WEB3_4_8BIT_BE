package com.backend.domain.activityhistory.service;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.backend.domain.activityhistory.entity.ActivityHistory;
import com.backend.domain.activityhistory.repository.ActivityHistoryRepository;
import com.backend.domain.fish.repository.FishRepository;
import com.backend.domain.fishencyclopedia.entity.FishEncyclopedia;
import com.backend.domain.fishingtrippost.entity.FishingTripPost;
import com.backend.domain.fishpoint.repository.FishPointRepository;
import com.backend.domain.reservation.entity.Reservation;
import com.backend.domain.shipfishingpost.repository.ShipFishingPostRepository;
import com.backend.global.util.BaseTest;

@ExtendWith(MockitoExtension.class)
class ActivityHistoryServiceTest extends BaseTest {

	@Mock
	private ActivityHistoryRepository activityHistoryRepository;

	@Mock
	private FishPointRepository fishPointRepository;

	@Mock
	private FishRepository fishRepository;

	@Mock
	private ShipFishingPostRepository shipFishingPostRepository;

	@InjectMocks
	private ActivityHistoryServiceImpl activityHistoryService;

	@Test
	@DisplayName("활동 내역 저장 [FishEncyclopedia] [Service] - Success")
	void t01() {
		// Given
		FishEncyclopedia givenFishEncyclopedia = fixtureMonkeyBuilder.giveMeOne(FishEncyclopedia.class);

		String givenFishName = "방어";
		String givenFishPointDetailName = "내나로도 구룡마을 갯바위";

		when(fishRepository.findNameById(givenFishEncyclopedia.getFishId())).thenReturn(givenFishName);
		when(fishPointRepository.findFishPointDetailNameByFishPointId(givenFishEncyclopedia.getFishPointId()))
			.thenReturn(givenFishPointDetailName);
		when(activityHistoryRepository.save(any(ActivityHistory.class))).thenReturn(null);

		// When
		activityHistoryService.createActivityHistory(givenFishEncyclopedia);

		// Then
		verify(activityHistoryRepository, times(1)).save(any(ActivityHistory.class));
	}

	@Test
	@DisplayName("활동 내역 저장 [FishingTripPost] [Service] - Success")
	void t02() {
		// Given
		FishingTripPost givenFishingTripPost = fixtureMonkeyBuilder.giveMeOne(FishingTripPost.class);

		when(activityHistoryRepository.save(any(ActivityHistory.class))).thenReturn(null);

		// When
		activityHistoryService.createActivityHistory(givenFishingTripPost);

		// Then
		verify(activityHistoryRepository, times(1)).save(any(ActivityHistory.class));
	}

	@Test
	@DisplayName("활동 내역 저장 [Reservation] [Service] - Success")
	void t03() {
		// Given
		Reservation givenReservation = fixtureMonkeyBuilder.giveMeOne(Reservation.class);

		String givenSubject = "테스트 제목";

		when(shipFishingPostRepository.findSubjectByShipFishingPostId(givenReservation.getShipFishingPostId()))
			.thenReturn(givenSubject);
		
		when(activityHistoryRepository.save(any(ActivityHistory.class))).thenReturn(null);

		// When
		activityHistoryService.createActivityHistory(givenReservation);

		// Then
		verify(activityHistoryRepository, times(1)).save(any(ActivityHistory.class));
	}
}