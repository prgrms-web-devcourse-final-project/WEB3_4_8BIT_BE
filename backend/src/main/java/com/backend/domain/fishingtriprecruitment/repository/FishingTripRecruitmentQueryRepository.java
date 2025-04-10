package com.backend.domain.fishingtriprecruitment.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import static com.backend.domain.fishingtriprecruitment.entity.QFishingTripRecruitment.*;
import static com.backend.domain.member.entity.QMember.*;
import static com.backend.global.storage.entity.QFile.*;

import com.backend.domain.fishingtriprecruitment.domain.RecruitmentStatus;
import com.backend.domain.fishingtriprecruitment.dto.response.FishingTripRecruitmentResponse;
import com.backend.domain.fishingtriprecruitment.dto.response.QFishingTripRecruitmentResponse_DetailPage;
import com.backend.global.dto.request.GlobalRequest;
import com.backend.global.dto.response.ScrollResponse;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class FishingTripRecruitmentQueryRepository {

	private final JPAQueryFactory jpaQueryFactory;

	public ScrollResponse<FishingTripRecruitmentResponse.DetailPage> findDetailPageQueryDtoByIdAndStatus(
		final GlobalRequest.CursorRequest cursorRequestDto,
		final Long fishingTripPostId,
		final RecruitmentStatus status
	) {
		List<FishingTripRecruitmentResponse.DetailPage> detailPageList = jpaQueryFactory.select(
				new QFishingTripRecruitmentResponse_DetailPage(
					fishingTripRecruitment.fishingTripRecruitmentId,
					member.nickname,
					file.url,
					fishingTripRecruitment.fishingLevel,
					fishingTripRecruitment.introduction,
					fishingTripRecruitment.recruitmentStatus,
					fishingTripRecruitment.createdAt
				)).from(fishingTripRecruitment)
			.leftJoin(member).on(member.memberId.eq(fishingTripRecruitment.memberId))
			.leftJoin(file).on(file.fileId.eq(member.fileId))
			.where(fishingTripRecruitment.fishingTripPostId.eq(fishingTripPostId),
				fishingTripRecruitment.recruitmentStatus.eq(status),
				cursorIdCondition(cursorRequestDto.id()))
			.orderBy(fishingTripRecruitment.fishingTripRecruitmentId.asc())
			.limit(cursorRequestDto.size() + 1)
			.fetch();

		boolean isLast = detailPageList.size() <= cursorRequestDto.size();

		if (!isLast) {
			detailPageList.remove(detailPageList.size() - 1);
		}

		return ScrollResponse.from(
			detailPageList,
			cursorRequestDto.size(),
			detailPageList.size(),
			cursorRequestDto.id() == null,
			isLast
		);
	}

	private BooleanExpression cursorIdCondition(final Long cursorId) {
		if (cursorId == null)
			return null;
		return fishingTripRecruitment.fishingTripRecruitmentId.gt(cursorId);
	}

	public List<Long> findMemberIdListByPostId(final Long fishingTripPostId) {
		return jpaQueryFactory
			.select(fishingTripRecruitment.memberId)
			.from(fishingTripRecruitment)
			.where(fishingTripRecruitment.fishingTripPostId.eq(fishingTripPostId),
				fishingTripRecruitment.recruitmentStatus.eq(RecruitmentStatus.APPROVED)
			)
			.fetch();
	}

	public void deleteAllByPostId(final Long fishingTripPostId) {
		jpaQueryFactory.delete(fishingTripRecruitment)
			.where(fishingTripRecruitment.fishingTripPostId.eq(fishingTripPostId))
			.execute();
	}
}
