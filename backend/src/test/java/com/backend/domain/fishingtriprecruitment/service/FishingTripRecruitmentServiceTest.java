package com.backend.domain.fishingtriprecruitment.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.backend.domain.fishingtrippost.exception.FishingTripPostErrorCode;
import com.backend.domain.fishingtrippost.exception.FishingTripPostException;
import com.backend.domain.fishingtrippost.repository.FishingTripPostRepository;
import com.backend.domain.fishingtriprecruitment.dto.request.FishingTripRecruitmentRequest;
import com.backend.domain.fishingtriprecruitment.entity.FishingTripRecruitment;
import com.backend.domain.fishingtriprecruitment.repository.FishingTripRecruitmentRepository;
import com.backend.domain.member.exception.MemberErrorCode;
import com.backend.domain.member.exception.MemberException;
import com.backend.domain.member.repository.MemberRepository;
import com.backend.global.util.BaseTest;

import com.navercorp.fixturemonkey.ArbitraryBuilder;

@ExtendWith(MockitoExtension.class)
class FishingTripRecruitmentServiceTest extends BaseTest {

	@InjectMocks
	private FishingTripRecruitmentServiceImpl fishingTripRecruitmentService;

	@Mock
	private FishingTripRecruitmentRepository fishingTripRecruitmentRepository;

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private FishingTripPostRepository fishingTripPostRepository;

	private final ArbitraryBuilder<FishingTripRecruitmentRequest.Create> createRequestBuilder =
		fixtureMonkeyValidation.giveMeBuilder(FishingTripRecruitmentRequest.Create.class)
			.set("fishingLevel", "BEGINNER");

	private final ArbitraryBuilder<FishingTripRecruitment> recruitmentBuilder =
		fixtureMonkeyBuilder.giveMeBuilder(FishingTripRecruitment.class);

	@Test
	@DisplayName("동출 모집 신청 [Service] - Success")
	void t01() {
		// Given
		Long memberId = 1L;
		FishingTripRecruitmentRequest.Create requestDto = createRequestBuilder
			.set("fishingTripPostId", 1L)
			.set("introduction", "초보입니다!")
			.sample();

		FishingTripRecruitment saved = recruitmentBuilder
			.set("fishingTripRecruitmentId", 1L)
			.set("memberId", memberId)
			.set("fishingTripPostId", requestDto.fishingTripPostId())
			.sample();

		when(memberRepository.existsById(memberId)).thenReturn(true);
		when(fishingTripPostRepository.existsById(requestDto.fishingTripPostId())).thenReturn(true);
		when(fishingTripRecruitmentRepository.save(any())).thenReturn(saved);

		// When
		Long result = fishingTripRecruitmentService.createFishingTripRecruitment(memberId, requestDto);

		// Then
		assertThat(result).isEqualTo(saved.getFishingTripRecruitmentId());

		verify(memberRepository).existsById(memberId);
		verify(fishingTripPostRepository).existsById(requestDto.fishingTripPostId());
		verify(fishingTripRecruitmentRepository).save(any(FishingTripRecruitment.class));
	}

	@Test
	@DisplayName("동출 모집 신청 실패 [MEMBER_NOT_FOUND] [Service] - Fail")
	void t02() {
		// Given
		Long memberId = 999L;
		FishingTripRecruitmentRequest.Create requestDto = createRequestBuilder.sample();

		when(memberRepository.existsById(memberId)).thenReturn(false);

		// When & Then
		assertThatThrownBy(() -> fishingTripRecruitmentService.createFishingTripRecruitment(memberId, requestDto))
			.isInstanceOf(MemberException.class)
			.hasFieldOrPropertyWithValue("errorCode", MemberErrorCode.MEMBER_NOT_FOUND)
			.hasMessageContaining(MemberErrorCode.MEMBER_NOT_FOUND.getMessage());

		verify(memberRepository).existsById(memberId);
		verify(fishingTripPostRepository, never()).existsById(any());
		verify(fishingTripRecruitmentRepository, never()).save(any());
	}

	@Test
	@DisplayName("동출 모집 신청 실패 [FISHING_TRIP_POST_NOT_FOUND] [Service] - Fail")
	void t03() {
		// Given
		Long memberId = 1L;
		FishingTripRecruitmentRequest.Create requestDto = createRequestBuilder
			.set("fishingTripPostId", 999L)
			.sample();

		when(memberRepository.existsById(memberId)).thenReturn(true);
		when(fishingTripPostRepository.existsById(requestDto.fishingTripPostId())).thenReturn(false);

		// When & Then
		assertThatThrownBy(() -> fishingTripRecruitmentService.createFishingTripRecruitment(memberId, requestDto))
			.isInstanceOf(FishingTripPostException.class)
			.hasFieldOrPropertyWithValue("errorCode", FishingTripPostErrorCode.FISHING_TRIP_POST_NOT_FOUND)
			.hasMessageContaining(FishingTripPostErrorCode.FISHING_TRIP_POST_NOT_FOUND.getMessage());

		verify(memberRepository).existsById(memberId);
		verify(fishingTripPostRepository).existsById(requestDto.fishingTripPostId());
		verify(fishingTripRecruitmentRepository, never()).save(any());
	}
}
