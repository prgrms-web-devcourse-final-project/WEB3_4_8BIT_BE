package com.backend.domain.activityhistory.repository;

import static org.assertj.core.api.Assertions.*;

import java.time.ZonedDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;

import com.backend.domain.activityhistory.domain.ActivityType;
import com.backend.domain.activityhistory.dto.request.ActivityHistoryRequest;
import com.backend.domain.activityhistory.dto.response.ActivityHistoryResponse;
import com.backend.domain.activityhistory.entity.ActivityHistory;
import com.backend.global.config.JpaAuditingConfig;
import com.backend.global.config.QuerydslConfig;
import com.backend.global.dto.request.GlobalRequest;
import com.backend.global.dto.response.ScrollResponse;
import com.backend.global.util.BaseTest;

import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;

@Import({
	ActivityHistoryQueryRepository.class,
	ActivityHistoryRepositoryImpl.class,
	JpaAuditingConfig.class,
	QuerydslConfig.class
})
@DataJpaTest
@Slf4j
class ActivityHistoryRepositoryTest extends BaseTest {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private ActivityHistoryRepository activityHistoryRepository;

	@Autowired
	private ActivityHistoryJpaRepository activityHistoryJpaRepository;

	@Autowired
	private EntityManager em;

	private final Arbitrary<String> englishStringLength = Arbitraries.strings()
		.withCharRange('a', 'z')
		.withCharRange('A', 'Z')
		.ofMinLength(1).ofMaxLength(10);

	@Test
	@DisplayName("활동 내역 저장 [Repository] - Success")
	void t01() {
		// Given
		ActivityHistory givenActivityHistory = fixtureMonkeyBuilder.giveMeBuilder(ActivityHistory.class)
			.set("activityHistoryId", null)
			.set("description", englishStringLength)
			.sample();

		// When
		ActivityHistory savedActivityHistory = activityHistoryRepository.save(givenActivityHistory);

		// Then
		assertThat(savedActivityHistory.getActivityHistoryId()).isNotNull();
	}

	@Test
	@DisplayName("활동 내역 전체 조회 [Repository] - Success")
	void t02() {
		// Given
		List<ActivityHistory> givenActivityHistory = fixtureMonkeyBuilder.giveMeBuilder(ActivityHistory.class)
			.set("activityHistoryId", null)
			.set("description", englishStringLength)
			.set("memberId", 1L)
			.sampleList(5);

		GlobalRequest.CursorRequest givenCursorRequestDto = new GlobalRequest.CursorRequest(
			null,
			null,
			null,
			null,
			null,
			10
		);

		ActivityHistoryRequest.Search givenRequestDto = new ActivityHistoryRequest.Search(null);

		List<ActivityHistory> savedActivityHistory = activityHistoryJpaRepository.saveAll(givenActivityHistory);

		// When
		ScrollResponse<ActivityHistoryResponse.Detail> findDetail = activityHistoryRepository.findDetail(
			givenCursorRequestDto,
			givenRequestDto,
			1L
		);

		// Then
		List<ActivityHistoryResponse.Detail> content = findDetail.content();
		assertThat(content).isNotEmpty();
		assertThat(content).hasSize(5);
	}

	@Test
	@DisplayName("활동 내역 전체 조회 [RESERVATION] [Repository] - Success")
	void t03() {
		// Given
		List<ActivityHistory> givenActivityHistory = fixtureMonkeyBuilder.giveMeBuilder(ActivityHistory.class)
			.set("activityHistoryId", null)
			.set("description", englishStringLength)
			.set("activityType", ActivityType.FISH_ENCYCLOPEDIA)
			.set("memberId", 1L)
			.sampleList(5);

		givenActivityHistory.add(
			fixtureMonkeyBuilder
				.giveMeBuilder(ActivityHistory.class)
				.set("activityHistoryId", null)
				.set("description", englishStringLength)
				.set("activityType", ActivityType.RESERVATION)
				.set("memberId", 3L)
				.sample()
		);

		GlobalRequest.CursorRequest givenCursorRequestDto = new GlobalRequest.CursorRequest(
			null,
			null,
			null,
			null,
			null,
			10
		);

		ActivityHistoryRequest.Search givenRequestDto = new ActivityHistoryRequest.Search(ActivityType.RESERVATION);

		activityHistoryJpaRepository.saveAll(givenActivityHistory);

		// When
		ScrollResponse<ActivityHistoryResponse.Detail> findDetail = activityHistoryRepository.findDetail(
			givenCursorRequestDto,
			givenRequestDto,
			3L
		);

		// Then
		List<ActivityHistoryResponse.Detail> content = findDetail.content();

		log.info(content.get(0).toString());

		assertThat(content).isNotEmpty();
		assertThat(content).hasSize(1);
		assertThat(content.get(0).activityType()).isEqualTo(ActivityType.RESERVATION);
	}

	@Test
	@DisplayName("한달이 지난 데이터 ID 조회 [시간대 조회 검증] [Repository] - Success")
	void t10() {
		// Given
		activityHistoryJpaRepository.deleteAll();

		em.flush();
		em.clear();

		List<ActivityHistory> givenActivityHistory = fixtureMonkeyBuilder.giveMeBuilder(ActivityHistory.class)
			.set("activityHistoryId", null)
			.set("description", englishStringLength)
			.set("activityType", ActivityType.FISH_ENCYCLOPEDIA)
			.set("memberId", 1L)
			.sampleList(5);

		List<ActivityHistory> activityHistories = activityHistoryJpaRepository.saveAll(givenActivityHistory);

		em.flush();
		em.clear();

		// 저장한 물고기 도감 데이터 전부 2시간 전으로 생성일 수정
		jdbcTemplate.update(
			"UPDATE activity_histories " +
				"SET created_at = ? " +
				"WHERE activity_history_id = ?",
			ZonedDateTime.now().minusMonths(2),
			activityHistories.get(0).getActivityHistoryId());

		// When
		List<Long> activityHistoryIdsBeforeOneMonthList = activityHistoryRepository
			.findActivityHistoryIdsBeforeOneMonth();

		// Then
		assertThat(activityHistoryIdsBeforeOneMonthList).hasSize(1);
	}

	@Test
	@DisplayName("여러개의 ID 값으로 데이터 삭제 [Repository] - Success")
	void t11() {
		// Given
		activityHistoryJpaRepository.deleteAll();

		List<ActivityHistory> givenActivityHistory = fixtureMonkeyBuilder.giveMeBuilder(ActivityHistory.class)
			.set("activityHistoryId", null)
			.set("description", englishStringLength)
			.set("activityType", ActivityType.FISH_ENCYCLOPEDIA)
			.set("memberId", 1L)
			.sampleList(5);

		List<ActivityHistory> savedActivityHistoryList = activityHistoryJpaRepository.saveAll(givenActivityHistory);

		List<Long> givenActivityHistoryIdList = savedActivityHistoryList.stream()
			.map(ActivityHistory::getActivityHistoryId)
			.toList();

		// When
		activityHistoryRepository.deleteByIdList(givenActivityHistoryIdList);

		// Then
		List<ActivityHistory> findAll = activityHistoryJpaRepository.findAll();

		assertThat(findAll).isEmpty();
	}
}