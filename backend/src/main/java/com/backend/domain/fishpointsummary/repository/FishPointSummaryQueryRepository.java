package com.backend.domain.fishpointsummary.repository;

import static com.backend.domain.fish.entity.QFish.*;
import static com.backend.domain.fishpointsummary.dto.response.FishPointSummaryResponse.*;
import static com.backend.domain.fishpointsummary.entity.QFishPointSummary.*;
import static com.backend.global.storage.entity.QFile.*;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.backend.domain.fishpointsummary.dto.response.QFishPointSummaryResponse_Basic;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class FishPointSummaryQueryRepository {

	private final JPAQueryFactory jpaQueryFactory;

	public List<Basic> findTop4ByFishPointIdOrderByTotalCountDesc(final Long fishPointId) {

		return jpaQueryFactory
			.select(new QFishPointSummaryResponse_Basic(
				fishPointSummary.fishId,
				fish.name,
				file.url,
				fish.spawnSeasonList,
				fishPointSummary.totalCount
			))
			.from(fishPointSummary)
			.innerJoin(fish).on(fishPointSummary.fishId.eq(fish.fishId))
			.innerJoin(file).on(fishPointSummary.fileId.eq(file.fileId))
			.where(fishPointSummary.fishPointId.eq(fishPointId))
			.orderBy(fishPointSummary.totalCount.desc())
			.limit(4)
			.fetch();
	}
}
