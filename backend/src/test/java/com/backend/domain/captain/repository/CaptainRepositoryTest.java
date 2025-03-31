package com.backend.domain.captain.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.backend.domain.captain.entity.Captain;
import com.backend.global.config.JpaAuditingConfig;
import com.backend.global.util.BaseTest;

import com.navercorp.fixturemonkey.ArbitraryBuilder;

@DataJpaTest
@Import({JpaAuditingConfig.class, CaptainRepositoryImpl.class})
class CaptainRepositoryTest extends BaseTest {

	@Autowired
	private CaptainRepository captainRepository;

	final ArbitraryBuilder<Captain> arbitraryBuilder = fixtureMonkeyBuilder.giveMeBuilder(Captain.class)
		.set("memberId", 1L)
		.set("shipLicenseNumber", "1-2019123456")
		.set("shipList", List.of(101L, 102L));

	@Test
	@DisplayName("선장 저장 [Repository] - Success")
	void t01() {
		// Given
		Captain givenCaptain = arbitraryBuilder.sample();

		// When
		Captain savedCaptain = captainRepository.save(givenCaptain);

		// Then
		assertThat(savedCaptain.getMemberId()).isNotNull();
	}

}