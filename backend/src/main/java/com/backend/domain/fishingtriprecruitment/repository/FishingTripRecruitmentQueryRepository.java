package com.backend.domain.fishingtriprecruitment.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import static com.backend.domain.fishingtriprecruitment.entity.QFishingTripRecruitment.*;
import static com.backend.domain.member.entity.QMember.*;

import com.backend.domain.fishingtriprecruitment.domain.RecruitmentStatus;
import com.backend.domain.fishingtriprecruitment.dto.response.FishingTripRecruitmentResponse;
import com.backend.domain.fishingtriprecruitment.dto.response.QFishingTripRecruitmentResponse_DetailPage;
import com.backend.domain.fishingtriprecruitment.dto.response.QFishingTripRecruitmentResponse_DetailPageQueryDto;
import com.backend.global.dto.request.GlobalRequest;
import com.backend.global.dto.response.ScrollResponse;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class FishingTripRecruitmentQueryRepository {

	private final JPAQueryFactory jpaQueryFactory;

	public List<FishingTripRecruitmentResponse.DetailPageQueryDto> findDetailPageQueryDtoByIdAndStatus(
		final Long fishingTripPostId,
		final RecruitmentStatus status
	) {
		return jpaQueryFactory.select(
				new QFishingTripRecruitmentResponse_DetailPageQueryDto(
					fishingTripRecruitment.fishingTripRecruitmentId,
					member.nickname,
					member.fileId,
					fishingTripRecruitment.fishingLevel,
					fishingTripRecruitment.introduction,
					fishingTripRecruitment.recruitmentStatus,
					fishingTripRecruitment.createdAt
				)).from(fishingTripRecruitment)
			.leftJoin(member).on(member.memberId.eq(fishingTripRecruitment.memberId))
			.where(fishingTripRecruitment.fishingTripPostId.eq(fishingTripPostId),
				fishingTripRecruitment.recruitmentStatus.eq(status))
			.orderBy(fishingTripRecruitment.fishingTripRecruitmentId.asc())
			.fetch();
	}
}
