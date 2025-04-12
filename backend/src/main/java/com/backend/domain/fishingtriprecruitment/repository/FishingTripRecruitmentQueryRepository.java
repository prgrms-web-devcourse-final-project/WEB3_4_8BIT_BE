package com.backend.domain.fishingtriprecruitment.repository;

import static com.backend.domain.fishingtrippost.entity.QFishingTripPost.*;
import static com.backend.domain.fishingtriprecruitment.entity.QFishingTripRecruitment.*;
import static com.backend.domain.member.entity.QMember.*;
import static com.backend.global.storage.entity.QFile.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.backend.domain.fishingtriprecruitment.domain.RecruitmentStatus;
import com.backend.domain.fishingtriprecruitment.dto.response.FishingTripRecruitmentResponse;
import com.backend.domain.fishingtriprecruitment.dto.response.QFishingTripRecruitmentResponse_DetailPage;
import com.backend.global.dto.request.GlobalRequest;
import com.backend.global.dto.response.ScrollResponse;
import com.querydsl.core.Tuple;
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

		List<FishingTripRecruitmentResponse.DetailPage> pageList = new ArrayList<>(detailPageList);

		boolean isLast = pageList.size() <= cursorRequestDto.size();

		if (!isLast) {
			pageList.remove(pageList.size() - 1);
		}

		return ScrollResponse.from(
			pageList,
			cursorRequestDto.size(),
			pageList.size(),
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

	public Map<Long, Integer> findApprovedFishingTripPostIdsWithCount(final Long memberId) {
		List<Tuple> tupleList = jpaQueryFactory
			.select(
				fishingTripPost.fishingTripPostId,
				fishingTripPost.currentCount
			)
			.from(fishingTripRecruitment)
			.join(fishingTripPost)
			.on(fishingTripRecruitment.fishingTripPostId.eq(fishingTripPost.fishingTripPostId))
			.where(
				fishingTripRecruitment.memberId.eq(memberId),
				fishingTripRecruitment.recruitmentStatus.eq(RecruitmentStatus.APPROVED)
			)
			.groupBy(fishingTripPost.fishingTripPostId, fishingTripPost.currentCount)
			.fetch();

		return tupleList.stream().collect(Collectors.toMap(
			t -> t.get(fishingTripPost.fishingTripPostId),
			t -> t.get(fishingTripPost.currentCount)
		));
	}
}
