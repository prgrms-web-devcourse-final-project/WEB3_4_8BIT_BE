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

import com.backend.domain.fishingtrippost.dto.request.FishingTripPostRequest;
import com.backend.domain.fishingtrippost.entity.FishingTripPost;
import com.backend.domain.fishingtrippost.exception.FishingTripPostErrorCode;
import com.backend.domain.fishingtrippost.exception.FishingTripPostException;
import com.backend.domain.fishingtrippost.repository.FishingTripPostRepository;
import com.backend.domain.fishpoint.exception.FishPointErrorCode;
import com.backend.domain.fishpoint.exception.FishPointException;
import com.backend.domain.fishpoint.repository.FishPointRepository;
import com.backend.domain.member.entity.Member;
import com.backend.domain.member.exception.MemberErrorCode;
import com.backend.domain.member.exception.MemberException;
import com.backend.domain.member.repository.MemberRepository;
import com.backend.global.storage.service.StorageService;
import com.backend.global.util.BaseTest;

import com.navercorp.fixturemonkey.ArbitraryBuilder;

@ExtendWith(MockitoExtension.class)
class FishingTripPostServiceTest extends BaseTest {

	@InjectMocks
	private FishingTripPostServiceImpl fishingTripPostService;

	@Mock
	private StorageService storageService;

	@Mock
	private FishPointRepository fishPointRepository;

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private FishingTripPostRepository fishingTripPostRepository;

	private final ArbitraryBuilder<FishingTripPostRequest.Form> createRequestBuilder =
		fixtureMonkeyValidation.giveMeBuilder(FishingTripPostRequest.Form.class);

	private final ArbitraryBuilder<FishingTripPost> postBuilder =
		fixtureMonkeyBuilder.giveMeBuilder(FishingTripPost.class);

	@Test
	@DisplayName("동출 게시글 등록 [Service] - Success")
	void t01() {
		// Given

		Member givenMember = fixtureMonkeyBuilder.giveMeBuilder(Member.class)
			.set("memberId", 1L)
			.sample();

		FishingTripPostRequest.Form givenRequestDto = fixtureMonkeyValidation.giveMeBuilder(
				FishingTripPostRequest.Form.class)
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

		// When
		Long savedId = fishingTripPostService.createFishingTripPost(givenMember.getMemberId(), givenRequestDto);

		// Then
		assertThat(savedId).isEqualTo(savedPost.getFishingTripPostId());

		verify(memberRepository).existsById(givenMember.getMemberId());
		verify(fishPointRepository).existsById(givenRequestDto.fishingPointId());
		verify(fishingTripPostRepository).save(any(FishingTripPost.class));
	}

	@Test
	@DisplayName("동출 게시글 등록 [MEMBER_NOT_FOUND] [Service] - Fail")
	void t02() {
		// Given
		Long memberId = 999L;

		FishingTripPostRequest.Form requestDto = createRequestBuilder
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

		FishingTripPostRequest.Form requestDto = createRequestBuilder
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

		// 기존에 저장되어 있던 이미지 ID
		List<Long> originalFileIds = List.of(10L, 30L); // 기존 이미지 10, 30
		List<Long> updatedFileIds = List.of(10L, 20L);  // 새로 들어온 이미지 10, 20 → 30은 삭제 대상

		FishingTripPostRequest.Form requestDto = FishingTripPostRequest.Form.builder()
			.subject("수정된 제목")
			.content("수정된 내용")
			.recruitmentCount(2)
			.isShipFish(false)
			.fishingDate(ZonedDateTime.now().plusDays(5))
			.fishingPointId(42L)
			.fileIdList(updatedFileIds)
			.build();

		FishingTripPost existingPost = mock(FishingTripPost.class);
		when(existingPost.getMemberId()).thenReturn(memberId);
		when(existingPost.getFileIdList()).thenReturn(originalFileIds);
		when(existingPost.getFishingPointId()).thenReturn(42L);

		when(fishingTripPostRepository.findById(postId)).thenReturn(Optional.of(existingPost));

		// storageService 삭제 로직 모킹
		doNothing().when(storageService).deleteFilesByIdList(eq(memberId), eq(List.of(30L)));

		// When
		Long result = fishingTripPostService.updateFishingTripPost(memberId, postId, requestDto);

		// Then
		assertThat(result).isEqualTo(requestDto.fishingPointId());
		verify(fishingTripPostRepository).findById(postId);
		verify(storageService).deleteFilesByIdList(memberId, List.of(30L));
		verify(existingPost).updateFishingTripPost(
			eq("수정된 제목"),
			eq("수정된 내용"),
			eq(2),
			eq(false),
			any(),
			eq(42L),
			eq(updatedFileIds)
		);
	}


	@Test
	@DisplayName("동출 게시글 수정 [FISHING_TRIP_POST_NOT_FOUND] [Service] - Fail")
	void t05() {
		// Given
		Long memberId = 1L;
		Long postId = 999L;

		FishingTripPostRequest.Form requestDto = createRequestBuilder.sample();

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

		FishingTripPostRequest.Form requestDto = createRequestBuilder.sample();

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

}
