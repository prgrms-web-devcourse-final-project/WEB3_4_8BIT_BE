package com.backend.domain.like.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.backend.domain.fish.repository.FishRepository;
import com.backend.domain.fishingtrippost.entity.FishingTripPost;
import com.backend.domain.fishingtrippost.exception.FishingTripPostException;
import com.backend.domain.fishingtrippost.repository.FishingTripPostRepository;
import com.backend.domain.like.domain.LikeTargetType;
import com.backend.domain.like.dto.request.LikeRequest;
import com.backend.domain.like.dto.response.LikeResponse;
import com.backend.domain.like.entity.Like;
import com.backend.domain.like.repository.LikeRepository;
import com.backend.domain.member.entity.Member;
import com.backend.global.dto.request.GlobalRequest;
import com.backend.global.dto.response.ScrollResponse;
import com.backend.global.util.BaseTest;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest extends BaseTest {

	@InjectMocks
	private LikeServiceImpl likeService;

	@Mock
	private LikeRepository likeRepository;

	@Mock
	private FishRepository fishRepository;

	@Mock
	private LikeCacheService likeCacheService;

	@Mock
	private FishingTripPostRepository fishingTripPostRepository;

	@Test
	@DisplayName("좋아요 생성 [Service] - Success")
	void t01() {
		Member givenMember = fixtureMonkeyBuilder.giveMeBuilder(Member.class)
			.set("memberId", 1L)
			.sample();

		FishingTripPost savedPost = fixtureMonkeyBuilder.giveMeBuilder(FishingTripPost.class)
			.set("fishingTripPostId", 1L)
			.set("memberId", 1L)
			.sample();

		LikeRequest givenRequestDto = fixtureMonkeyBuilder.giveMeBuilder(LikeRequest.class)
			.set("targetType", LikeTargetType.FISHING_TRIP_POST)
			.set("targetId", savedPost.getFishingTripPostId())
			.sample();

		when(fishingTripPostRepository.existsById(savedPost.getFishingTripPostId())).thenReturn(true);
		when(likeRepository.findByMemberIdAndTargetTypeAndTargetId(givenMember.getMemberId(),
			givenRequestDto.targetType(), givenRequestDto.targetId()))
			.thenReturn(Optional.empty());

		likeService.toggleLike(givenMember.getMemberId(), givenRequestDto);

		verify(likeRepository).save(any(Like.class));
		verify(likeCacheService).updateLikeCountCache(givenRequestDto.targetType(), givenRequestDto.targetId(), true);
	}

	@Test
	@DisplayName("좋아요 생성 [Service] - Fail: 존재하지 않는 게시글")
	void t02() {
		// Given
		Member givenMember = fixtureMonkeyBuilder.giveMeBuilder(Member.class)
			.set("memberId", 1L)
			.sample();

		LikeRequest givenRequestDto = new LikeRequest(
			LikeTargetType.FISHING_TRIP_POST,
			999L
		);

		when(fishingTripPostRepository.existsById(givenRequestDto.targetId())).thenReturn(false);

		assertThatThrownBy(() -> likeService.toggleLike(givenMember.getMemberId(), givenRequestDto))
			.isInstanceOf(FishingTripPostException.class);
	}

	@Test
	@DisplayName("좋아요 취소 [Service] - Success")
	void t03() {
		Member givenMember = fixtureMonkeyBuilder.giveMeBuilder(Member.class)
			.set("memberId", 1L)
			.sample();
		LikeRequest givenRequestDto = new LikeRequest(LikeTargetType.FISHING_TRIP_POST, 1L);

		Like rawLike = fixtureMonkeyBuilder.giveMeBuilder(Like.class)
			.set("memberId", givenMember.getMemberId())
			.set("targetType", givenRequestDto.targetType())
			.set("targetId", givenRequestDto.targetId())
			.set("isDeleted", false)
			.sample();
		Like like = spy(rawLike);
		when(like.isActive()).thenReturn(true);

		when(likeRepository.findByMemberIdAndTargetTypeAndTargetId(
			givenMember.getMemberId(),
			givenRequestDto.targetType(),
			givenRequestDto.targetId()
		)).thenReturn(Optional.of(like));
		when(fishingTripPostRepository.existsById(givenRequestDto.targetId())).thenReturn(true);

		likeService.toggleLike(givenMember.getMemberId(), givenRequestDto);

		verify(likeRepository).deleteByMemberIdAndTargetTypeAndTargetId(
			givenMember.getMemberId(),
			givenRequestDto.targetType(),
			givenRequestDto.targetId()
		);
		verify(likeCacheService).updateLikeCountCache(givenRequestDto.targetType(), givenRequestDto.targetId(), false);
	}

	@Test
	@DisplayName("좋아요 복구 [Service] - Success")
	void t04() {
		Member givenMember = fixtureMonkeyBuilder.giveMeBuilder(Member.class)
			.set("memberId", 1L)
			.sample();
		LikeRequest givenRequestDto = new LikeRequest(LikeTargetType.FISHING_TRIP_POST, 2L);

		Like rawLike = fixtureMonkeyBuilder.giveMeBuilder(Like.class)
			.set("memberId", givenMember.getMemberId())
			.set("targetType", givenRequestDto.targetType())
			.set("targetId", givenRequestDto.targetId())
			.set("isDeleted", true)
			.sample();
		Like like = spy(rawLike);
		when(like.isActive()).thenReturn(false);

		when(likeRepository.findByMemberIdAndTargetTypeAndTargetId(
			givenMember.getMemberId(),
			givenRequestDto.targetType(),
			givenRequestDto.targetId()
		)).thenReturn(Optional.of(like));
		when(fishingTripPostRepository.existsById(givenRequestDto.targetId())).thenReturn(true);

		likeService.toggleLike(givenMember.getMemberId(), givenRequestDto);

		verify(likeRepository).restoreByMemberIdAndTargetTypeAndTargetId(
			givenMember.getMemberId(),
			givenRequestDto.targetType(),
			givenRequestDto.targetId()
		);
		verify(likeCacheService).updateLikeCountCache(givenRequestDto.targetType(), givenRequestDto.targetId(), true);
	}

	@Test
	@DisplayName("동출 좋아요 게시글 조회 [Service] - Success")
	void t05() {
		Long memberId = 1L;
		Long likeId = 1L;
		GlobalRequest.CursorRequest cursorRequest = new GlobalRequest.CursorRequest(
			"desc", "createdAt", "next",
			"2025-04-09T04:00:00+09:00", likeId, 10
		);

		List<LikeResponse.FishingTripPostLikedQueryDto> fakeDtos = List.of(
			LikeResponse.FishingTripPostLikedQueryDto.builder()
				.fishingTripPostId(1L)
				.subject("테스트")
				.build()
		);
		when(likeRepository.getFishingTripPostLikedDetailPage(cursorRequest, memberId))
			.thenReturn(fakeDtos);

		ScrollResponse<LikeResponse.FishingTripPostLikedDetailResponse> result = likeService.getLikedFishingTripPosts(
			cursorRequest, memberId);

		assertThat(result.content()).hasSize(1);
	}

	@Test
	@DisplayName("동출 좋아요 게시글 조회 [Service] - Fail")
	void t06() {
		Long memberId = 1L;
		Long likeId = 1L;
		GlobalRequest.CursorRequest cursorRequest = new GlobalRequest.CursorRequest(
			"desc", "createdAt", "next",
			"2025-04-09T04:00:00+09:00", likeId, 10
		);

		when(likeRepository.getFishingTripPostLikedDetailPage(cursorRequest, memberId))
			.thenThrow(new RuntimeException("조회 실패"));

		assertThatThrownBy(() -> likeService.getLikedFishingTripPosts(cursorRequest, memberId))
			.isInstanceOf(RuntimeException.class)
			.hasMessageContaining("조회 실패");
	}

	@Test
	@DisplayName("선상 좋아요 게시글 조회 [Service] - Success")
	void t07() {
		Long memberId = 1L;
		Long likeId = 1L;
		GlobalRequest.CursorRequest cursorRequest = new GlobalRequest.CursorRequest(
			"desc", "createdAt", "next",
			"2025-04-09T04:00:00+09:00", likeId, 10
		);


		List<LikeResponse.ShipFishingPostLikedQueryDto> fakeDtos = List.of(
			LikeResponse.ShipFishingPostLikedQueryDto.builder()
				.shipFishingPostId(1L)
				.subject("테스트")
				.fishIdList(List.of(1L))
				.build()
		);

		when(likeRepository.getShipFishingPostLikedDetailPage(cursorRequest, memberId)).thenReturn(fakeDtos);
		when(fishRepository.findFishNameListByIdList(any())).thenReturn(List.of("광어"));

		ScrollResponse<LikeResponse.ShipFishingPostLikedDetailResponse> result = likeService.getLikedShipFishingPosts(
			cursorRequest, memberId);

		assertThat(result.content()).hasSize(1);
	}

	@Test
	@DisplayName("선상 좋아요 게시글 조회 [Service] - Fail")
	void t08() {
		Long memberId = 1L;
		Long likeId = 1L;
		GlobalRequest.CursorRequest cursorRequest = new GlobalRequest.CursorRequest(
			"desc", "createdAt", "next",
			"2025-04-09T04:00:00+09:00", likeId, 10
		);

		List<LikeResponse.ShipFishingPostLikedQueryDto> fakeDtos = List.of(
			LikeResponse.ShipFishingPostLikedQueryDto.builder()
				.shipFishingPostId(1L)
				.fishIdList(List.of(1L))
				.build()
		);

		when(likeRepository.getShipFishingPostLikedDetailPage(cursorRequest, memberId)).thenReturn(fakeDtos);
		when(fishRepository.findFishNameListByIdList(any())).thenThrow(new RuntimeException("fish 오류"));

		assertThatThrownBy(() -> likeService.getLikedShipFishingPosts(cursorRequest, memberId))
			.isInstanceOf(RuntimeException.class)
			.hasMessageContaining("fish 오류");
	}

}
