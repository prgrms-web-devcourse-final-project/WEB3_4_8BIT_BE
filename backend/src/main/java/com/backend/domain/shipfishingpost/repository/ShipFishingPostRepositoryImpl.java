package com.backend.domain.shipfishingpost.repository;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import com.backend.domain.shipfishingpost.dto.request.ShipFishingPostRequest;
import com.backend.domain.shipfishingpost.dto.response.ShipFishingPostResponse;
import com.backend.domain.shipfishingpost.entity.ShipFishingPost;
import com.backend.global.util.pageutil.Page;

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
	public Optional<ShipFishingPostResponse.Detail> findDetailById(final Long fishingPostId) {

		return shipFishingPostQueryRepository.findDetailById(fishingPostId);
	}

	@Override
	public Optional<ShipFishingPostResponse.DetailAll> findDetailAllById(final Long shipFishingPostId) {

		return shipFishingPostQueryRepository.findDetailAllById(shipFishingPostId);
	}

	@Override
	public Slice<ShipFishingPostResponse.DetailPage> findAllBySearchAndCondition(
		final ShipFishingPostRequest.Search search,
		final Pageable pageable) {

		return shipFishingPostQueryRepository.findDetailPage(search, pageable);
	}
}