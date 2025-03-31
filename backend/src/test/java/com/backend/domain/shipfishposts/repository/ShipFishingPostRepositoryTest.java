package com.backend.domain.shipfishposts.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Repository;

import com.backend.domain.ship.dto.response.ShipResponse;
import com.backend.domain.ship.entity.Ship;
import com.backend.domain.ship.repository.ShipRepository;
import com.backend.domain.shipfishingpost.dto.response.ShipFishingPostResponse;
import com.backend.domain.shipfishingpost.entity.ShipFishingPost;
import com.backend.domain.shipfishingpost.repository.ShipFishingPostRepository;
import com.backend.global.util.BaseTest;
import com.backend.global.config.QuerydslConfig;

import lombok.extern.slf4j.Slf4j;

import com.navercorp.fixturemonkey.ArbitraryBuilder;

@Slf4j
@Import(QuerydslConfig.class)
@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Repository.class))
public class ShipFishingPostRepositoryTest extends BaseTest {

	@Autowired
	private ShipRepository shipRepository;

	@Autowired
	private ShipFishingPostRepository shipFishingPostRepository;

	private final ArbitraryBuilder<ShipFishingPost> arbitraryBuilder = fixtureMonkeyBuilder
		.giveMeBuilder(ShipFishingPost.class)
		.set("subject", "1555");

	@Test
	@DisplayName("선상 낚시 게시글 저장 [Repository] - Success")
	void t01() {
		// Given
		ShipFishingPost givenShipFishingPost = arbitraryBuilder
			.set("shipFishingPostId", null)
			.sample();

		// When
		ShipFishingPost savedShipFishingPost = shipFishingPostRepository.save(givenShipFishingPost);

		// Then
		assertThat(savedShipFishingPost).isNotNull();
		assertThat(savedShipFishingPost.getShipFishingPostId()).isNotNull();
	}

	@Test
	@DisplayName("선상 낚시 게시글 상세 조회 [ShipFishingPostResponse.Detail] [Repository] - Success")
	void t02() {
		// Given
		ShipFishingPost givenShipFishingPost = arbitraryBuilder
			.set("shipFishingPostId", null)
			.sample();

		ShipFishingPost savedShipFishingPost = shipFishingPostRepository.save(givenShipFishingPost);

		// When
		ShipFishingPostResponse.Detail findDetail = shipFishingPostRepository
			.findDetailById(savedShipFishingPost.getShipFishingPostId()).get();

		// Then
		assertThat(findDetail.shipFishingPostId()).isEqualTo(savedShipFishingPost.getShipFishingPostId());
		assertThat(findDetail.subject()).isEqualTo(savedShipFishingPost.getSubject());
		assertThat(findDetail.content()).isEqualTo(savedShipFishingPost.getContent());
		assertThat(findDetail.imageList().toString()).isEqualTo(savedShipFishingPost.getImageList().toString());
		assertThat(findDetail.startTime()).isEqualTo(savedShipFishingPost.getStartTime());
		assertThat(findDetail.durationTime()).isEqualTo(savedShipFishingPost.getDurationTime());
		assertThat(findDetail.maxGuestCount()).isEqualTo(savedShipFishingPost.getMaxGuestCount());
		assertThat(findDetail.reviewEverRate()).isEqualTo(savedShipFishingPost.getReviewEverRate());
	}

	@Test
	@DisplayName("선상 낚시 게시글 상세 조회 [ShipFishingPostResponse.DetailAll] [Repository] - Success")
	void t03() {
		// Given
		Ship givenShip = fixtureMonkeyBuilder.giveMeBuilder(Ship.class)
			.set("shipId", null)
			.set("shipName", "나로호")
			.set("shipNumber", "12345-6789")
			.set("departurePort", "선착장")
			.sample();

		Ship savedShip = shipRepository.save(givenShip);

		ShipFishingPost givenShipFishingPost = arbitraryBuilder
			.set("shipFishingPostId", null)
			.set("shipId", savedShip.getShipId())
			.sample();

		ShipFishingPost savedShipFishingPost = shipFishingPostRepository.save(givenShipFishingPost);

		// When
		Optional<ShipFishingPostResponse.DetailAll> findOptionalDetailAll = shipFishingPostRepository
			.findDetailAllById(savedShipFishingPost.getShipFishingPostId());

		// Then
		assertThat(findOptionalDetailAll).isPresent();

		ShipFishingPostResponse.DetailAll findDetailAll = findOptionalDetailAll.get();

		// 선상 낚시 게시글 정보 검증
		ShipFishingPostResponse.Detail postDetail = findDetailAll.detailShipFishingPost();

		assertThat(postDetail.shipFishingPostId()).isEqualTo(savedShipFishingPost.getShipFishingPostId());
		assertThat(postDetail.subject()).isEqualTo(savedShipFishingPost.getSubject());
		assertThat(postDetail.content()).isEqualTo(savedShipFishingPost.getContent());
		assertThat(postDetail.imageList().toString()).isEqualTo(savedShipFishingPost.getImageList().toString());
		assertThat(postDetail.startTime()).isEqualTo(savedShipFishingPost.getStartTime());
		assertThat(postDetail.durationTime()).isEqualTo(savedShipFishingPost.getDurationTime());
		assertThat(postDetail.maxGuestCount()).isEqualTo(savedShipFishingPost.getMaxGuestCount());
		assertThat(postDetail.reviewEverRate()).isEqualTo(savedShipFishingPost.getReviewEverRate());

		// 선박 정보 검증
		ShipResponse.Detail shipDetail = findDetailAll.detailShip();
		assertThat(shipDetail.shipId()).isEqualTo(savedShip.getShipId());
		assertThat(shipDetail.shipName()).isEqualTo(savedShip.getShipName());
		assertThat(shipDetail.shipNumber()).isEqualTo(savedShip.getShipNumber());
		assertThat(shipDetail.departurePort()).isEqualTo(savedShip.getDeparturePort());
		assertThat(shipDetail.publicRestroom()).isEqualTo(savedShip.getPublicRestroom());
		assertThat(shipDetail.loungeArea()).isEqualTo(savedShip.getLoungeArea());
		assertThat(shipDetail.kitchenFacility()).isEqualTo(savedShip.getKitchenFacility());
		assertThat(shipDetail.fishingChair()).isEqualTo(savedShip.getFishingChair());
		assertThat(shipDetail.passengerInsurance()).isEqualTo(savedShip.getPassengerInsurance());
		assertThat(shipDetail.fishingGearRental()).isEqualTo(savedShip.getFishingGearRental());
		assertThat(shipDetail.mealProvided()).isEqualTo(savedShip.getMealProvided());
		assertThat(shipDetail.parkingAvailable()).isEqualTo(savedShip.getParkingAvailable());

		// Todo : 멤버 정보 검증
	}
}
