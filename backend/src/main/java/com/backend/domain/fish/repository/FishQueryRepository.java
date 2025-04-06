package com.backend.domain.fish.repository;

import static com.backend.domain.fish.entity.QFish.*;
import static com.backend.global.storage.entity.QFile.*;

import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.backend.domain.fish.dto.FishResponse;
import com.backend.domain.fish.dto.QFishResponse_Detail;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@RequiredArgsConstructor
public class FishQueryRepository {

	private final JPAQueryFactory jpaQueryFactory;
	private final JdbcTemplate jdbcTemplate;

	public Optional<FishResponse.Detail> findDetailById(final Long fishId) {
		FishResponse.Detail findDetail = jpaQueryFactory
			.select(new QFishResponse_Detail(fish.fishId, fish.name, fish.description, file.url, fish.spawnSeasonList,
				fish.spawnLocation))
			.from(fish)
			.leftJoin(file)
			.on(fish.fileId.eq(file.fileId))
			.where(fish.fishId.eq(fishId))
			.fetchOne();

		return Optional.ofNullable(findDetail);
	}

	public void updateFishPopularityScores(List<Tuple> hourlyFishCountSummaryList) {

		if (!hourlyFishCountSummaryList.isEmpty()) {
			for (Tuple tuple : hourlyFishCountSummaryList) {
				log.debug("물고기 ID: {}, 카운트 합계: {}",
					tuple.get(0, Long.class),
					tuple.get(1, Number.class).longValue());
			}

			// JDBC batch update 사용
			String sql = "UPDATE fishes SET fishes.popularity_score = ? WHERE fishes.fish_id = ?";
			jdbcTemplate.batchUpdate(sql, hourlyFishCountSummaryList, hourlyFishCountSummaryList.size(),
				(ps, tuple) -> {
					Long fishId = tuple.get(0, Long.class);
					Long count = tuple.get(1, Number.class).longValue();
					ps.setLong(1, count);
					ps.setLong(2, fishId);
					log.debug("물고기 인기도 추가: 물고기 ID = {}, 인기도 = {}", fishId, count);
				});
		} else {
			log.debug("업데이트할 데이터가 없습니다.");
		}
	}
}
