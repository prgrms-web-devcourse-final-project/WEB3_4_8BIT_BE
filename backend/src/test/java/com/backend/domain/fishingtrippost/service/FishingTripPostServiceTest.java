package com.backend.domain.fishingtrippost.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.backend.domain.activityhistory.service.ActivityHistoryService;
import com.backend.domain.chat.room.entity.TargetType;
import com.backend.domain.chat.room.service.RoomService;
import com.backend.domain.fishingtrippost.domain.PostStatus;
import com.backend.domain.fishingtrippost.dto.request.FishingTripPostRequest;
import com.backend.domain.fishingtrippost.dto.response.FishingTripPostResponse;
import com.backend.domain.fishingtrippost.entity.FishingTripPost;
import com.backend.domain.fishingtrippost.exception.FishingTripPostErrorCode;
import com.backend.domain.fishingtrippost.exception.FishingTripPostException;
import com.backend.domain.fishingtrippost.notifier.FishingTripPostNotifier;
import com.backend.domain.fishingtrippost.repository.FishingTripPostRepository;
import com.backend.domain.fishpoint.exception.FishPointErrorCode;
import com.backend.domain.fishpoint.exception.FishPointException;
import com.backend.domain.fishpoint.repository.FishPointRepository;
import com.backend.domain.like.domain.LikeTargetType;
import com.backend.domain.like.repository.LikeRepository;
import com.backend.domain.member.entity.Member;
import com.backend.domain.member.exception.MemberErrorCode;
import com.backend.domain.member.exception.MemberException;
import com.backend.domain.member.repository.MemberRepository;
import com.backend.global.dto.request.GlobalRequest;
import com.backend.global.dto.response.ScrollResponse;
import com.backend.global.storage.entity.File;
import com.backend.global.storage.repository.StorageRepository;
import com.backend.global.storage.service.StorageService;
import com.backend.global.util.BaseTest;

import com.navercorp.fixturemonkey.ArbitraryBuilder;

@ExtendWith(MockitoExtension.class)
class FishingTripPostServiceTest extends BaseTest {

	@InjectMocks
	private FishingTripPostServiceImpl fishingTripPostService;

	@Mock
	private ActivityHistoryService activityHistoryService;

	@Mock
	private StorageService storageService;

	@Mock
	private FishPointRepository fishPointRepository;

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private FishingTripPostRepository fishingTripPostRepository;

	@Mock
	private StorageRepository storageRepository;

	@Mock
	private FishingTripPostNotifier fishingTripPostNotifier;

	@Mock
	private LikeRepository likeRepository;

	@Mock
	private RoomService roomService;

	private final ArbitraryBuilder<FishingTripPostRequest.create> createRequestBuilder =
		fixtureMonkeyValidation.giveMeBuilder(FishingTripPostRequest.create.class);

	private final ArbitraryBuilder<FishingTripPostRequest.update> updateRequestBuilder =
		fixtureMonkeyValidation.giveMeBuilder(FishingTripPostRequest.update.class);

	private final ArbitraryBuilder<FishingTripPost> postBuilder =
		fixtureMonkeyBuilder.giveMeBuilder(FishingTripPost.class);

	@Test
	@DisplayName("동출 게시글 등록 [Service] - Success")
	void t01() {
		// Given

		Member givenMember = fixtureMonkeyBuilder.giveMeBuilder(Member.class)
			.set("memberId", 1L)
			.sample();

		FishingTripPostRequest.create givenRequestDto = fixtureMonkeyValidation.giveMeBuilder(
				FishingTripPostRequest.create.class)
			.set("subject", "같이 낚시 가실 분~")
			.set("content", "초보 환영합니다!")
			.set("recruitmentCount", 5)
			.set("isShipFish", false)
			.set("fishingDate", ZonedDateTime.now().plusDays(1))
			.set("fishingPointId", 1L)
			.set("fileIdList", List.of(1L, 2L, 3L))
			.sample();

		FishingTripPost savedPost = fixtureMonkeyBuilder.giveMeBuilder(FishingTripPost.class)
			.set("fishingTripPostId", 1L)
			.sample();

		when(memberRepository.existsById(givenMember.getMemberId())).thenReturn(true);
		when(fishPointRepository.existsById(givenRequestDto.fishingPointId())).thenReturn(true);
		when(fishingTripPostRepository.save(any(FishingTripPost.class))).thenReturn(savedPost);
		doNothing().when(activityHistoryService).createActivityHistory(any(FishingTripPost.class));

		// When
		Long savedId = fishingTripPostService.createFishingTripPost(givenMember.getMemberId(), givenRequestDto);

		// Then
		assertThat(savedId).isEqualTo(savedPost.getFishingTripPostId());

		verify(memberRepository).existsById(givenMember.getMemberId());
		verify(fishPointRepository).existsById(givenRequestDto.fishingPointId());
		verify(fishingTripPostRepository).save(any(FishingTripPost.class));
		verify(roomService).createRoom(savedId, TargetType.FISHING_TRIP_POST);
	}

	@Test
	@DisplayName("동출 게시글 등록 [MEMBER_NOT_FOUND] [Service] - Fail")
	void t02() {
		// Given
		Long memberId = 999L;

		FishingTripPostRequest.create requestDto = createRequestBuilder
			.set("fishingTripPointId", 1L)
			.sample();

		when(memberRepository.existsById(memberId)).thenReturn(false);

		// When & Then
		assertThatThrownBy(() -> fishingTripPostService.createFishingTripPost(memberId, requestDto))
			.isInstanceOf(MemberException.class)
			.hasMessage(MemberErrorCode.MEMBER_NOT_FOUND.getMessage());

		verify(memberRepository).existsById(memberId);
		verify(fishPointRepository, never()).existsById(any());
		verify(fishingTripPostRepository, never()).save(any());
	}

	@Test
	@DisplayName("동출 게시글 등록 [FISH_POINT_NOT_FOUND] [Service] - Fail")
	void t03() {
		// Given
		Long memberId = 1L;

		FishingTripPostRequest.create requestDto = createRequestBuilder
			.set("subject", "같이 낚시 가실 분~")
			.set("content", "초보 환영합니다!")
			.set("recruitmentCount", 5)
			.set("isShipFish", false)
			.set("fishingDate", ZonedDateTime.now().plusDays(1))
			.set("fishingPointId", 999L)
			.set("fileIdList", List.of(1L, 2L, 3L))
			.sample();

		when(memberRepository.existsById(memberId)).thenReturn(true);
		when(fishPointRepository.existsById(requestDto.fishingPointId())).thenReturn(false);

		// When & Then
		assertThatThrownBy(() -> fishingTripPostService.createFishingTripPost(memberId, requestDto))
			.isInstanceOf(FishPointException.class)
			.hasMessage(FishPointErrorCode.FISH_POINT_NOT_FOUND.getMessage());

		verify(memberRepository).existsById(memberId);
		verify(fishPointRepository).existsById(requestDto.fishingPointId());
		verify(fishingTripPostRepository, never()).save(any());
	}

	@Test
	@DisplayName("동출 게시글 수정 [Service] - Success")
	void t04() {
		// Given
		Long memberId = 1L;
		Long postId = 100L;

		List<Long> originalFileIds = List.of(10L, 30L); // 기존 이미지
		List<Long> updatedFileIds = List.of(10L, 20L);  // 요청 이미지 (30 제거됨)

		FishingTripPostRequest.update requestDto = FishingTripPostRequest.update.builder()
			.subject("수정된 제목")
			.content("수정된 내용")
			.recruitmentCount(2)
			.isShipFish(false)
			.fishingDate(ZonedDateTime.now().plusDays(5))
			.fileIdList(updatedFileIds)
			.build();

		FishingTripPost mockPost = mock(FishingTripPost.class);
		when(mockPost.getMemberId()).thenReturn(memberId);
		when(mockPost.getFileIdList()).thenReturn(originalFileIds);
		when(mockPost.getFishingTripPostId()).thenReturn(postId);

		when(fishingTripPostRepository.findById(postId)).thenReturn(Optional.of(mockPost));
		doNothing().when(storageService).deleteFilesByIdList(eq(memberId), eq(List.of(30L)));

		// When
		Long result = fishingTripPostService.updateFishingTripPost(memberId, postId, requestDto);

		// Then
		assertThat(result).isEqualTo(postId);
		verify(fishingTripPostRepository).findById(postId);
		verify(storageService).deleteFilesByIdList(memberId, List.of(30L));
		verify(mockPost).updateFishingTripPost(
			eq("수정된 제목"),
			eq("수정된 내용"),
			eq(2),
			eq(false),
			any(ZonedDateTime.class),
			eq(updatedFileIds)
		);
	}


	@Test
	@DisplayName("동출 게시글 수정 [FISHING_TRIP_POST_NOT_FOUND] [Service] - Fail")
	void t05() {
		// Given
		Long memberId = 1L;
		Long postId = 999L;

		FishingTripPostRequest.update requestDto = updateRequestBuilder.sample();

		when(fishingTripPostRepository.findById(postId)).thenReturn(java.util.Optional.empty());

		// When & Then
		assertThatThrownBy(() -> fishingTripPostService.updateFishingTripPost(memberId, postId, requestDto))
			.isInstanceOf(FishingTripPostException.class)
			.hasMessage(FishingTripPostErrorCode.FISHING_TRIP_POST_NOT_FOUND.getMessage());

		verify(fishingTripPostRepository).findById(postId);
	}

	@Test
	@DisplayName("동출 게시글 수정 [UNAUTHORIZED_AUTHOR] [Service] - Fail")
	void t06() {
		// Given
		Long memberId = 1L;
		Long postId = 100L;

		FishingTripPostRequest.update requestDto = updateRequestBuilder.sample();

		FishingTripPost existingPost = postBuilder
			.set("fishingTripPostId", postId)
			.set("memberId", 2L) // 다른 작성자
			.sample();

		when(fishingTripPostRepository.findById(postId)).thenReturn(java.util.Optional.of(existingPost));

		// When & Then
		assertThatThrownBy(() -> fishingTripPostService.updateFishingTripPost(memberId, postId, requestDto))
			.isInstanceOf(FishingTripPostException.class)
			.hasMessage(FishingTripPostErrorCode.FISHING_TRIP_POST_UNAUTHORIZED_AUTHOR.getMessage());

		verify(fishingTripPostRepository).findById(postId);
	}

	@Test
	@DisplayName("동출 게시글 상세 조회 [Service] - Success")
	void t07() {
		// Given
		Long postId = 1L;
		Long memberId = 1L; // 로그인된 사용자 ID
		List<Long> fileIds = List.of(101L, 102L, 103L);
		List<String> fileUrls = List.of(
			"https://cdn.example.com/1.jpg",
			"https://cdn.example.com/2.jpg",
			"https://cdn.example.com/3.jpg"
		);

		FishingTripPostResponse.DetailQueryDto queryDto = new FishingTripPostResponse.DetailQueryDto(
			postId,
			"루피",
			"같이 갑시다",
			"초보 환영",
			1,
			5,
			ZonedDateTime.parse("2025-04-01T12:00:00+09:00"),
			ZonedDateTime.parse("2025-04-10T06:00:00+09:00"),
			"남해 앞바다",
			"남해",
			128.12345,
			37.12345,
			fileIds,
			PostStatus.RECRUITING,
			3L
		);

		List<File> mockFiles = List.of(
			File.builder().fileId(101L).url(fileUrls.get(0)).uploaded(true).build(),
			File.builder().fileId(102L).url(fileUrls.get(1)).uploaded(true).build(),
			File.builder().fileId(103L).url(fileUrls.get(2)).uploaded(true).build()
		);

		when(fishingTripPostRepository.findDetailQueryDtoById(postId)).thenReturn(Optional.of(queryDto));
		when(storageRepository.findAllById(fileIds)).thenReturn(mockFiles);
		when(likeRepository.existsByMemberIdAndTargetTypeAndTargetId(memberId, LikeTargetType.FISHING_TRIP_POST,
			postId)).thenReturn(true);

		// When
		FishingTripPostResponse.Detail actual = fishingTripPostService.getFishingTripPostDetail(memberId, postId);

		// Then
		assertThat(actual.fishingTripPostId()).isEqualTo(postId);
		assertThat(actual.name()).isEqualTo("루피");
		assertThat(actual.subject()).isEqualTo("같이 갑시다");
		assertThat(actual.content()).isEqualTo("초보 환영");
		assertThat(actual.currentCount()).isEqualTo(1);
		assertThat(actual.recruitmentCount()).isEqualTo(5);
		assertThat(actual.createDate()).isEqualTo(ZonedDateTime.parse("2025-04-01T12:00:00+09:00"));
		assertThat(actual.fishingDate()).isEqualTo(ZonedDateTime.parse("2025-04-10T06:00:00+09:00"));
		assertThat(actual.fishPointDetailName()).isEqualTo("남해 앞바다");
		assertThat(actual.fishPointName()).isEqualTo("남해");
		assertThat(actual.longitude()).isEqualTo(128.12345);
		assertThat(actual.latitude()).isEqualTo(37.12345);
		assertThat(actual.fileUrlList()).containsExactlyElementsOf(fileUrls);
		assertThat(actual.likeCount()).isEqualTo(3L);
		assertThat(actual.isLiked()).isTrue();
		assertThat(actual.isPostOwner()).isFalse();

		verify(fishingTripPostRepository).findDetailQueryDtoById(postId);
		verify(storageRepository).findAllById(fileIds);
		verify(likeRepository).existsByMemberIdAndTargetTypeAndTargetId(memberId, LikeTargetType.FISHING_TRIP_POST,
			postId);
	}

	@Test
	@DisplayName("동출 게시글 상세 조회 [FISHING_TRIP_POST_NOT_FOUND] [Service] - Fail")
	void t08() {
		// Given
		Long postId = 999L;
		Long memberId = null;

		when(fishingTripPostRepository.findDetailQueryDtoById(postId)).thenReturn(Optional.empty());

		// When & Then
		assertThatThrownBy(() -> fishingTripPostService.getFishingTripPostDetail(memberId, postId))
			.isInstanceOf(FishingTripPostException.class)
			.hasMessage(FishingTripPostErrorCode.FISHING_TRIP_POST_NOT_FOUND.getMessage());

		verify(fishingTripPostRepository).findDetailQueryDtoById(postId);
		verifyNoInteractions(storageRepository); // 파일 조회는 호출되지 않아야 함
	}

	@Test
	@DisplayName("동출 게시글 모집 완료 처리 [작성자 본인일 경우] - Success")
	void t09() {
		// Given
		Long memberId = 1L;
		Long postId = 100L;

		FishingTripPost post = postBuilder
			.set("fishingTripPostId", postId)
			.set("memberId", memberId)
			.set("postStatus", PostStatus.RECRUITING)
			.sample();

		when(fishingTripPostRepository.findById(postId)).thenReturn(Optional.of(post));

		// When
		fishingTripPostService.completeFishingTripPost(memberId, postId);

		// Then
		assertThat(post.getPostStatus()).isEqualTo(PostStatus.COMPLETED);
		verify(fishingTripPostNotifier).notifyMailIfCompleted(post);
	}

	@Test
	@DisplayName("동출 게시글 모집 완료 처리 [작성자가 아닌 경우] - Fail")
	void t10() {
		// Given
		Long memberId = 1L;       // 요청자
		Long postId = 100L;

		FishingTripPost post = postBuilder
			.set("fishingTripPostId", postId)
			.set("memberId", 2L)   // 실제 작성자
			.sample();

		when(fishingTripPostRepository.findById(postId)).thenReturn(Optional.of(post));

		// When & Then
		assertThatThrownBy(() -> fishingTripPostService.completeFishingTripPost(memberId, postId))
			.isInstanceOf(FishingTripPostException.class)
			.hasMessage(FishingTripPostErrorCode.FISHING_TRIP_POST_UNAUTHORIZED_AUTHOR.getMessage());

		verify(fishingTripPostNotifier, never()).notifyMailIfCompleted(any());
	}

	@Test
	@DisplayName("동출 게시글 스크롤 조회 [createdAt] [desc] [첫 페이지] [Service] - Success")
	void t11() {
		// Given
		Long postId = 1L;
		Long fileId = 101L;

		FishingTripPostResponse.DetailPageQueryDto queryDto =
			new FishingTripPostResponse.DetailPageQueryDto(
				postId,
				1L,
				null,
				"테스트 제목",
				"테스트 내용",
				ZonedDateTime.parse("2025-06-10T08:00:00+09:00"),
				ZonedDateTime.parse("2025-04-09T04:00:00+09:00"),
				5,
				PostStatus.RECRUITING,
				List.of(fileId),
				0L,
				0L
			);

		when(fishingTripPostRepository.findScrollDetailPageDto(any(), isNull(), isNull(), isNull()))
			.thenReturn(List.of(queryDto));

		when(storageRepository.findById(fileId)).thenReturn(
			Optional.of(File.builder()
				.fileId(fileId)
				.url("https://cdn.example.com/file.jpg")
				.uploaded(true)
				.build())
		);

		GlobalRequest.CursorRequest cursorRequest = new GlobalRequest.CursorRequest(
			"desc", "createdAt", "next", null, null, 10
		);

		// When
		ScrollResponse<FishingTripPostResponse.DetailPage> result =
			fishingTripPostService.getDetailPage(cursorRequest, null, null, null);

		// Then
		assertThat(result).isNotNull();
		assertThat(result.content()).hasSize(1);

		FishingTripPostResponse.DetailPage dto = result.content().get(0);
		assertThat(dto.fishingTripPostId()).isEqualTo(postId);
		assertThat(dto.imageUrl()).isEqualTo("https://cdn.example.com/file.jpg");

		assertThat(result.isFirst()).isTrue();
		assertThat(result.isLast()).isTrue();
	}

	@Test
	@DisplayName("동출 게시글 스크롤 조회 [createdAt] [desc] [다음 페이지] [Service] - Success")
	void t12() {
		// Given
		Long postId = 1L;
		Long fileId = 101L;

		FishingTripPostResponse.DetailPageQueryDto queryDto =
			new FishingTripPostResponse.DetailPageQueryDto(
				postId,
				1L,
				null,
				"테스트 제목",
				"테스트 내용",
				ZonedDateTime.parse("2025-06-10T08:00:00+09:00"),
				ZonedDateTime.parse("2025-04-09T04:00:00+09:00"),
				5,
				PostStatus.RECRUITING,
				List.of(fileId),
				0L,
				0L
			);

		when(fishingTripPostRepository.findScrollDetailPageDto(any(), isNull(), isNull(), isNull()))
			.thenReturn(List.of(queryDto));

		when(storageRepository.findById(fileId)).thenReturn(
			Optional.of(File.builder()
				.fileId(fileId)
				.url("https://cdn.example.com/file.jpg")
				.uploaded(true)
				.build())
		);

		GlobalRequest.CursorRequest cursorRequest = new GlobalRequest.CursorRequest(
			"desc", "createdAt", "next",
			"2025-04-09T04:00:00+09:00", postId, 10
		);

		// When
		ScrollResponse<FishingTripPostResponse.DetailPage> result =
			fishingTripPostService.getDetailPage(cursorRequest, null, null, null);

		// Then
		assertThat(result).isNotNull();
		assertThat(result.content()).hasSize(1);

		FishingTripPostResponse.DetailPage dto = result.content().get(0);
		assertThat(dto.fishingTripPostId()).isEqualTo(postId);
		assertThat(dto.imageUrl()).isEqualTo("https://cdn.example.com/file.jpg");

		assertThat(result.isFirst()).isFalse();
		assertThat(result.isLast()).isTrue();
	}

	@Test
	@DisplayName("동출 게시글 참여 상세 정보 조회 [Service] - Success")
	void t13() {
		// Given
		Long memberId = 1L;
		Long postId = 100L;
		Long postOwnerId = 1L;
		Long participantId = 2L;

		FishingTripPostResponse.ParticipantDetailDto dto =
			new FishingTripPostResponse.ParticipantDetailDto(
				postId,
				5,
				1, // 현재 인원
				PostStatus.RECRUITING,
				false,
				true,
				postOwnerId,
				"루피",
				"https://cdn.example.com/루피.jpg"
			);

		List<FishingTripPostResponse.ParticipantDetail> participants = List.of(
			new FishingTripPostResponse.ParticipantDetail(
				participantId, "참가자1", "https://cdn.example.com/참가자1.jpg")
		);

		when(fishingTripPostRepository.findParticipantDetailDto(postId, memberId)).thenReturn(dto);
		when(fishingTripPostRepository.findApprovedParticipants(postId)).thenReturn(participants);

		// When
		FishingTripPostResponse.FishingTripPostParticipationDetail result =
			fishingTripPostService.getFishingTripPostParticipationDetail(memberId, postId);

		// Then
		assertThat(result).isNotNull();
		assertThat(result.fishingTripPostId()).isEqualTo(postId);
		assertThat(result.ownerNickname()).isEqualTo("루피");
		assertThat(result.ownerProfileImageUrl()).isEqualTo("https://cdn.example.com/루피.jpg");
		assertThat(result.recruitmentCount()).isEqualTo(5);
		assertThat(result.currentCount()).isEqualTo(1);
		assertThat(result.postStatus()).isEqualTo(PostStatus.RECRUITING);
		assertThat(result.isCurrentUserOwner()).isTrue();
		assertThat(result.isApplicant()).isFalse();

		assertThat(result.participants()).hasSize(1);
		assertThat(result.participants().get(0).nickname()).isEqualTo("참가자1");
		assertThat(result.participants().get(0).profileImageUrl()).isEqualTo("https://cdn.example.com/참가자1.jpg");

		verify(fishingTripPostRepository).findParticipantDetailDto(postId, memberId);
		verify(fishingTripPostRepository).findApprovedParticipants(postId);
	}

	@Test
	@DisplayName("내가 신청한 동출 게시글 목록 커서 기반 조회 [Service] - Success")
	void t14() {
		// Given
		Long memberId = 1L;
		PostStatus postStatus = PostStatus.RECRUITING;

		GlobalRequest.CursorRequest cursorRequest = new GlobalRequest.CursorRequest(
			"desc", "createdAt", "next", null, null, 10
		);

		FishingTripPostResponse.MyFishingTripPostDetailPage dto1 =
			new FishingTripPostResponse.MyFishingTripPostDetailPage(
				100L,                         // fishingTripPostId
				"같이 갑시다",                // subject
				1L,                          // fishingPointId
				"남해",                       // fishingPointName
				"남해 앞바다",                 // fishingPointDetailName
				ZonedDateTime.now().plusDays(2), // fishingDate
				ZonedDateTime.now(),         // createdAt
				1,                           // currentCount
				5,                           // recruitmentCount
				PostStatus.RECRUITING,       // postStatus
				3L,                          // commentCount
				12L                          // likeCount
			);

		FishingTripPostResponse.MyFishingTripPostDetailPage dto2 =
			new FishingTripPostResponse.MyFishingTripPostDetailPage(
				101L, "지려버린 낚시", 2L, "동해", "동해 큰방파제",
				ZonedDateTime.now().plusDays(3), ZonedDateTime.now(), 2, 3,
				PostStatus.RECRUITING, 5L, 8L
			);

		ScrollResponse<FishingTripPostResponse.MyFishingTripPostDetailPage> mockResponse =
			new ScrollResponse<>(
				List.of(dto1, dto2),
				10,
				2,
				false,
				true
			);

		when(fishingTripPostRepository.findMyFishingTripRecruitmentDetailPage(cursorRequest, postStatus, memberId))
			.thenReturn(mockResponse);

		// When
		ScrollResponse<FishingTripPostResponse.MyFishingTripPostDetailPage> result =
			fishingTripPostService.getMyFishingTripPostDetailPage(cursorRequest, memberId, postStatus);

		// Then
		assertThat(result).isNotNull();
		assertThat(result.content()).hasSize(2);

		FishingTripPostResponse.MyFishingTripPostDetailPage resultDto1 = result.content().get(0);
		assertThat(resultDto1.fishingTripPostId()).isEqualTo(100L);
		assertThat(resultDto1.fishingPointName()).isEqualTo("남해");

		FishingTripPostResponse.MyFishingTripPostDetailPage resultDto2 = result.content().get(1);
		assertThat(resultDto2.fishingTripPostId()).isEqualTo(101L);
	}

	@Test
	@DisplayName("내가 작성한 동출 게시글 목록 커서 기반 조회 [Service] - Success")
	void t15() {
		// Given
		Long memberId = 1L;
		PostStatus postStatus = PostStatus.RECRUITING;

		GlobalRequest.CursorRequest cursorRequest = new GlobalRequest.CursorRequest(
			"desc", "createdAt", "next", null, null, 10
		);

		FishingTripPostResponse.MyFishingTripPostDetailPage dto1 =
			new FishingTripPostResponse.MyFishingTripPostDetailPage(
				200L, "서울낚시", 10L, "서해", "서해 갯벌",
				ZonedDateTime.now().plusDays(4), ZonedDateTime.now(),
				3, 6, PostStatus.RECRUITING, 4L, 15L
			);

		FishingTripPostResponse.MyFishingTripPostDetailPage dto2 =
			new FishingTripPostResponse.MyFishingTripPostDetailPage(
				201L, "혼자보단 함께", 11L, "제주", "제주 바다",
				ZonedDateTime.now().plusDays(5), ZonedDateTime.now(),
				2, 5, PostStatus.RECRUITING, 2L, 9L
			);

		ScrollResponse<FishingTripPostResponse.MyFishingTripPostDetailPage> mockResponse =
			new ScrollResponse<>(
				List.of(dto1, dto2),
				10,
				2,
				false,
				true
			);

		when(fishingTripPostRepository.findMyPostFishingTripPostDetailPage(cursorRequest, postStatus, memberId))
			.thenReturn(mockResponse);

		// When
		ScrollResponse<FishingTripPostResponse.MyFishingTripPostDetailPage> result =
			fishingTripPostService.getMyPostFishingTripPostDetailPage(cursorRequest, memberId, postStatus);

		// Then
		assertThat(result).isNotNull();
		assertThat(result.content()).hasSize(2);

		FishingTripPostResponse.MyFishingTripPostDetailPage resultDto1 = result.content().get(0);
		assertThat(resultDto1.fishingTripPostId()).isEqualTo(200L);
		assertThat(resultDto1.fishingPointName()).isEqualTo("서해");

		FishingTripPostResponse.MyFishingTripPostDetailPage resultDto2 = result.content().get(1);
		assertThat(resultDto2.fishingTripPostId()).isEqualTo(201L);
		assertThat(resultDto2.fishingPointName()).isEqualTo("제주");

		verify(fishingTripPostRepository).findMyPostFishingTripPostDetailPage(cursorRequest, postStatus, memberId);
	}
}