package com.backend.domain.catchmaxlength.repository;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.backend.domain.catchmaxlength.entity.CatchMaxLength;
import com.backend.global.config.JpaAuditingConfig;
import com.backend.global.util.BaseTest;

import lombok.extern.slf4j.Slf4j;

import com.navercorp.fixturemonkey.ArbitraryBuilder;

@Import({CatchMaxLengthRepositoryImpl.class, JpaAuditingConfig.class})
@DataJpaTest
@Slf4j
class CatchMaxLengthRepositoryTest extends BaseTest {

	@Autowired
	private CatchMaxLengthRepository catchMaxLengthRepository;

	ArbitraryBuilder<CatchMaxLength> arbitraryBuilder = fixtureMonkeyBuilder
		.giveMeBuilder(CatchMaxLength.class)
		.set("catchMaxLengthId", null);

	@Test
	@DisplayName("물고기 도감 최대 길이 저장 [Repository] - Success")
	void t01() {
		// Given
		CatchMaxLength givenCatchMaxLength = arbitraryBuilder
			.sample();

		// When
		CatchMaxLength saveCatchMaxLength = catchMaxLengthRepository
			.save(givenCatchMaxLength);

		// Then
		assertThat(saveCatchMaxLength.getCatchMaxLengthId()).isNotNull();
	}

	@Test
	@DisplayName("물고기 도감 최대 길이 조회 [Repository] - Success")
	void t02() {
		// Given
		CatchMaxLength givenCatchMaxLength = arbitraryBuilder
			.sample();

		CatchMaxLength savedCatchMaxLength = catchMaxLengthRepository
			.save(givenCatchMaxLength);

		// When
		CatchMaxLength findCatchMaxLength = catchMaxLengthRepository.findByFishIdAndMemberId(
			savedCatchMaxLength.getFishId(),
			savedCatchMaxLength.getMemberId()
		).get();

		// Then
		assertThat(findCatchMaxLength).isEqualTo(savedCatchMaxLength);

	}
}