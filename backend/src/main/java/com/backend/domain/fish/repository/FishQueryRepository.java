package com.backend.domain.fish.repository;

import static com.backend.domain.fish.entity.QFish.*;
import static com.backend.global.storage.entity.QFile.*;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.backend.domain.fish.dto.FishResponse;
import com.backend.domain.fish.dto.QFishResponse_Detail;
import com.backend.domain.fish.dto.QFishResponse_Popular;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class FishQueryRepository {

	private final JPAQueryFactory jpaQueryFactory;

	public Optional<FishResponse.Detail> findDetailById(final Long fishId) {
		FishResponse.Detail findDetail = jpaQueryFactory
			.select(new QFishResponse_Detail(
				fish.fishId,
				fish.name, fish.
				description,
				file.url,
				fish.spawnSeasonList,
				fish.spawnLocation
			))
			.from(fish)
			.leftJoin(file)
			.on(fish.fileId.eq(file.fileId))
			.where(fish.fishId.eq(fishId))
			.fetchOne();

		return Optional.ofNullable(findDetail);
	}

	public List<FishResponse.Popular> findPopular(final Integer size) {

		return jpaQueryFactory
			.select(new QFishResponse_Popular(
				fish.fishId,
				fish.name,
				fish.spawnSeasonList,
				fish.popularityScore,
				file.url))
			.from(fish)
			.leftJoin(file)
			.on(fish.fileId.eq(file.fileId))
			.orderBy(new OrderSpecifier<>(Order.DESC, fish.popularityScore))
			.limit(size)
			.fetch();
	}
}
