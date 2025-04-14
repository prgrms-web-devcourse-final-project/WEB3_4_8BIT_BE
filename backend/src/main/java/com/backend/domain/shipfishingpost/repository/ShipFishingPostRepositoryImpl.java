package com.backend.domain.shipfishingpost.repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.backend.domain.shipfishingpost.dto.request.ShipFishingPostRequest;
import com.backend.domain.shipfishingpost.dto.response.ShipFishingPostResponse;
import com.backend.domain.shipfishingpost.entity.ShipFishingPost;
import com.backend.global.dto.request.GlobalRequest;
import com.backend.global.dto.response.ScrollResponse;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ShipFishingPostRepositoryImpl implements ShipFishingPostRepository {

	private final ShipFishingPostJpaRepository shipFishingPostJpaRepository;
	private final ShipFishingPostQueryRepository shipFishingPostQueryRepository;

	@Override
	public ShipFishingPost save(final ShipFishingPost shipFishingPost) {

		return shipFishingPostJpaRepository.save(shipFishingPost);
	}

	@Override
	public Optional<ShipFishingPost> findById(Long shipFishingPostId) {

		return shipFishingPostJpaRepository.findById(shipFishingPostId);
	}

	@Override
	public Optional<ShipFishingPostResponse.DetailAll> findDetailAllById(final Long shipFishingPostId) {

		return shipFishingPostQueryRepository.findDetailAllById(shipFishingPostId);
	}

	@Override
	public List<ShipFishingPostResponse.MyPagePostList> findMyPagePostList(final Long memberId) {

		return shipFishingPostQueryRepository.findDetailMyPageListByMemberId(memberId);
	}

	@Override
	public ScrollResponse<ShipFishingPostResponse.DetailScroll> findDetailScrollBySearch(
		final Long memberId,
		final ShipFishingPostRequest.Search requestDto,
		final GlobalRequest.CursorRequest cursorRequestDto) {

		return shipFishingPostQueryRepository.findDetailScrollBySearch(memberId, requestDto, cursorRequestDto);
	}

	@Override
	public List<ShipFishingPostResponse.MainPageHotPost> findMainPageHotPostWithSize(final Integer size) {

		return shipFishingPostQueryRepository.findMainPageHotPostListWithSize(size);
	}

	@Override
	public void deleteById(final Long shipFishingPostId) {

		shipFishingPostJpaRepository.deleteById(shipFishingPostId);
	}

	@Override
	public void updateReviewEverRate(final ZonedDateTime now, final ZonedDateTime lastRun) {

		shipFishingPostQueryRepository.updateReviewEverRate(now, lastRun);
	}

	@Override
	public void updateReviewEverRateByDelete(final Long shipFishingPostId) {

		shipFishingPostQueryRepository.updateReviewEverRateByDeleteReview(shipFishingPostId);
	}

	@Override
	public boolean existsById(final Long shipFishingPostId) {

		return shipFishingPostJpaRepository.existsById(shipFishingPostId);
	}

	@Override
	public boolean updateLikeCount(final Long shipFishingPostId, final Long likeCount) {

		return shipFishingPostQueryRepository.updateLikeCount(shipFishingPostId, likeCount);
	}

	@Override
	public boolean existsByShipId(final Long shipId) {

		return shipFishingPostJpaRepository.existsByShipId(shipId);
	}

	@Override
	public String findSubjectByShipFishingPostId(Long shipFishingPostId) {

		return shipFishingPostQueryRepository.findSubjectByShipFishingPostId(shipFishingPostId);
	}
}