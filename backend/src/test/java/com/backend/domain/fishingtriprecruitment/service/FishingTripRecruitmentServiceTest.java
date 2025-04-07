package com.backend.domain.fishingtriprecruitment.service;

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

import com.backend.domain.fishingtrippost.entity.FishingTripPost;
import com.backend.domain.fishingtrippost.exception.FishingTripPostErrorCode;
import com.backend.domain.fishingtrippost.exception.FishingTripPostException;
import com.backend.domain.fishingtrippost.repository.FishingTripPostRepository;
import com.backend.domain.fishingtriprecruitment.domain.FishingLevel;
import com.backend.domain.fishingtriprecruitment.domain.RecruitmentStatus;
import com.backend.domain.fishingtriprecruitment.dto.request.FishingTripRecruitmentRequest;
import com.backend.domain.fishingtriprecruitment.dto.response.FishingTripRecruitmentResponse;
import com.backend.domain.fishingtriprecruitment.entity.FishingTripRecruitment;
import com.backend.domain.fishingtriprecruitment.repository.FishingTripRecruitmentRepository;
import com.backend.domain.member.exception.MemberErrorCode;
import com.backend.domain.member.exception.MemberException;
import com.backend.domain.member.repository.MemberRepository;
import com.backend.global.dto.request.GlobalRequest;
import com.backend.global.dto.response.ScrollResponse;
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

	@Test
	@DisplayName("동출 모집 신청 거절 [Service] - Success")
	void t04() {
		// Given
		Long authorId = 1L;
		Long recruitmentId = 10L;
		Long postId = 100L;

		FishingTripRecruitment recruitment = recruitmentBuilder
			.set("fishingTripRecruitmentId", recruitmentId)
			.set("memberId", 2L)
			.set("fishingTripPostId", postId)
			.set("recruitmentStatus", RecruitmentStatus.PENDING)
			.sample();

		FishingTripPost post = FishingTripPost.builder()
			.fishingTripPostId(postId)
			.memberId(authorId)
			.subject("같이 가요~")
			.build();

		when(fishingTripRecruitmentRepository.findById(recruitmentId)).thenReturn(java.util.Optional.of(recruitment));
		when(fishingTripPostRepository.findById(postId)).thenReturn(java.util.Optional.of(post));

		// When
		fishingTripRecruitmentService.refuseFishingTripRecruitment(authorId, recruitmentId);

		// Then
		assertThat(recruitment.getRecruitmentStatus()).isEqualTo(RecruitmentStatus.REJECTED);
		verify(fishingTripRecruitmentRepository).findById(recruitmentId);
		verify(fishingTripPostRepository).findById(postId);
	}

	@Test
	@DisplayName("동출 모집 신청 거절 실패 [FISHING_TRIP_POST_UNAUTHORIZED_AUTHOR] [Service] - Fail")
	void t05() {
		// Given
		Long unauthorizedUserId = 2L;
		Long recruitmentId = 10L;
		Long postId = 100L;

		FishingTripRecruitment recruitment = recruitmentBuilder
			.set("fishingTripRecruitmentId", recruitmentId)
			.set("fishingTripPostId", postId)
			.sample();

		FishingTripPost post = FishingTripPost.builder()
			.fishingTripPostId(postId)
			.memberId(1L)
			.build();

		when(fishingTripRecruitmentRepository.findById(recruitmentId)).thenReturn(java.util.Optional.of(recruitment));
		when(fishingTripPostRepository.findById(postId)).thenReturn(java.util.Optional.of(post));

		// When & Then
		assertThatThrownBy(() ->
			fishingTripRecruitmentService.refuseFishingTripRecruitment(unauthorizedUserId, recruitmentId))
			.isInstanceOf(FishingTripPostException.class)
			.hasFieldOrPropertyWithValue("errorCode", FishingTripPostErrorCode.FISHING_TRIP_POST_UNAUTHORIZED_AUTHOR)
			.hasMessageContaining(FishingTripPostErrorCode.FISHING_TRIP_POST_UNAUTHORIZED_AUTHOR.getMessage());

		verify(fishingTripRecruitmentRepository).findById(recruitmentId);
		verify(fishingTripPostRepository).findById(postId);
	}

	@Test
	@DisplayName("동출 모집 신청자 목록 커서 페이징 조회 [Service] - Success")
	void t06() {
		// Given
		Long memberId = 1L;
		Long fishingTripPostId = 100L;
		RecruitmentStatus status = RecruitmentStatus.PENDING;
		GlobalRequest.CursorRequest cursorRequest = new GlobalRequest.CursorRequest(null, "createdAt", "next", null, null, 10);

		FishingTripPost post = FishingTripPost.builder()
			.fishingTripPostId(fishingTripPostId)
			.memberId(memberId)
			.build();

		List<FishingTripRecruitmentResponse.DetailPage> content = List.of(
			new FishingTripRecruitmentResponse.DetailPage(
				1L, "닉네임", "image.png", FishingLevel.BEGINNER, "소개글", status, ZonedDateTime.now()
			)
		);

		ScrollResponse<FishingTripRecruitmentResponse.DetailPage> expected = ScrollResponse.from(
			content, 10, 1, true, true
		);

		when(fishingTripPostRepository.findById(fishingTripPostId)).thenReturn(Optional.of(post));
		when(fishingTripRecruitmentRepository.findDetailPageByFishingTripPostIdAndStatus(
			cursorRequest, fishingTripPostId, status
		)).thenReturn(expected);

		// When
		ScrollResponse<FishingTripRecruitmentResponse.DetailPage> actual =
			fishingTripRecruitmentService.getDetailPageList(memberId, cursorRequest, fishingTripPostId, status);

		// Then
		assertThat(actual).isNotNull();
		assertThat(actual.content()).hasSize(1);
		assertThat(actual.content().get(0).recruitmentStatus()).isEqualTo(status);

		verify(fishingTripPostRepository).findById(fishingTripPostId);
		verify(fishingTripRecruitmentRepository).findDetailPageByFishingTripPostIdAndStatus(cursorRequest, fishingTripPostId, status);
	}

	@Test
	@DisplayName("동출 모집 신청자 목록 조회 실패 [FISHING_TRIP_POST_UNAUTHORIZED_AUTHOR] [Service] - Fail")
	void t07() {
		// Given
		Long memberId = 2L;
		Long fishingTripPostId = 100L;
		RecruitmentStatus status = RecruitmentStatus.PENDING;
		GlobalRequest.CursorRequest cursorRequest = new GlobalRequest.CursorRequest(null, "createdAt", "next", null, null, 10);

		FishingTripPost post = FishingTripPost.builder()
			.fishingTripPostId(fishingTripPostId)
			.memberId(1L)
			.build();

		when(fishingTripPostRepository.findById(fishingTripPostId)).thenReturn(Optional.of(post));

		// When & Then
		assertThatThrownBy(() -> fishingTripRecruitmentService.getDetailPageList(memberId, cursorRequest, fishingTripPostId, status))
			.isInstanceOf(FishingTripPostException.class)
			.hasFieldOrPropertyWithValue("errorCode", FishingTripPostErrorCode.FISHING_TRIP_POST_UNAUTHORIZED_AUTHOR)
			.hasMessageContaining(FishingTripPostErrorCode.FISHING_TRIP_POST_UNAUTHORIZED_AUTHOR.getMessage());

		verify(fishingTripPostRepository).findById(fishingTripPostId);
		verify(fishingTripRecruitmentRepository, never()).findDetailPageByFishingTripPostIdAndStatus(any(), any(), any());
	}

}
