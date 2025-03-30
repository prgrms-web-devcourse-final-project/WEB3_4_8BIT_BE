package com.backend.domain.review.repository;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.backend.domain.review.entity.Review;
import com.backend.global.config.JpaAuditingConfig;
import com.backend.global.util.BaseTest;

@DataJpaTest
@Import({JpaAuditingConfig.class, ReviewRepositoryImpl.class})
class ReviewRepositoryTest extends BaseTest {

	@Autowired
	private ReviewRepository reviewRepository;

	@Test
	@DisplayName("선상 낚시 리뷰 저장 [Repository] - Success")
	void t01() {
		// given
		Review givenReview = fixtureMonkeyBuilder.giveMeBuilder(Review.class)
			.set("reviewId", null)
			.set("reservationId", 1L)
			.sample();

		// when
		Review saved = reviewRepository.save(givenReview);

		// then
		assertThat(saved).isNotNull();
		assertThat(saved.getReviewId()).isNotNull();
	}

	@Test
	@DisplayName("예약 ID로 리뷰 존재 여부 확인 [Repository] - true 반환")
	void t02() {
		// given
		Long reservationId = 1L;

		Review givenReview = fixtureMonkeyBuilder.giveMeBuilder(Review.class)
			.set("reviewId", null)
			.set("reservationId", reservationId)
			.sample();

		reviewRepository.save(givenReview);

		// when
		boolean exists = reviewRepository.existsByReservationId(reservationId);

		// then
		assertThat(exists).isTrue();
	}

	@Test
	@DisplayName("예약 ID로 리뷰 존재 여부 확인 [Repository] - false 반환")
	void t03() {
		// given
		Long givenReservationId = 999L;

		// when
		boolean exists = reviewRepository.existsByReservationId(givenReservationId);

		// then
		assertThat(exists).isFalse();
	}
}