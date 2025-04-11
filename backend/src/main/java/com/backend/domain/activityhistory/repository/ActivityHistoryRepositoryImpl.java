package com.backend.domain.activityhistory.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.backend.domain.activityhistory.dto.request.ActivityHistoryRequest;
import com.backend.domain.activityhistory.dto.response.ActivityHistoryResponse;
import com.backend.domain.activityhistory.entity.ActivityHistory;
import com.backend.global.dto.request.GlobalRequest;
import com.backend.global.dto.response.ScrollResponse;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ActivityHistoryRepositoryImpl implements ActivityHistoryRepository {

	private final ActivityHistoryJpaRepository activityHistoryJpaRepository;
	private final ActivityHistoryQueryRepository activityHistoryQueryRepository;

	@Override
	public ActivityHistory save(final ActivityHistory activityHistory) {
		return activityHistoryJpaRepository.save(activityHistory);
	}

	@Override
	public ScrollResponse<ActivityHistoryResponse.Detail> findDetail(
		final GlobalRequest.CursorRequest cursorRequestDto,
		final ActivityHistoryRequest.Search requestDto,
		final Long memberId
	) {
		return activityHistoryQueryRepository.findDetail(cursorRequestDto, requestDto, memberId);
	}

	@Override
	public List<Long> findActivityHistoryIdsBeforeOneMonth() {
		return activityHistoryQueryRepository.findActivityHistoryIdsBeforeOneMonth();
	}

	@Override
	public long deleteByIdList(final List<Long> activityHistoryidList) {
		return activityHistoryQueryRepository.deleteByIdList(activityHistoryidList);
	}
}
