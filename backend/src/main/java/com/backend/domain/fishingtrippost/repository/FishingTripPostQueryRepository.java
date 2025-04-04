package com.backend.domain.fishingtrippost.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.backend.domain.fishingtrippost.dto.response.FishingTripPostResponse;

import static com.backend.domain.fishingtrippost.entity.QFishingTripPost.*;
import static com.backend.domain.member.entity.QMember.*;
import static com.backend.domain.fishpoint.entity.QFishPoint.*;

import com.backend.global.storage.entity.File;
import com.backend.global.storage.repository.StorageRepository;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class FishingTripPostQueryRepository {

	private final JPAQueryFactory jpaQueryFactory;
	private final StorageRepository storageRepository;

	public Optional<FishingTripPostResponse.Detail> findDetailById(final Long fishingTripPostId) {

		Tuple tuple = jpaQueryFactory
			.select(
				fishingTripPost.fishingTripPostId,
				member.name,
				fishingTripPost.subject,
				fishingTripPost.content,
				fishingTripPost.currentCount,
				fishingTripPost.recruitmentCount,
				fishingTripPost.createdAt,
				fishingTripPost.fishingDate,
				fishPoint.fishPointDetailName,
				fishPoint.fishPointName,
				fishPoint.longitude,
				fishPoint.latitude,
				fishingTripPost.fileIdList
			)
			.from(fishingTripPost)
			.leftJoin(member).on(member.memberId.eq(fishingTripPost.memberId))
			.leftJoin(fishPoint).on(fishPoint.fishPointId.eq(fishingTripPost.fishingPointId))
			.where(fishingTripPost.fishingTripPostId.eq(fishingTripPostId))
			.fetchOne();

		List<Long> fileIdList = tuple.get(fishingTripPost.fileIdList);
		List<String> fileUrlList = storageRepository.findAllById(fileIdList).stream()
			.map(File::getUrl)
			.toList();

		return Optional.of(FishingTripPostResponse.Detail.builder()
			.fishingTripPostId(tuple.get(fishingTripPost.fishingTripPostId))
			.name(tuple.get(member.name))
			.subject(tuple.get(fishingTripPost.subject))
			.content(tuple.get(fishingTripPost.content))
			.currentCount(tuple.get(fishingTripPost.currentCount))
			.recruitmentCount(tuple.get(fishingTripPost.recruitmentCount))
			.createDate(tuple.get(fishingTripPost.createdAt))
			.fishingDate(tuple.get(fishingTripPost.fishingDate))
			.fishPointDetailName(tuple.get(fishPoint.fishPointDetailName))
			.fishPointName(tuple.get(fishPoint.fishPointName))
			.longitude(tuple.get(fishPoint.longitude))
			.latitude(tuple.get(fishPoint.latitude))
			.fileUrlList(fileUrlList)
			.build());
	}
}
