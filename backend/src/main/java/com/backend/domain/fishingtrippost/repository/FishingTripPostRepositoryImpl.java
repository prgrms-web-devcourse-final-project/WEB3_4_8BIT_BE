package com.backend.domain.fishingtrippost.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.backend.domain.fishingtrippost.domain.PostStatus;
import com.backend.domain.fishingtrippost.dto.response.FishingTripPostResponse;
import com.backend.domain.fishingtrippost.entity.FishingTripPost;
import com.backend.global.dto.request.GlobalRequest;
import com.backend.global.dto.response.ScrollResponse;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class FishingTripPostRepositoryImpl implements FishingTripPostRepository {

	private final FishingTripPostJpaRepository fishingTripPostJpaRepository;
	private final FishingTripPostQueryRepository fishingTripPostQueryRepository;

	@Override
	public FishingTripPost save(final FishingTripPost fishingTripPost) {
		return fishingTripPostJpaRepository.save(fishingTripPost);
	}

	@Override
	public Optional<FishingTripPost> findById(final Long fishingTripPostId) {
		return fishingTripPostJpaRepository.findById(fishingTripPostId);
	}

	@Override
	public Optional<FishingTripPostResponse.DetailQueryDto> findDetailQueryDtoById(final Long fishingTripPostId) {
		return fishingTripPostQueryRepository.findDetailDtoById(fishingTripPostId);
	}

	@Override
	public boolean existsById(final Long fishingTripPostId) {
		return fishingTripPostJpaRepository.existsById(fishingTripPostId);
	}

	@Override
	public List<FishingTripPostResponse.DetailPageQueryDto> findScrollDetailPageDto(
		final GlobalRequest.CursorRequest cursorRequestDto,
		final PostStatus status,
		final Long regionId,
		final String keyword) {
		return fishingTripPostQueryRepository
			.findScrollDetailPageDto(cursorRequestDto, status, regionId, keyword);
	}

	@Override
	public boolean updateLikeCount(final Long fishingTripPostId, final Long likeCount) {
		return fishingTripPostQueryRepository.updateLikeCount(fishingTripPostId, likeCount);
	}

	@Override
	public void delete(final FishingTripPost fishingTripPost) {
		fishingTripPostJpaRepository.delete(fishingTripPost);
	}

	@Override
	public ScrollResponse<FishingTripPostResponse.MyFishingTripPostDetailPage> findMyFishingTripRecruitmentDetailPage(
		final GlobalRequest.CursorRequest cursorRequestDto,
		final PostStatus postStatus,
		final Long memberId) {
		return fishingTripPostQueryRepository.findMyFishingTripRecruitmentDetailPage(cursorRequestDto, postStatus,
			memberId);
	}

	@Override
	public ScrollResponse<FishingTripPostResponse.MyFishingTripPostDetailPage> findMyPostFishingTripPostDetailPage(
		GlobalRequest.CursorRequest cursorRequestDto, PostStatus postStatus, Long memberId) {
		return fishingTripPostQueryRepository.findMyPostFishingTripPostDetailPage(cursorRequestDto, postStatus,
			memberId);
	}

	@Override
	public List<FishingTripPostResponse.HotPostDto> findHotPostDto() {
		return fishingTripPostQueryRepository.findHotPostDto();
	}

	@Override
	public FishingTripPostResponse.ParticipantDetailDto findParticipantDetailDto(
		final Long fishingTripPostId,
		final Long memberId) {
		return fishingTripPostQueryRepository.findParticipantDetailDto(fishingTripPostId, memberId);
	}

	@Override
	public List<FishingTripPostResponse.ParticipantDetail> findApprovedParticipants(
		final Long fishingTripPostId) {
		return fishingTripPostQueryRepository.findApprovedParticipants(fishingTripPostId);
	}
}
