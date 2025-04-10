package com.backend.domain.shipfishingpost.repository;

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
	public ScrollResponse<ShipFishingPostResponse.DetailScroll> findDetailScrollBySearch(
		final ShipFishingPostRequest.Search requestDto,
		final GlobalRequest.CursorRequest cursorRequestDto) {

		return shipFishingPostQueryRepository.findDetailScrollBySearch(requestDto, cursorRequestDto);
	}

	@Override
	public void deleteById(final Long shipFishingPostId) {
		shipFishingPostJpaRepository.deleteById(shipFishingPostId);
	}

	@Override
	public boolean existsById(final Long shipFishingPostId) {
		return shipFishingPostJpaRepository.existsById(shipFishingPostId);
	}

	@Override
	public void updateLikeCount(final Long shipFishingPostId, final Long likeCount) {
		shipFishingPostQueryRepository.updateLikeCount(shipFishingPostId, likeCount);
  }
  
  @Override
	public boolean existsByShipId(final Long shipId) {
		return shipFishingPostJpaRepository.existsByShipId(shipId);
	}
}