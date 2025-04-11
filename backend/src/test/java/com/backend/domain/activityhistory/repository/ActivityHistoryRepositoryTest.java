package com.backend.domain.activityhistory.repository;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;

import com.backend.domain.activityhistory.entity.ActivityHistory;
import com.backend.global.config.JpaAuditingConfig;
import com.backend.global.util.BaseTest;

import lombok.extern.slf4j.Slf4j;

@Import({ActivityHistoryRepositoryImpl.class, JpaAuditingConfig.class})
@DataJpaTest
@Slf4j
class ActivityHistoryRepositoryTest extends BaseTest {

	@Autowired
	private ActivityHistoryRepository activityHistoryRepository;

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
}