package com.backend.domain.shipfishposts.repository;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import com.backend.domain.fish.entity.Fish;
import com.backend.domain.fish.repository.FishRepository;
import com.backend.domain.ship.dto.response.ShipResponse;
import com.backend.domain.ship.entity.Ship;
import com.backend.domain.ship.repository.ShipRepository;
import com.backend.domain.shipfishingpost.dto.request.ShipFishingPostRequest;
import com.backend.domain.shipfishingpost.dto.response.ShipFishingPostResponse;
import com.backend.domain.shipfishingpost.entity.ShipFishingPost;
import com.backend.domain.shipfishingpost.repository.ShipFishingPostRepository;
import com.backend.domain.shipfishingpostfish.entity.ShipFishingPostFish;
import com.backend.domain.shipfishingpostfish.repository.ShipFishingPostFishRepository;
import com.backend.global.config.QuerydslConfig;
import com.backend.global.exception.GlobalErrorCode;
import com.backend.global.exception.GlobalException;
import com.backend.global.util.BaseTest;
import com.backend.global.util.pageutil.Page;

import lombok.extern.slf4j.Slf4j;

import com.navercorp.fixturemonkey.ArbitraryBuilder;

@Slf4j
@Import(QuerydslConfig.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Repository.class))
public class ShipFishingPostRepositoryTest extends BaseTest {

	@Autowired
	private ShipRepository shipRepository;

	@Autowired
	private FishRepository fishRepository;

	@Autowired
	private ShipFishingPostRepository shipFishingPostRepository;

	@Autowired
	private ShipFishingPostFishRepository shipFishingPostFishRepository;

	private final ArbitraryBuilder<ShipFishingPost> arbitraryBuilder = fixtureMonkeyBuilder
		.giveMeBuilder(ShipFishingPost.class)
		.set("subject", "1555");

	@BeforeAll
	void init() {
		for (int i = 0; i < 20; i++) {
			fishRepository.save(
				fixtureMonkeyBuilder.giveMeBuilder(Fish.class)
					.set("fishId", null)
					.set("description", "test description")
					.set("icon", "icon")
					.set("spawnLocation", "test")
					.set("name", String.format("fish %d", i))
					.sample()
			);
		}
	}

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

	@Test
	@DisplayName("선상 낚시 게시글 목록 조회 [페이징 여부 확인] [Repository] - Success")
	void t04() {
		// given
		for (long i = 1; i <= 14; i++) {
			ShipFishingPost givenPost = arbitraryBuilder
				.set("shipFishingPostId", null)
				.set("shipId", i)
				.set("fishList", List.of(1L))
				.sample();
			ShipFishingPost savedShipFishingPost = shipFishingPostRepository.save(givenPost);

			shipFishingPostFishRepository.save(fixtureMonkeyBuilder
				.giveMeBuilder(ShipFishingPostFish.class)
				.set("shipFishingPostId", savedShipFishingPost.getShipFishingPostId())
				.set("fishId", 1L)
				.sample());
		}

		ShipFishingPostRequest.Search givenRequestDto = ShipFishingPostRequest.Search.builder().build();

		Pageable pageable = PageRequest.of(2, 6, Sort.by("createdAt").descending());

		// when
		Slice<ShipFishingPostResponse.DetailPage> savedSlice = shipFishingPostRepository.findAllBySearchAndCondition(
			givenRequestDto, pageable);

		// Then
		assertThat(savedSlice.getContent().size()).isEqualTo(2);
		assertThat(savedSlice.getNumber()).isEqualTo(2);
		assertThat(savedSlice.getSize()).isEqualTo(6);
		assertThat(savedSlice.hasNext()).isEqualTo(false);
	}

	@Test
	@DisplayName("선상 낚시 게시글 목록 조회 [Condition - 대상 어종] [Repository] - Success")
	void t05() {
		// Given
		List<ShipFishingPostFish> givenShipFishingPostFishList = new ArrayList<>();

		for (long i = 1; i <= 14; i++) {
			ShipFishingPost givenPost = arbitraryBuilder
				.set("shipFishingPostId", null)
				.set("shipId", i)
				.set("fishList", List.of(i, i + 1))
				.sample();
			ShipFishingPost savedShipFishingPost = shipFishingPostRepository.save(givenPost);

			for (int j = 0; j < 2; j++) {
				givenShipFishingPostFishList.add(fixtureMonkeyBuilder
					.giveMeBuilder(ShipFishingPostFish.class)
					.set("shipFishingPostId", savedShipFishingPost.getShipFishingPostId())
					.set("fishId", i + j)
					.sample());
			}
		}

		shipFishingPostFishRepository.saveAll(givenShipFishingPostFishList);

		ShipFishingPostRequest.Search givenRequestDto = ShipFishingPostRequest.Search.builder().fishId(5L).build();

		Pageable pageable = PageRequest.of(0, 6, Sort.by("createdAt").descending());

		Slice<ShipFishingPostResponse.DetailPage> savedSlice = shipFishingPostRepository.findAllBySearchAndCondition(
			givenRequestDto, pageable);

		// Then
		assertThat(savedSlice.getContent().size()).isEqualTo(2);
		assertThat(savedSlice.getNumber()).isEqualTo(0);
		assertThat(savedSlice.getSize()).isEqualTo(6);
		assertThat(savedSlice.hasNext()).isEqualTo(false);
	}

	@Test
	@DisplayName("선상 낚시 게시글 목록 조회 [Condition - 가격 (minPrice)] [Repository] - Success")
	void t06() {
		// Given
		for (long i = 1; i <= 14; i++) {
			ShipFishingPost givenPost = arbitraryBuilder
				.set("shipFishingPostId", null)
				.set("shipId", i)
				.set("price", 20000 * i)
				.set("fishList", List.of(1L))
				.sample();
			ShipFishingPost savedShipFishingPost = shipFishingPostRepository.save(givenPost);

			shipFishingPostFishRepository.save(fixtureMonkeyBuilder
				.giveMeBuilder(ShipFishingPostFish.class)
				.set("shipFishingPostId", savedShipFishingPost.getShipFishingPostId())
				.set("fishId", 1L)
				.sample());
		}

		ShipFishingPostRequest.Search givenRequestDto = ShipFishingPostRequest.Search.builder()
			.minPrice(200000L)
			.build();

		Pageable pageable = PageRequest.of(0, 6, Sort.by("createdAt").descending());

		Slice<ShipFishingPostResponse.DetailPage> savedSlice = shipFishingPostRepository.findAllBySearchAndCondition(
			givenRequestDto, pageable);

		// Then
		assertThat(savedSlice.getContent().size()).isEqualTo(5);
		assertThat(savedSlice.getNumber()).isEqualTo(0);
		assertThat(savedSlice.getSize()).isEqualTo(6);
		assertThat(savedSlice.hasNext()).isEqualTo(false);
	}

	@Test
	@DisplayName("선상 낚시 게시글 목록 조회 [Condition - 가격 (maxPrice)] [Repository] - Success")
	void t07() {
		// Given
		for (long i = 1; i <= 14; i++) {
			ShipFishingPost givenPost = arbitraryBuilder
				.set("shipFishingPostId", null)
				.set("shipId", i)
				.set("price", 20000 * i)
				.set("fishList", List.of(1L))
				.sample();
			ShipFishingPost savedShipFishingPost = shipFishingPostRepository.save(givenPost);

			shipFishingPostFishRepository.save(fixtureMonkeyBuilder
				.giveMeBuilder(ShipFishingPostFish.class)
				.set("shipFishingPostId", savedShipFishingPost.getShipFishingPostId())
				.set("fishId", 1L)
				.sample());
		}

		ShipFishingPostRequest.Search givenRequestDto = ShipFishingPostRequest.Search.builder()
			.maxPrice(200000L)
			.build();

		Pageable pageable = PageRequest.of(0, 6, Sort.by("createdAt").descending());

		Slice<ShipFishingPostResponse.DetailPage> savedSlice = shipFishingPostRepository.findAllBySearchAndCondition(
			givenRequestDto, pageable);

		// Then
		assertThat(savedSlice.getContent().size()).isEqualTo(6);
		assertThat(savedSlice.getNumber()).isEqualTo(0);
		assertThat(savedSlice.getSize()).isEqualTo(6);
		assertThat(savedSlice.hasNext()).isEqualTo(true);
	}

	@Test
	@DisplayName("선상 낚시 게시글 목록 조회 [Condition - 가격 (minPrice ~ maxPrice)] [Repository] - Success")
	void t08() {
		// Given
		for (long i = 1; i <= 14; i++) {
			ShipFishingPost givenPost = arbitraryBuilder
				.set("shipFishingPostId", null)
				.set("shipId", i)
				.set("price", 20000 * i)
				.set("fishList", List.of(1L))
				.sample();
			ShipFishingPost savedShipFishingPost = shipFishingPostRepository.save(givenPost);

			shipFishingPostFishRepository.save(fixtureMonkeyBuilder
				.giveMeBuilder(ShipFishingPostFish.class)
				.set("shipFishingPostId", savedShipFishingPost.getShipFishingPostId())
				.set("fishId", 1L)
				.sample());
		}

		ShipFishingPostRequest.Search givenRequestDto = ShipFishingPostRequest.Search.builder()
			.minPrice(200000L)
			.maxPrice(220000L)
			.build();

		Pageable pageable = PageRequest.of(0, 6, Sort.by("createdAt").descending());

		Slice<ShipFishingPostResponse.DetailPage> savedSlice = shipFishingPostRepository.findAllBySearchAndCondition(
			givenRequestDto, pageable);

		// Then
		assertThat(savedSlice.getContent().size()).isEqualTo(2);
		assertThat(savedSlice.getNumber()).isEqualTo(0);
		assertThat(savedSlice.getSize()).isEqualTo(6);
		assertThat(savedSlice.hasNext()).isEqualTo(false);
	}

	@Test
	@DisplayName("선상 낚시 게시글 목록 조회 [Sort - price, desc] [Repository] - Success")
	void t09() {
		// Given
		for (long i = 1; i <= 14; i++) {
			ShipFishingPost givenPost = arbitraryBuilder
				.set("shipFishingPostId", null)
				.set("shipId", i)
				.set("price", 20000 * i)
				.set("fishList", List.of(1L))
				.sample();
			ShipFishingPost savedShipFishingPost = shipFishingPostRepository.save(givenPost);

			shipFishingPostFishRepository.save(fixtureMonkeyBuilder
				.giveMeBuilder(ShipFishingPostFish.class)
				.set("shipFishingPostId", savedShipFishingPost.getShipFishingPostId())
				.set("fishId", 1L)
				.sample());
		}

		ShipFishingPostRequest.Search givenRequestDto = ShipFishingPostRequest.Search.builder().build();

		Pageable pageable = PageRequest.of(0, 6, Sort.by("price").descending());

		Slice<ShipFishingPostResponse.DetailPage> savedSlice = shipFishingPostRepository.findAllBySearchAndCondition(
			givenRequestDto, pageable);

		// Then
		assertThat(savedSlice.getContent().get(0).price()).isEqualTo(280000L);
	}

	@Test
	@DisplayName("선상 낚시 게시글 목록 조회 [Sort - price, asc] [Repository] - Success")
	void t10() {
		// Given
		for (long i = 1; i <= 14; i++) {
			ShipFishingPost givenPost = arbitraryBuilder
				.set("shipFishingPostId", null)
				.set("shipId", i)
				.set("price", 20000 * i)
				.set("fishList", List.of(1L))
				.sample();
			ShipFishingPost savedShipFishingPost = shipFishingPostRepository.save(givenPost);

			shipFishingPostFishRepository.save(fixtureMonkeyBuilder
				.giveMeBuilder(ShipFishingPostFish.class)
				.set("shipFishingPostId", savedShipFishingPost.getShipFishingPostId())
				.set("fishId", 1L)
				.sample());
		}

		ShipFishingPostRequest.Search givenRequestDto = ShipFishingPostRequest.Search.builder().build();

		Pageable pageable = PageRequest.of(0, 6, Sort.by("price").ascending());

		Slice<ShipFishingPostResponse.DetailPage> savedSlice = shipFishingPostRepository.findAllBySearchAndCondition(
			givenRequestDto, pageable);

		// Then
		assertThat(savedSlice.getContent().get(0).price()).isEqualTo(20000L);
	}

	@Test
	@DisplayName("선상 낚시 게시글 목록 조회 [Condition - 평점] [Repository] - Success")
	void t11() {
		// Given
		for (long i = 1; i <= 10; i++) {
			ShipFishingPost givenPost = arbitraryBuilder
				.set("shipFishingPostId", null)
				.set("shipId", i)
				.set("reviewEverRate", 0.5 * i)
				.set("fishList", List.of(1L))
				.sample();
			ShipFishingPost savedShipFishingPost = shipFishingPostRepository.save(givenPost);

			shipFishingPostFishRepository.save(fixtureMonkeyBuilder
				.giveMeBuilder(ShipFishingPostFish.class)
				.set("shipFishingPostId", savedShipFishingPost.getShipFishingPostId())
				.set("fishId", 1L)
				.sample());
		}

		ShipFishingPostRequest.Search givenRequestDto = ShipFishingPostRequest.Search.builder().minRating(3D).build();

		Pageable pageable = PageRequest.of(0, 6, Sort.by("createdAt").descending());

		Slice<ShipFishingPostResponse.DetailPage> savedSlice = shipFishingPostRepository.findAllBySearchAndCondition(
			givenRequestDto, pageable);

		// Then
		assertThat(savedSlice.getContent().size()).isEqualTo(5);
		assertThat(savedSlice.getNumber()).isEqualTo(0);
		assertThat(savedSlice.getSize()).isEqualTo(6);
		assertThat(savedSlice.hasNext()).isEqualTo(false);
	}

	@Test
	@DisplayName("선상 낚시 게시글 목록 조회 [Sort - reviewEverRate, desc] [Repository] - Success")
	void t12() {
		// Given
		for (long i = 1; i <= 10; i++) {
			ShipFishingPost givenPost = arbitraryBuilder
				.set("shipFishingPostId", null)
				.set("shipId", i)
				.set("reviewEverRate", 0.5 * i)
				.set("fishList", List.of(1L))
				.sample();
			ShipFishingPost savedShipFishingPost = shipFishingPostRepository.save(givenPost);

			shipFishingPostFishRepository.save(fixtureMonkeyBuilder
				.giveMeBuilder(ShipFishingPostFish.class)
				.set("shipFishingPostId", savedShipFishingPost.getShipFishingPostId())
				.set("fishId", 1L)
				.sample());
		}

		ShipFishingPostRequest.Search givenRequestDto = ShipFishingPostRequest.Search.builder().build();

		Pageable pageable = PageRequest.of(0, 6, Sort.by("reviewEverRate").descending());

		Slice<ShipFishingPostResponse.DetailPage> savedSlice = shipFishingPostRepository.findAllBySearchAndCondition(
			givenRequestDto, pageable);

		// Then
		assertThat(savedSlice.getContent().get(0).reviewEverRate()).isEqualTo(5D);
	}

	@Test
	@DisplayName("선상 낚시 게시글 목록 조회 [Sort - reviewEverRate, asc] [Repository] - Success")
	void t13() {
		// Given
		for (long i = 1; i <= 10; i++) {
			ShipFishingPost givenPost = arbitraryBuilder
				.set("shipFishingPostId", null)
				.set("shipId", i)
				.set("reviewEverRate", 0.5 * i)
				.set("fishList", List.of(1L))
				.sample();
			ShipFishingPost savedShipFishingPost = shipFishingPostRepository.save(givenPost);

			shipFishingPostFishRepository.save(fixtureMonkeyBuilder
				.giveMeBuilder(ShipFishingPostFish.class)
				.set("shipFishingPostId", savedShipFishingPost.getShipFishingPostId())
				.set("fishId", 1L)
				.sample());
		}

		ShipFishingPostRequest.Search givenRequestDto = ShipFishingPostRequest.Search.builder().build();

		Pageable pageable = PageRequest.of(0, 6, Sort.by("reviewEverRate").ascending());

		Slice<ShipFishingPostResponse.DetailPage> savedSlice = shipFishingPostRepository.findAllBySearchAndCondition(
			givenRequestDto, pageable);

		// Then
		assertThat(savedSlice.getContent().get(0).reviewEverRate()).isEqualTo(0.5D);
	}

	@Test
	@DisplayName("선상 낚시 게시글 목록 조회 [Condition - 소요 시간] [Repository] - Success")
	void t14() {
		// Given
		for (long i = 1; i <= 10; i++) {
			int hour = (int)(i / 2);
			int minute = (int)(30 * i) % 60;
			ShipFishingPost givenPost = arbitraryBuilder
				.set("shipFishingPostId", null)
				.set("shipId", i)
				.set("durationTime", LocalTime.of(hour, minute))
				.set("fishList", List.of(1L))
				.sample();
			ShipFishingPost savedShipFishingPost = shipFishingPostRepository.save(givenPost);

			shipFishingPostFishRepository.save(fixtureMonkeyBuilder
				.giveMeBuilder(ShipFishingPostFish.class)
				.set("shipFishingPostId", savedShipFishingPost.getShipFishingPostId())
				.set("fishId", 1L)
				.sample());
		}

		ShipFishingPostRequest.Search givenRequestDto = ShipFishingPostRequest.Search.builder()
			.duration(LocalTime.of(2, 30))
			.build();

		Pageable pageable = PageRequest.of(0, 6, Sort.by("createdAt").descending());

		Slice<ShipFishingPostResponse.DetailPage> savedSlice = shipFishingPostRepository.findAllBySearchAndCondition(
			givenRequestDto, pageable);

		// Then
		assertThat(savedSlice.getContent().size()).isEqualTo(5);
		assertThat(savedSlice.getNumber()).isEqualTo(0);
		assertThat(savedSlice.getSize()).isEqualTo(6);
		assertThat(savedSlice.hasNext()).isEqualTo(false);
	}

	@Test
	@DisplayName("선상 낚시 게시글 목록 조회 [Condition - 출항 지역] [Repository] - Success")
	void t15() {
		// Given
		for (long i = 1; i <= 20; i++) {
			ShipFishingPost givenPost = arbitraryBuilder
				.set("shipFishingPostId", null)
				.set("shipId", i)
				.set("location", String.format("test%d", i))
				.set("fishList", List.of(1L))
				.sample();
			ShipFishingPost savedShipFishingPost = shipFishingPostRepository.save(givenPost);

			shipFishingPostFishRepository.save(fixtureMonkeyBuilder
				.giveMeBuilder(ShipFishingPostFish.class)
				.set("shipFishingPostId", savedShipFishingPost.getShipFishingPostId())
				.set("fishId", 1L)
				.sample());
		}

		ShipFishingPostRequest.Search givenRequestDto = ShipFishingPostRequest.Search.builder().location("t2").build();

		Pageable pageable = PageRequest.of(0, 6, Sort.by("createdAt").descending());

		Slice<ShipFishingPostResponse.DetailPage> savedSlice = shipFishingPostRepository.findAllBySearchAndCondition(
			givenRequestDto, pageable);

		// Then
		assertThat(savedSlice.getContent().size()).isEqualTo(2);
		assertThat(savedSlice.getNumber()).isEqualTo(0);
		assertThat(savedSlice.getSize()).isEqualTo(6);
		assertThat(savedSlice.hasNext()).isEqualTo(false);
	}

	@Test
	@DisplayName("선상 낚시 게시글 목록 조회 [Search - keyword] [Repository] - Success")
	void t16() {
		// Given
		for (long i = 1; i <= 20; i++) {
			ShipFishingPost givenPost = arbitraryBuilder
				.set("shipFishingPostId", null)
				.set("shipId", i)
				.set("subject", String.format("test%d", i))
				.set("fishList", List.of(1L))
				.sample();
			ShipFishingPost savedShipFishingPost = shipFishingPostRepository.save(givenPost);

			shipFishingPostFishRepository.save(fixtureMonkeyBuilder
				.giveMeBuilder(ShipFishingPostFish.class)
				.set("shipFishingPostId", savedShipFishingPost.getShipFishingPostId())
				.set("fishId", 1L)
				.sample());
		}

		ShipFishingPostRequest.Search givenRequestDto = ShipFishingPostRequest.Search.builder()
			.keyword("test2")
			.build();

		Pageable pageable = PageRequest.of(0, 6, Sort.by("createdAt").descending());

		Slice<ShipFishingPostResponse.DetailPage> savedSlice = shipFishingPostRepository.findAllBySearchAndCondition(
			givenRequestDto, pageable);

		// Then
		assertThat(savedSlice.getContent().size()).isEqualTo(2);
		assertThat(savedSlice.getNumber()).isEqualTo(0);
		assertThat(savedSlice.getSize()).isEqualTo(6);
		assertThat(savedSlice.hasNext()).isEqualTo(false);
	}

	@Test
	@DisplayName("선상 낚시 게시글 목록 조회 [Sort - wrong] [Repository] - Fail")
	void t17() {
		// Given
		for (long i = 1; i <= 20; i++) {
			ShipFishingPost givenPost = arbitraryBuilder
				.set("shipFishingPostId", null)
				.set("shipId", i)
				.set("location", String.format("test%d", i))
				.sample();
			ShipFishingPost savedShipFishingPost = shipFishingPostRepository.save(givenPost);

			shipFishingPostFishRepository.save(fixtureMonkeyBuilder
				.giveMeBuilder(ShipFishingPostFish.class)
				.set("shipFishingPostId", savedShipFishingPost.getShipFishingPostId())
				.set("fishId", 1L)
				.sample());
		}

		ShipFishingPostRequest.Search givenRequestDto = ShipFishingPostRequest.Search.builder().build();

		Pageable pageable = PageRequest.of(0, 6, Sort.by("wrong").descending());

		// Then
		assertThatThrownBy(() -> shipFishingPostRepository.findAllBySearchAndCondition(givenRequestDto, pageable))
			.isInstanceOf(GlobalException.class)
			.hasFieldOrPropertyWithValue("globalErrorCode", GlobalErrorCode.WRONG_SORT_CONDITION)
			.hasMessageContaining(GlobalErrorCode.WRONG_SORT_CONDITION.getMessage());
	}
}
