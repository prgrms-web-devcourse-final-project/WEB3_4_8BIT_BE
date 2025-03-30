package com.backend.domain.shipfishingpost.repository;

import static com.backend.domain.shipfishingpost.entity.QShipFishingPost.*;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.backend.domain.shipfishingpost.dto.response.QShipFishingPostResponse_Detail;
import com.backend.domain.shipfishingpost.dto.response.ShipFishingPostResponse;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ShipFishingPostQueryRepository {

	private final JPAQueryFactory jpaQueryFactory;

	public Optional<ShipFishingPostResponse.Detail> findDetailById(final Long shipFishingPostId) {
		ShipFishingPostResponse.Detail detail = jpaQueryFactory
			.select(new QShipFishingPostResponse_Detail(
				shipFishingPost.shipFishingPostId,
				shipFishingPost.subject,
				shipFishingPost.content,
				shipFishingPost.price,
				shipFishingPost.imageList,
				shipFishingPost.startTime,
				shipFishingPost.durationTime,
				shipFishingPost.maxGuestCount,
				shipFishingPost.reviewEverRate
			))
			.from(shipFishingPost)
			.where(shipFishingPost.shipFishingPostId.eq(shipFishingPostId))
			.fetchOne();

		return Optional.ofNullable(detail);
	}

}