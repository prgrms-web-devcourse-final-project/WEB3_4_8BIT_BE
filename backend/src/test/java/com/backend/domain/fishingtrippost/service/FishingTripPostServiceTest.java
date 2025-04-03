package com.backend.domain.fishingtrippost.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.ZonedDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.backend.domain.fishingtrippost.dto.request.FishingTripPostRequest;
import com.backend.domain.fishingtrippost.entity.FishingTripPost;
import com.backend.domain.fishingtrippost.repository.FishingTripPostRepository;
import com.backend.domain.fishpoint.exception.FishPointErrorCode;
import com.backend.domain.fishpoint.exception.FishPointException;
import com.backend.domain.fishpoint.repository.FishPointRepository;
import com.backend.domain.member.entity.Member;
import com.backend.domain.member.exception.MemberErrorCode;
import com.backend.domain.member.exception.MemberException;
import com.backend.domain.member.repository.MemberRepository;
import com.backend.global.util.BaseTest;
import com.navercorp.fixturemonkey.ArbitraryBuilder;

@ExtendWith(MockitoExtension.class)
class FishingTripPostServiceTest extends BaseTest {

	@InjectMocks
	private FishingTripPostServiceImpl fishingTripPostService;

	@Mock
	private FishPointRepository fishPointRepository;

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private FishingTripPostRepository fishingTripPostRepository;

	private final ArbitraryBuilder<FishingTripPostRequest.Create> createRequestBuilder =
		fixtureMonkeyValidation.giveMeBuilder(FishingTripPostRequest.Create.class);

	private final ArbitraryBuilder<FishingTripPost> postBuilder =
		fixtureMonkeyBuilder.giveMeBuilder(FishingTripPost.class);

	@Test
	@DisplayName("동출 게시글 등록 [Service] - Success")
	void t01() {
		// Given

		Member givenMember = fixtureMonkeyBuilder.giveMeBuilder(Member.class)
			.set("memberId", 1L)
			.sample();

		FishingTripPostRequest.Create givenRequestDto = fixtureMonkeyValidation.giveMeBuilder(FishingTripPostRequest.Create.class)
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

		FishingTripPostRequest.Create requestDto = createRequestBuilder
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

		FishingTripPostRequest.Create requestDto = createRequestBuilder
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
}
