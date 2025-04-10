package com.backend.domain.shipfishingpost.repository;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Repository;

import com.backend.domain.fish.entity.Fish;
import com.backend.domain.fish.repository.FishRepository;
import com.backend.domain.member.dto.MemberResponse;
import com.backend.domain.member.entity.Member;
import com.backend.domain.member.repository.MemberRepository;
import com.backend.domain.reservationdate.entity.ReservationDate;
import com.backend.domain.reservationdate.repository.ReservationDateRepository;
import com.backend.domain.review.entity.Review;
import com.backend.domain.review.repository.ReviewRepository;
import com.backend.domain.ship.dto.response.ShipResponse;
import com.backend.domain.ship.entity.Ship;
import com.backend.domain.ship.repository.ShipRepository;
import com.backend.domain.shipfishingpost.dto.request.ShipFishingPostRequest;
import com.backend.domain.shipfishingpost.dto.response.ShipFishingPostResponse;
import com.backend.domain.shipfishingpost.entity.ShipFishingPost;
import com.backend.global.config.QuerydslConfig;
import com.backend.global.dto.request.GlobalRequest;
import com.backend.global.dto.response.ScrollResponse;
import com.backend.global.util.BaseTest;

import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;

import com.navercorp.fixturemonkey.ArbitraryBuilder;

@Slf4j
@Import(QuerydslConfig.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Repository.class))
public class ShipFishingPostRepositoryTest extends BaseTest {

	@Autowired
	private EntityManager em;

	@Autowired
	private ShipRepository shipRepository;

	@Autowired
	private FishRepository fishRepository;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private ReviewRepository reviewRepository;

	@Autowired
	private ReservationDateRepository reservationDateRepository;

	@Autowired
	private ShipFishingPostRepository shipFishingPostRepository;

	@Autowired
	private ShipFishingPostJpaRepository shipFishingPostJpaRepository;

	private final ArbitraryBuilder<ShipFishingPost> arbitraryBuilder = fixtureMonkeyBuilder.giveMeBuilder(
		ShipFishingPost.class).set("subject", "1555");

	@BeforeAll
	void init() {
		for (int i = 0; i < 20; i++) {
			fishRepository.save(fixtureMonkeyBuilder.giveMeBuilder(Fish.class)
				.set("fishId", null)
				.set("description", "test description")
				.set("icon", "icon")
				.set("spawnLocation", "test")
				.set("name", String.format("fish %d", i))
				.sample());
		}

		List<Fish> givenFishList = new ArrayList<>();

		for (int i = 1; i <= 19; i++) {
			Fish fish = fixtureMonkeyBuilder.giveMeBuilder(Fish.class)
				.set("fishId", null)
				.set("name", "test" + i)
				.set("description", "test" + i)
				.set("fileId", i + 1L)
				.set("spawnLocation", "test" + i)
				.sample();

			givenFishList.add(fishRepository.save(fish));
		}

		List<ShipFishingPost> givenPostList = new ArrayList<>();

		for (int i = 1; i <= 15; i++) {
			ShipFishingPost post = arbitraryBuilder.set("shipFishingPostId", null)
				.set("subject", "TestSubject " + i)
				.set("fishIdList", List.of(
					givenFishList.get(i).getFishId(),
					givenFishList.get(i + 1).getFishId(),
					givenFishList.get(i + 2).getFishId(),
					givenFishList.get(i + 3).getFishId()))
				.set("price", 10000L * i)
				.set("reviewEverRate", 0.3D * i)
				.set("maxGuestCount", i)
				.set("likeCount", 0L)
				.sample();

			givenPostList.add(shipFishingPostRepository.save(post));

			for (int j = 1; j <= 3; j++) {
				Review givenReview = fixtureMonkeyBuilder.giveMeBuilder(Review.class)
					.set("reviewId", null)
					.set("shipFishingPostId", post.getShipFishingPostId())
					.set("content", "Test Content")
					.set("reservationId", post.getShipFishingPostId() * 10 + j)
					.sample();

				reviewRepository.save(givenReview);
			}
		}

		reservationDateRepository.save(fixtureMonkeyBuilder.giveMeBuilder(ReservationDate.class)
			.set("shipFishingPostId", givenPostList.get(14).getShipFishingPostId())
			.set("reservationDate", LocalDate.now())
			.set("isBan", true)
			.set("remainCount", 2)
			.sample());
	}

	@AfterEach
	public void tearDown() {
		shipFishingPostJpaRepository.deleteAll();
		em.flush();
		em.clear();
	}

	@Test
	@DisplayName("선상 낚시 게시글 저장 [Repository] - Success")
	void t01() {
		// Given
		ShipFishingPost givenShipFishingPost = arbitraryBuilder.set("shipFishingPostId", null).sample();

		// When
		ShipFishingPost savedShipFishingPost = shipFishingPostRepository.save(givenShipFishingPost);

		// Then
		assertThat(savedShipFishingPost).isNotNull();
		assertThat(savedShipFishingPost.getShipFishingPostId()).isNotNull();
	}

	@Test
	@DisplayName("선상 낚시 게시글 상세 조회 [ShipFishingPostResponse.DetailAll] [Repository] - Success")
	void t02() {
		// Given
		Member givenMember = fixtureMonkeyBuilder.giveMeBuilder(Member.class)
			.set("memberId", null)
			.set("email", "test@test.com")
			.set("name", "member")
			.set("nickname", "nickname")
			.set("phone", "telephone")
			.sample();

		Member savedMember = memberRepository.save(givenMember);

		Ship givenShip = fixtureMonkeyBuilder.giveMeBuilder(Ship.class)
			.set("shipId", null)
			.set("shipName", "나로호")
			.set("shipNumber", "12345-6789")
			.set("departurePort", "선착장")
			.sample();

		Ship savedShip = shipRepository.save(givenShip);

		ShipFishingPost givenShipFishingPost = arbitraryBuilder.set("shipFishingPostId", null)
			.set("shipId", savedShip.getShipId())
			.set("memberId", savedMember.getMemberId())
			.sample();

		ShipFishingPost savedShipFishingPost = shipFishingPostRepository.save(givenShipFishingPost);

		// When
		Optional<ShipFishingPostResponse.DetailAll> findOptionalDetailAll = shipFishingPostRepository.findDetailAllById(
			savedShipFishingPost.getShipFishingPostId());

		// Then
		assertThat(findOptionalDetailAll).isPresent();

		ShipFishingPostResponse.DetailAll findDetailAll = findOptionalDetailAll.get();

		// 선상 낚시 게시글 정보 검증
		ShipFishingPostResponse.Detail postDetail = findDetailAll.detailShipFishingPost();

		assertThat(postDetail.shipFishingPostId()).isEqualTo(savedShipFishingPost.getShipFishingPostId());
		assertThat(postDetail.subject()).isEqualTo(savedShipFishingPost.getSubject());
		assertThat(postDetail.content()).isEqualTo(savedShipFishingPost.getContent());
		assertThat(postDetail.fileIdList().toString()).isEqualTo(savedShipFishingPost.getFileIdList().toString());
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
		assertThat(shipDetail.restroomType()).isEqualTo(savedShip.getRestroomType());
		assertThat(shipDetail.loungeArea()).isEqualTo(savedShip.getLoungeArea());
		assertThat(shipDetail.kitchenFacility()).isEqualTo(savedShip.getKitchenFacility());
		assertThat(shipDetail.fishingChair()).isEqualTo(savedShip.getFishingChair());
		assertThat(shipDetail.passengerInsurance()).isEqualTo(savedShip.getPassengerInsurance());
		assertThat(shipDetail.fishingGearRental()).isEqualTo(savedShip.getFishingGearRental());
		assertThat(shipDetail.mealProvided()).isEqualTo(savedShip.getMealProvided());
		assertThat(shipDetail.parkingAvailable()).isEqualTo(savedShip.getParkingAvailable());

		// 게시글 작성자 정보 검증
		MemberResponse.ContactInfo memberDetail = findDetailAll.detailMember();
		assertThat(memberDetail.memberId()).isEqualTo(savedMember.getMemberId());
		assertThat(memberDetail.email()).isEqualTo(savedMember.getEmail());
		assertThat(memberDetail.name()).isEqualTo(savedMember.getName());
		assertThat(memberDetail.phone()).isEqualTo(savedMember.getPhone());
	}

	@Test
	@DisplayName("선상 낚시 게시글 스크롤 조회 [price] [desc] [검색 조건 - 오늘 예약 가능] [Repository] - Success")
	void t03() {
		// Given
		ShipFishingPostRequest.Search givenSearchDto = ShipFishingPostRequest.Search.builder()
			.searchDate(LocalDate.now())
			.build();

		GlobalRequest.CursorRequest givenCursorRequest1 = new GlobalRequest
			.CursorRequest("desc", "price", "next", null, null, 5);

		// When
		ScrollResponse<ShipFishingPostResponse.DetailScroll> responsePage1 = shipFishingPostRepository.findDetailScrollBySearch(
			givenSearchDto, givenCursorRequest1);

		log.debug("{} {} \n {} {} \n {} {} \n {} {} ",
			responsePage1.content().get(0).price(), responsePage1.content().get(0).shipFishingPostId(),
			responsePage1.content().get(1).price(), responsePage1.content().get(1).shipFishingPostId(),
			responsePage1.content().get(2).price(), responsePage1.content().get(2).shipFishingPostId(),
			responsePage1.content().get(3).price(), responsePage1.content().get(3).shipFishingPostId());

		// Then 1
		assertThat(responsePage1).isNotNull();

		List<ShipFishingPostResponse.DetailScroll> data1 = responsePage1.content();
		assertThat(data1).isNotNull();
		assertThat(data1.get(0).price()).isEqualTo(140000L);
		assertThat(data1.size()).isEqualTo(5);
		assertThat(responsePage1.isLast()).isFalse();

		GlobalRequest.CursorRequest givenCursorRequest2 = new GlobalRequest
			.CursorRequest("desc", "price", "next", data1.get(4).price().toString(), data1.get(4).shipFishingPostId(),
			5);

		// When
		ScrollResponse<ShipFishingPostResponse.DetailScroll> responsePage2 = shipFishingPostRepository.findDetailScrollBySearch(
			givenSearchDto, givenCursorRequest2);

		log.debug("{} {} \n {} {} \n {} {} \n {} {} ",
			responsePage2.content().get(0).price(), responsePage2.content().get(0).shipFishingPostId(),
			responsePage2.content().get(1).price(), responsePage2.content().get(1).shipFishingPostId(),
			responsePage2.content().get(2).price(), responsePage2.content().get(2).shipFishingPostId(),
			responsePage2.content().get(3).price(), responsePage2.content().get(3).shipFishingPostId());

		// Then 2
		assertThat(responsePage2).isNotNull();

		List<ShipFishingPostResponse.DetailScroll> data2 = responsePage2.content();
		assertThat(data2).isNotNull();
		assertThat(data2.get(0).price()).isEqualTo(90000L);
		assertThat(data2.size()).isEqualTo(5);
		assertThat(responsePage2.isLast()).isFalse();
	}

	@Test
	@DisplayName("선상 낚시 게시글 스크롤 조회 [price] [desc] [검색 조건 - 오늘 예약 가능 & max price ] [Repository] - Success")
	void t04() {
		// Given
		ShipFishingPostRequest.Search givenSearchDto = ShipFishingPostRequest.Search.builder()
			.searchDate(LocalDate.now())
			.maxPrice(130000L)
			.build();

		GlobalRequest.CursorRequest givenCursorRequest = new GlobalRequest
			.CursorRequest("desc", "price", "next", null, null, 5);

		// When
		ScrollResponse<ShipFishingPostResponse.DetailScroll> response = shipFishingPostRepository.findDetailScrollBySearch(
			givenSearchDto, givenCursorRequest);

		log.debug("{} {} \n {} {} \n {} {} \n {} {} ",
			response.content().get(0).price(), response.content().get(0).shipFishingPostId(),
			response.content().get(1).price(), response.content().get(1).shipFishingPostId(),
			response.content().get(2).price(), response.content().get(2).shipFishingPostId(),
			response.content().get(3).price(), response.content().get(3).shipFishingPostId());

		// Then
		assertThat(response).isNotNull();

		List<ShipFishingPostResponse.DetailScroll> data = response.content();
		assertThat(data).isNotNull();
		assertThat(data.size()).isEqualTo(5);
		assertThat(data.get(0).price()).isEqualTo(130000L);
		assertThat(response.isLast()).isFalse();
	}

	@Test
	@DisplayName("선상 낚시 게시글 스크롤 조회 [reviewEverRate] [asc] [검색 조건 - 오늘 예약 가능 & 최소 별점  ] [Repository] - Success")
	void t05() {
		// Given
		ShipFishingPostRequest.Search givenSearchDto = ShipFishingPostRequest.Search.builder()
			.searchDate(LocalDate.now())
			.minRating(3.0D)
			.build();

		GlobalRequest.CursorRequest givenCursorRequest = new GlobalRequest
			.CursorRequest("asc", "reviewEverRate", "next", null, null, 5);

		// When
		ScrollResponse<ShipFishingPostResponse.DetailScroll> response = shipFishingPostRepository.findDetailScrollBySearch(
			givenSearchDto, givenCursorRequest);

		log.debug("{} {} \n {} {} \n {} {} \n {} {} ",
			response.content().get(0).price(), response.content().get(0).shipFishingPostId(),
			response.content().get(1).price(), response.content().get(1).shipFishingPostId(),
			response.content().get(2).price(), response.content().get(2).shipFishingPostId(),
			response.content().get(3).price(), response.content().get(3).shipFishingPostId());

		// Then
		assertThat(response).isNotNull();

		List<ShipFishingPostResponse.DetailScroll> data = response.content();
		assertThat(data).isNotNull();
		assertThat(data.size()).isEqualTo(5);
		assertThat(data.get(0).reviewEverRate()).isEqualTo(3.0D);
		assertThat(response.isLast()).isTrue();
	}

	@Test
	@DisplayName("선상 낚시 게시글 스크롤 조회 [reviewEverRate] [asc] [검색 조건 - 예약 인원 ] [Repository] - Success")
	void t06() {
		// Given
		ShipFishingPostRequest.Search givenSearchDto = ShipFishingPostRequest.Search.builder()
			.searchDate(LocalDate.now())
			.guestCount(10L)
			.build();

		GlobalRequest.CursorRequest givenCursorRequest = new GlobalRequest
			.CursorRequest("asc", "reviewEverRate", "next", null, null, 5);

		// When
		ScrollResponse<ShipFishingPostResponse.DetailScroll> response = shipFishingPostRepository.findDetailScrollBySearch(
			givenSearchDto, givenCursorRequest);

		log.debug("{} {} \n {} {} \n {} {} \n {} {} ",
			response.content().get(0).price(), response.content().get(0).shipFishingPostId(),
			response.content().get(1).price(), response.content().get(1).shipFishingPostId(),
			response.content().get(2).price(), response.content().get(2).shipFishingPostId(),
			response.content().get(3).price(), response.content().get(3).shipFishingPostId());

		// Then
		assertThat(response).isNotNull();

		List<ShipFishingPostResponse.DetailScroll> data = response.content();
		assertThat(data).isNotNull();
		assertThat(data.size()).isEqualTo(5);
		assertThat(data.get(0).price()).isEqualTo(100000L);
		assertThat(response.isLast()).isTrue();
	}

	@Test
	@DisplayName("선상 낚시 게시글 스크롤 조회 [null - default createdAt] [desc] [검색 조건 - 검색어 ] [Repository] - Success")
	void t07() {
		// Given
		ShipFishingPostRequest.Search givenSearchDto = ShipFishingPostRequest.Search.builder()
			.searchDate(LocalDate.now())
			.keyword("2")
			.build();

		GlobalRequest.CursorRequest givenCursorRequest = new GlobalRequest
			.CursorRequest(null, null, "next", null, null, 5);

		// When
		ScrollResponse<ShipFishingPostResponse.DetailScroll> response = shipFishingPostRepository.findDetailScrollBySearch(
			givenSearchDto, givenCursorRequest);

		log.debug("{} {} \n {} {} ",
			response.content().get(0).price(), response.content().get(0).shipFishingPostId(),
			response.content().get(1).price(), response.content().get(1).shipFishingPostId());

		// Then
		assertThat(response).isNotNull();

		List<ShipFishingPostResponse.DetailScroll> data = response.content();
		assertThat(data).isNotNull();
		assertThat(data.size()).isEqualTo(2);
		assertThat(data.get(0).price()).isEqualTo(120000L);
		assertThat(response.isLast()).isTrue();
	}

	@Test
	@DisplayName("선상 낚시 게시글 삭제 [Repository] - Success")
	void t09() {
		// Given
		ShipFishingPost givenShipFishingPost = arbitraryBuilder.set("shipFishingPostId", null).sample();

		ShipFishingPost savedShipFishingPost = shipFishingPostRepository.save(givenShipFishingPost);

		// When
		shipFishingPostRepository.deleteById(savedShipFishingPost.getShipFishingPostId());

		// Then
		Optional<ShipFishingPost> findShipFishingPost = shipFishingPostRepository.findById(
			savedShipFishingPost.getShipFishingPostId());

		assertThat(findShipFishingPost.isPresent()).isFalse();
	}

	@Test
	@DisplayName("선상 낚시 게시글 마이페이지 조회 [Repository] - Success")
	void t10() {
		// Given
		Long givenMemberId = 1L;

		for (int i = 1; i <= 15; i++) {
			ShipFishingPost post = arbitraryBuilder.set("shipFishingPostId", null)
				.set("memberId", givenMemberId)
				.set("subject", "TestSubject " + i)
				.set("content", "TestContent " + i)
				.set("fishIdList", List.of())
				.set("fileIdList", List.of())
				.set("price", 10000L * i)
				.set("shipId", (long)i)
				.set("reviewEverRate", 0.3D * i)
				.set("maxGuestCount", i)
				.sample();

			shipFishingPostRepository.save(post);
		}

		List<ShipFishingPostResponse.MyPagePostList> findResponseDto = shipFishingPostRepository
			.findMyPagePostList(givenMemberId);

		assertThat(findResponseDto).isNotNull();
		assertThat(findResponseDto.size()).isEqualTo(15);
	}

	@Test
	@DisplayName("선상 낚시 게시글 평점 업데이트 [Repository] - Success")
	void t11() {
		Long givenMemberId = 1L;

		ShipFishingPost post = arbitraryBuilder.set("shipFishingPostId", null)
			.set("memberId", givenMemberId)
			.set("subject", "TestSubject")
			.set("content", "TestContent")
			.set("fishIdList", List.of())
			.set("fileIdList", List.of())
			.set("price", 10000L)
			.set("shipId", 100L)
			.set("reviewEverRate", 0.0D)
			.set("maxGuestCount", 10)
			.sample();

		ShipFishingPost savedShipFishingPost = shipFishingPostRepository.save(post);

		assertThat(savedShipFishingPost.getReviewEverRate()).isEqualTo(0.0D);

		Review givenReview = fixtureMonkeyBuilder.giveMeBuilder(Review.class)
			.set("reviewId", null)
			.set("shipFishingPostId", savedShipFishingPost.getShipFishingPostId())
			.set("rating", 5)
			.set("content", "Test Content")
			.sample();

		reviewRepository.save(givenReview);

		ZonedDateTime now = ZonedDateTime.now();
		ZonedDateTime lastRun = ZonedDateTime.now().minusDays(1);

		shipFishingPostRepository.updateReviewEverRate(now, lastRun);

		em.flush();
		em.clear();

		Optional<ShipFishingPost> findOptionalShipFishingPost = shipFishingPostRepository.findById(
			savedShipFishingPost.getShipFishingPostId());

		assertThat(findOptionalShipFishingPost.isPresent()).isTrue();

		ShipFishingPost findShipFishingPost = findOptionalShipFishingPost.get();

		assertThat(findShipFishingPost.getReviewEverRate()).isEqualTo(5.0D);
	}

}
