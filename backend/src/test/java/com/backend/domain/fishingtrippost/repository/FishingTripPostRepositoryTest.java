package com.backend.domain.fishingtrippost.repository;

import static org.assertj.core.api.Assertions.*;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

import com.backend.domain.fishingtrippost.converter.FishingTripPostConverter;
import com.backend.domain.fishingtrippost.domain.PostStatus;
import com.backend.domain.fishingtrippost.dto.response.FishingTripPostResponse;
import com.backend.domain.fishingtrippost.entity.FishingTripPost;
import com.backend.domain.fishingtriprecruitment.domain.FishingLevel;
import com.backend.domain.fishingtriprecruitment.domain.RecruitmentStatus;
import com.backend.domain.fishingtriprecruitment.entity.FishingTripRecruitment;
import com.backend.domain.fishingtriprecruitment.repository.FishingTripRecruitmentQueryRepository;
import com.backend.domain.fishingtriprecruitment.repository.FishingTripRecruitmentRepository;
import com.backend.domain.fishingtriprecruitment.repository.FishingTripRecruitmentRepositoryImpl;
import com.backend.domain.fishpoint.entity.FishPoint;
import com.backend.domain.fishpoint.repository.FishPointQueryRepository;
import com.backend.domain.fishpoint.repository.FishPointRepository;
import com.backend.domain.fishpoint.repository.FishPointRepositoryImpl;
import com.backend.domain.member.domain.MemberRole;
import com.backend.domain.member.domain.Provider;
import com.backend.domain.member.entity.Member;
import com.backend.domain.member.repository.MemberQueryRepository;
import com.backend.domain.member.repository.MemberRepository;
import com.backend.domain.member.repository.MemberRepositoryImpl;
import com.backend.global.config.JpaAuditingConfig;
import com.backend.global.config.QuerydslConfig;
import com.backend.global.dto.request.GlobalRequest;
import com.backend.global.storage.entity.File;
import com.backend.global.storage.repository.StorageQueryRepository;
import com.backend.global.storage.repository.StorageRepository;
import com.backend.global.storage.repository.StorageRepositoryImpl;
import com.backend.global.util.BaseTest;

import jakarta.persistence.EntityManager;

import com.navercorp.fixturemonkey.ArbitraryBuilder;

@DataJpaTest
@Import({
	JpaAuditingConfig.class,
	MemberRepositoryImpl.class,
	MemberQueryRepository.class,
	FishPointRepositoryImpl.class,
	FishPointQueryRepository.class,
	FishingTripPostRepositoryImpl.class,
	FishingTripPostQueryRepository.class,
	StorageRepositoryImpl.class,
	StorageQueryRepository.class,
	FishingTripRecruitmentRepositoryImpl.class,
	FishingTripRecruitmentQueryRepository.class,
	QuerydslConfig.class,
})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FishingTripPostRepositoryTest extends BaseTest {

	@Autowired
	private FishingTripPostRepository fishingTripPostRepository;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private FishPointRepository fishPointRepository;

	@Autowired
	private StorageRepository storageRepository;

	@Autowired
	private FishingTripRecruitmentRepository fishingTripRecruitmentRepository;

	@Autowired
	private FishingTripPostJpaRepository fishingTripPostJpaRepository;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private EntityManager em;

	final ArbitraryBuilder<FishingTripPost> fishingTripPostArbitraryBuilder = fixtureMonkeyBuilder
		.giveMeBuilder(FishingTripPost.class)
		.set("fishingTripPostId", null)
		.set("subject", "테스트 제목")
		.set("content", "테스트 내용")
		.set("recruitmentCount", 5)
		.set("currentCount", 0)
		.set("isShipFish", false)
		.set("fishingDate", ZonedDateTime.of(2025, 6, 10, 8, 0, 0, 0, ZoneId.of("Asia/Seoul")))
		.set("fileIdList", List.of(1L, 2L, 3L));

	final ArbitraryBuilder<Member> memberArbitraryBuilder = fixtureMonkeyBuilder
		.giveMeBuilder(Member.class)
		.set("memberId", null)
		.set("nickname", "강태공")
		.set("name", "테스트")
		.set("email", UUID.randomUUID() + "@example.com")
		.set("phone", "010-" + UUID.randomUUID().toString().substring(0, 8))
		.set("role", MemberRole.USER)
		.set("provider", Provider.KAKAO)
		.set("providerId", UUID.randomUUID().toString())
		.set("isAddInfo", false);

	@Test
	@DisplayName("동출 게시글 저장 [Repository] - Success")
	void t01() {
		// given
		Member savedMember = memberRepository.save(memberArbitraryBuilder.sample());
		FishPoint savedFishPoint = fishPointRepository.save(createRandomFishPoint());

		FishingTripPost givenPost = fishingTripPostArbitraryBuilder
			.set("fishingTripPostId", null)
			.set("memberId", savedMember.getMemberId())
			.set("fishPointId", savedFishPoint.getFishPointId())
			.sample();

		// when
		FishingTripPost saved = fishingTripPostRepository.save(givenPost);

		// then
		assertThat(saved).isNotNull();
		assertThat(saved.getFishingTripPostId()).isNotNull();
	}

	@Test
	@DisplayName("동출 게시글 Id로 조회 [Repository] - Success")
	void t02() {
		// given
		Member savedMember = memberRepository.save(memberArbitraryBuilder.sample());
		FishPoint savedFishPoint = fishPointRepository.save(createRandomFishPoint());

		FishingTripPost givenPost = fishingTripPostArbitraryBuilder
			.set("fishingTripPostId", null)
			.set("memberId", savedMember.getMemberId())
			.set("fishPointId", savedFishPoint.getFishPointId())
			.sample();

		FishingTripPost saved = fishingTripPostRepository.save(givenPost);

		// when
		FishingTripPost result = fishingTripPostRepository.findById(saved.getFishingTripPostId()).orElse(null);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getFishingTripPostId()).isEqualTo(saved.getFishingTripPostId());
		assertThat(result.getSubject()).isEqualTo("테스트 제목");
		assertThat(result.getContent()).isEqualTo("테스트 내용");
	}

	@Test
	@DisplayName("동출 게시글 상세 조회 [Repository] - Success")
	void t03() {
		// given
		Member savedMember = memberRepository.save(memberArbitraryBuilder.sample());
		FishPoint savedFishPoint = fishPointRepository.save(createRandomFishPoint());

		List<File> savedFiles = Stream.of(1, 2, 3)
			.map(i -> File.builder()
				.fileName("file_" + i + ".jpg")
				.originalFileName("original_" + i + ".jpg")
				.contentType("image/jpeg")
				.fileSize(12345L)
				.url("http://test.com/file_" + i + ".jpg")
				.domain("test")
				.createdById(savedMember.getMemberId())
				.uploaded(true)
				.build())
			.map(storageRepository::save)
			.toList();

		List<Long> fileIds = savedFiles.stream()
			.map(File::getFileId)
			.toList();

		Map<Long, String> expectedFileUrlMap = savedFiles.stream()
			.collect(Collectors.toMap(File::getFileId, File::getUrl));

		FishingTripPost givenPost = fishingTripPostArbitraryBuilder
			.set("fishingTripPostId", null)
			.set("memberId", savedMember.getMemberId())
			.set("fishingPointId", savedFishPoint.getFishPointId())
			.set("fileIdList", fileIds)
			.sample();

		FishingTripPost savedPost = fishingTripPostRepository.save(givenPost);

		// when
		Optional<FishingTripPostResponse.DetailQueryDto> optionalDto =
			fishingTripPostRepository.findDetailQueryDtoById(savedPost.getFishingTripPostId());

		assertThat(optionalDto).isPresent();
		FishingTripPostResponse.DetailQueryDto detailDto = optionalDto.get();

		Map<Long, String> fileUrlMap = storageRepository.findAllById(detailDto.fileIdList()).stream()
			.collect(Collectors.toMap(File::getFileId, File::getUrl));

		FishingTripPostResponse.Detail detail = FishingTripPostConverter.toDetail(
			detailDto, 0L,false, false, fileUrlMap);

		// then
		assertThat(detail.fishingTripPostId()).isEqualTo(savedPost.getFishingTripPostId());
		assertThat(detail.name()).isEqualTo(savedMember.getName());
		assertThat(detail.subject()).isEqualTo(savedPost.getSubject());
		assertThat(detail.content()).isEqualTo(savedPost.getContent());
		assertThat(detail.currentCount()).isEqualTo(savedPost.getCurrentCount());
		assertThat(detail.recruitmentCount()).isEqualTo(savedPost.getRecruitmentCount());
		assertThat(detail.fishingDate()).isEqualTo(savedPost.getFishingDate());
		assertThat(detail.fishPointName()).isEqualTo(savedFishPoint.getFishPointName());
		assertThat(detail.fishPointDetailName()).isEqualTo(savedFishPoint.getFishPointDetailName());
		assertThat(detail.longitude()).isEqualTo(savedFishPoint.getLongitude());
		assertThat(detail.latitude()).isEqualTo(savedFishPoint.getLatitude());
		assertThat(detail.fileUrlList()).containsExactlyInAnyOrderEntriesOf(expectedFileUrlMap);
	}

	@Test
	@DisplayName("동출 게시글 스크롤 조회 [createdAt] [desc] [커서 조건 적용] - Success")
	void t04() {
		// given
		Member savedMember = memberRepository.save(memberArbitraryBuilder.sample());
		FishPoint savedFishPoint = fishPointRepository.save(createRandomFishPoint());

		List<FishingTripPost> givenPosts = fishingTripPostArbitraryBuilder
			.set("memberId", savedMember.getMemberId())
			.set("fishingPointId", savedFishPoint.getFishPointId())
			.set("fishingTripPostId", null)
			.sampleList(3);

		fishingTripPostJpaRepository.saveAll(givenPosts);

		ZonedDateTime baseTime = ZonedDateTime.of(2025, 4, 10, 12, 0, 0, 0, ZoneId.of("Asia/Seoul"));
		List<ZonedDateTime> createdTimes = List.of(
			baseTime.minusHours(2),
			baseTime.minusHours(1),
			baseTime
		);

		for (int i = 0; i < givenPosts.size(); i++) {
			jdbcTemplate.update(
				"UPDATE fishing_trip_posts SET created_at = ? WHERE fishing_trip_post_id = ?",
				Timestamp.valueOf(createdTimes.get(i).toLocalDateTime()),
				givenPosts.get(i).getFishingTripPostId()
			);
		}

		em.flush();
		em.clear();

		List<FishingTripPost> refreshedPosts = fishingTripPostJpaRepository.findAll();
		refreshedPosts.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
		FishingTripPost cursorBase = refreshedPosts.get(0);

		GlobalRequest.CursorRequest cursorRequest = new GlobalRequest.CursorRequest(
			"desc", "createdAt", "next",
			cursorBase.getCreatedAt().toString(),
			cursorBase.getFishingTripPostId(),
			10
		);

		// when
		List<FishingTripPostResponse.DetailPageQueryDto> result =
			fishingTripPostRepository.findScrollDetailPageDto(cursorRequest, null, null, null);

		// then
		assertThat(result).isNotEmpty();
		assertThat(result)
			.extracting(FishingTripPostResponse.DetailPageQueryDto::fishingTripPostId)
			.doesNotContain(cursorBase.getFishingTripPostId());
	}

	@Test
	@DisplayName("동출 게시글 참여 상세 DTO 조회 [Repository] - Success")
	void t05() {
		//given
		Member writer = memberRepository.save(
			memberArbitraryBuilder.sample()
		);

		File writerFile = storageRepository.save(
			File.builder()
				.fileName("profile.jpg")
				.originalFileName("profile.jpg")
				.contentType("image/jpeg")
				.fileSize(10000L)
				.url("https://cdn.example.com/profile.jpg")
				.domain("profile")
				.createdById(writer.getMemberId())
				.uploaded(true)
				.build()
		);

		writer = memberRepository.save(
			Member.builder()
				.memberId(writer.getMemberId())
				.nickname(writer.getNickname())
				.name(writer.getName())
				.email(writer.getEmail())
				.phone(writer.getPhone())
				.role(writer.getRole())
				.provider(writer.getProvider())
				.providerId(writer.getProviderId())
				.description(writer.getDescription())
				.isAddInfo(writer.getIsAddInfo())
				.fileId(writerFile.getFileId())
				.build()
		);

		FishPoint fishPoint = fishPointRepository.save(createRandomFishPoint());

		FishingTripPost post = fishingTripPostRepository.save(
			fishingTripPostArbitraryBuilder
				.set("memberId", writer.getMemberId())
				.set("fishPointId", fishPoint.getFishPointId())
				.sample()
		);

		//when
		FishingTripPostResponse.ParticipantDetailDto dto =
			fishingTripPostRepository.findParticipantDetailDto(post.getFishingTripPostId(), writer.getMemberId());

		// then
		assertThat(dto).isNotNull();
		assertThat(dto.fishingTripPostId()).isEqualTo(post.getFishingTripPostId());
		assertThat(dto.postOwnerId()).isEqualTo(writer.getMemberId());
		assertThat(dto.ownerNickname()).isEqualTo(writer.getNickname());
		assertThat(dto.ownerProfileImageUrl()).isEqualTo(writerFile.getUrl());
		assertThat(dto.isCurrentUserOwner()).isTrue();
		assertThat(dto.isApplicant()).isFalse();
	}

	@Test
	@DisplayName("동출 게시글 승인된 참여자 목록 조회 [Repository] - Success")
	void t06() {
		// given
		Member writer = memberRepository.save(
			memberArbitraryBuilder
				.set("phone", "010-" + UUID.randomUUID().toString().substring(0, 8))
				.set("email", UUID.randomUUID().toString().substring(0, 8) + "@example.com")
				.set("providerId", UUID.randomUUID().toString())
				.sample()
		);

		FishPoint fishPoint = fishPointRepository.save(createRandomFishPoint());

		FishingTripPost post = fishingTripPostRepository.save(
			fishingTripPostArbitraryBuilder
				.set("memberId", writer.getMemberId())
				.set("fishPointId", fishPoint.getFishPointId())
				.sample()
		);

		List<Member> participants = List.of(
			memberRepository.save(memberArbitraryBuilder
				.set("nickname", "참가자1")
				.set("phone", "010-" + UUID.randomUUID().toString().substring(0, 8))
				.set("email", UUID.randomUUID().toString().substring(0, 8) + "@example.com")
				.set("providerId", UUID.randomUUID().toString())
				.sample()),
			memberRepository.save(memberArbitraryBuilder
				.set("nickname", "참가자2")
				.set("phone", "010-" + UUID.randomUUID().toString().substring(0, 8))
				.set("email", UUID.randomUUID().toString().substring(0, 8) + "@example.com")
				.set("providerId", UUID.randomUUID().toString())
				.sample())
		);

		for (Member participant : participants) {
			File file = storageRepository.save(
				File.builder()
					.fileName("profile.jpg")
					.originalFileName("original.jpg")
					.contentType("image/jpeg")
					.fileSize(10000L)
					.url("https://cdn.example.com/" + participant.getNickname() + ".jpg")
					.domain("profile")
					.createdById(participant.getMemberId())
					.uploaded(true)
					.build()
			);

			participant = memberRepository.save(
				Member.builder()
					.memberId(participant.getMemberId())
					.nickname(participant.getNickname())
					.name(participant.getName())
					.email(participant.getEmail())
					.phone(participant.getPhone())
					.role(participant.getRole())
					.provider(participant.getProvider())
					.providerId(participant.getProviderId())
					.isAddInfo(participant.getIsAddInfo())
					.fileId(file.getFileId())
					.build()
			);

			fishingTripRecruitmentRepository.save(
				FishingTripRecruitment.builder()
					.fishingTripPostId(post.getFishingTripPostId())
					.memberId(participant.getMemberId())
					.introduction("테스트 소개입니다.")
					.fishingLevel(FishingLevel.BEGINNER)
					.recruitmentStatus(RecruitmentStatus.APPROVED)
					.build()
			);
		}

		// when
		List<FishingTripPostResponse.ParticipantDetail> result =
			fishingTripPostRepository.findApprovedParticipants(post.getFishingTripPostId());

		// then
		assertThat(result).hasSize(2);
		assertThat(result).extracting("nickname")
			.containsExactlyInAnyOrder("참가자1", "참가자2");
		assertThat(result).allSatisfy(detail ->
			assertThat(detail.profileImageUrl()).startsWith("https://cdn.example.com/")
		);
	}

	@Test
	@DisplayName("동출 게시글 삭제 [Repository] - Success")
	void t07() {
		// given
		Member savedMember = memberRepository.save(memberArbitraryBuilder.sample());
		FishPoint savedFishPoint = fishPointRepository.save(createRandomFishPoint());

		FishingTripPost givenPost = fishingTripPostArbitraryBuilder
			.set("fishingTripPostId", null)
			.set("memberId", savedMember.getMemberId())
			.set("fishPointId", savedFishPoint.getFishPointId())
			.sample();

		FishingTripPost savedPost = fishingTripPostRepository.save(givenPost);

		// when
		fishingTripPostRepository.delete(savedPost);
		Optional<FishingTripPost> deleted = fishingTripPostRepository.findById(savedPost.getFishingTripPostId());

		// then
		assertThat(deleted).isEmpty();
	}

	// @Test
	// @DisplayName("내가 신청한 동출 게시글 목록 커서 기반 조회 [Repository] - Success")
	// void t08() {
	// 	// given
	// 	Member writer = memberRepository.save(memberArbitraryBuilder
	// 		.set("email", UUID.randomUUID() + "@example.com")
	// 		.set("phone", "010-" + UUID.randomUUID().toString().substring(0, 8).replaceAll("[^0-9]", "1"))
	// 		.set("providerId", UUID.randomUUID().toString())
	// 		.sample());
	//
	// 	Member applicant = memberRepository.save(memberArbitraryBuilder
	// 		.set("nickname", "지원자")
	// 		.set("email", UUID.randomUUID() + "@example.com")
	// 		.set("phone", "010-" + UUID.randomUUID().toString().substring(0, 8).replaceAll("[^0-9]", "2"))
	// 		.set("providerId", UUID.randomUUID().toString())
	// 		.sample());
	//
	// 	FishPoint fishPoint = fishPointRepository.save(createRandomFishPoint());
	//
	// 	List<FishingTripPost> posts = fishingTripPostArbitraryBuilder
	// 		.set("memberId", writer.getMemberId())
	// 		.set("fishingPointId", fishPoint.getFishPointId())
	// 		.sampleList(3);
	//
	// 	fishingTripPostJpaRepository.saveAll(posts);
	//
	// 	for (FishingTripPost post : posts) {
	// 		fishingTripRecruitmentRepository.save(FishingTripRecruitment.builder()
	// 			.fishingTripPostId(post.getFishingTripPostId())
	// 			.memberId(applicant.getMemberId())
	// 			.introduction("안녕하세요")
	// 			.fishingLevel(FishingLevel.INTERMEDIATE)
	// 			.recruitmentStatus(RecruitmentStatus.PENDING)
	// 			.build());
	// 	}
	//
	// 	posts.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
	// 	FishingTripPost cursorBase = posts.get(0);
	//
	// 	// 커서 기준 확인
	// 	System.out.println("Cursor Base - CreatedAt: " + cursorBase.getCreatedAt() + ", FishingTripPostId: " + cursorBase.getFishingTripPostId());
	//
	// 	GlobalRequest.CursorRequest cursorRequest = new GlobalRequest.CursorRequest(
	// 		"desc", "createdAt", "next",
	// 		cursorBase.getCreatedAt().toString(),
	// 		cursorBase.getFishingTripPostId(),
	// 		10
	// 	);
	//
	// 	// when
	// 	ScrollResponse<FishingTripPostResponse.MyFishingTripPostDetailPage> response =
	// 		fishingTripPostRepository.findMyFishingTripRecruitmentDetailPage(cursorRequest, PostStatus.RECRUITING,
	// 			applicant.getMemberId());
	//
	// 	// then
	// 	assertThat(response).isNotNull();
	// 	assertThat(response.content()).isNotEmpty();
	// 	assertThat(response.content()).extracting("fishingTripPostId")
	// 		.doesNotContain(cursorBase.getFishingTripPostId());  // 커서값이 제외되는지 확인
	// 	assertThat(response.content()).allSatisfy(item ->
	// 		assertThat(item.postStatus()).isEqualTo(PostStatus.RECRUITING)
	// 	);
	// }

	// // Debugging added to check cursor value handling
	// @Test
	// @DisplayName("내가 작성한 동출 게시글 목록 커서 기반 조회 [Repository] - Success")
	// void t09() {
	// 	// given
	// 	Member author = memberRepository.save(memberArbitraryBuilder
	// 		.set("email", UUID.randomUUID() + "@example.com")
	// 		.set("phone", "010-" + UUID.randomUUID().toString().substring(0, 8).replaceAll("[^0-9]", "3"))
	// 		.set("providerId", UUID.randomUUID().toString())
	// 		.sample());
	//
	// 	FishPoint fishPoint = fishPointRepository.save(createRandomFishPoint());
	//
	// 	List<FishingTripPost> posts = fishingTripPostArbitraryBuilder
	// 		.set("memberId", author.getMemberId())
	// 		.set("fishingPointId", fishPoint.getFishPointId())
	// 		.sampleList(3);
	//
	// 	fishingTripPostJpaRepository.saveAll(posts);
	//
	// 	posts.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt())); // 최신순으로 정렬
	// 	FishingTripPost cursorBase = posts.get(0);
	//
	// 	// Log the cursor base for debugging
	// 	System.out.println("Cursor Base - CreatedAt: " + cursorBase.getCreatedAt() + ", FishingTripPostId: " + cursorBase.getFishingTripPostId());
	//
	// 	GlobalRequest.CursorRequest cursorRequest = new GlobalRequest.CursorRequest(
	// 		"desc", "createdAt", "next",
	// 		cursorBase.getCreatedAt().toString(),
	// 		cursorBase.getFishingTripPostId(),
	// 		10
	// 	);
	//
	// 	// when
	// 	ScrollResponse<FishingTripPostResponse.MyFishingTripPostDetailPage> response =
	// 		fishingTripPostRepository.findMyPostFishingTripPostDetailPage(cursorRequest, PostStatus.RECRUITING, author.getMemberId());
	//
	// 	// then
	// 	assertThat(response).isNotNull();
	// 	assertThat(response.content()).isNotEmpty();
	// 	assertThat(response.content()).extracting("fishingTripPostId")
	// 		.doesNotContain(cursorBase.getFishingTripPostId());  // 커서값이 제외되는지 확인
	// 	assertThat(response.content()).allSatisfy(item ->
	// 		assertThat(item.postStatus()).isEqualTo(PostStatus.RECRUITING)
	// 	);
	// }

	@Test
	@DisplayName("HOT 게시글 조회 [Repository] - Success")
	void t10() {
		ZonedDateTime now = ZonedDateTime.of(2025, 4, 10, 10, 0, 0, 0, ZoneId.of("Asia/Seoul"));
		ZonedDateTime baseTime = now.minusDays(5).withHour(0).withMinute(0).withSecond(0).withNano(0);

		// given
		Member writer = memberRepository.save(memberArbitraryBuilder.sample());
		FishPoint fishPoint = fishPointRepository.save(createRandomFishPoint());

		for (int i = 0; i < 6; i++) {
			FishingTripPost post = fishingTripPostArbitraryBuilder
				.set("memberId", writer.getMemberId())
				.set("fishingPointId", fishPoint.getFishPointId())
				.set("likeCount", (long)(5 - i))
				.set("commentCount", (long)i)
				.set("postStatus", PostStatus.RECRUITING)
				.sample();

			FishingTripPost saved = fishingTripPostJpaRepository.save(post);

			jdbcTemplate.update(
				"UPDATE fishing_trip_posts SET created_at = ? WHERE fishing_trip_post_id = ?",
				Timestamp.valueOf(baseTime.plusHours(i + 1).toLocalDateTime()), // baseTime보다 +1시간부터 시작
				saved.getFishingTripPostId()
			);
		}

		em.flush();
		em.clear();

		FishingTripPost oldPost = fishingTripPostArbitraryBuilder
			.set("memberId", writer.getMemberId())
			.set("fishingPointId", fishPoint.getFishPointId())
			.set("subject", "오래된 게시글")
			.set("likeCount", 100L)
			.set("commentCount", 100L)
			.set("postStatus", PostStatus.RECRUITING)
			.sample();

		FishingTripPost savedOld = fishingTripPostJpaRepository.save(oldPost);
		jdbcTemplate.update(
			"UPDATE fishing_trip_posts SET created_at = ? WHERE fishing_trip_post_id = ?",
			Timestamp.valueOf(baseTime.minusDays(1).toLocalDateTime()), // 아예 하루 전으로
			savedOld.getFishingTripPostId()
		);

		em.flush();
		em.clear();

		// when
		List<FishingTripPostResponse.HotPostDto> result = fishingTripPostRepository.findHotPostDto(baseTime);

		// then
		assertThat(result).hasSize(5); // 상위 5개만
		assertThat(result).extracting("subject").doesNotContain("오래된 게시글");
		assertThat(result).extracting(FishingTripPostResponse.HotPostDto::fishingTripPostId)
			.doesNotContain(savedOld.getFishingTripPostId());

		List<Long> scores = result.stream().map(FishingTripPostResponse.HotPostDto::hotScore).toList();
		List<Long> sorted = scores.stream().sorted(Comparator.reverseOrder()).toList();
		assertThat(scores).isEqualTo(sorted);
	}
}