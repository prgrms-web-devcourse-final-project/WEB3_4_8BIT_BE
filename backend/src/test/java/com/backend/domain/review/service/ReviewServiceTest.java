package com.backend.domain.review.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.backend.domain.review.converter.ReviewConverter;
import com.backend.domain.review.dto.request.ReviewRequest;
import com.backend.domain.review.entity.Review;
import com.backend.domain.review.exception.ReviewErrorCode;
import com.backend.domain.review.exception.ReviewException;

import com.backend.domain.review.repository.ReviewRepository;
import com.backend.global.util.BaseTest;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest extends BaseTest {

	@Mock
	private ReviewRepository reviewRepository;

	@InjectMocks
	private ReviewServiceImpl reviewServiceImpl;

	@Test
	@DisplayName("리뷰 저장 [Service] - Success")
	void t01() {
	    //given
		Long reservationId = 1L;
		ReviewRequest.Create givenRequest = fixtureMonkeyValidation.giveMeOne(ReviewRequest.Create.class);

		Review givenReview = ReviewConverter.fromReviewRequestCreate(reservationId, givenRequest);
		ReflectionTestUtils.setField(givenReview, "reviewId", 1L);

		given(reviewRepository.existsByReservationId(reservationId)).willReturn(false);
		given(reviewRepository.save(any(Review.class))).willReturn(givenReview);

		//when
		Long savedReviewId = reviewServiceImpl.save(reservationId, givenRequest);

	    //then
		verify(reviewRepository).existsByReservationId(reservationId);
		verify(reviewRepository).save(any(Review.class));
		assertThat(savedReviewId).isEqualTo(1L);
	}

	@Test
	@DisplayName("리뷰 저장 [Service] - Fail (중복 리뷰)")
	void t02() {
		// given
		Long reservationId = 1L;
		ReviewRequest.Create givenRequest = fixtureMonkeyValidation.giveMeOne(ReviewRequest.Create.class);

		given(reviewRepository.existsByReservationId(reservationId)).willReturn(true);

		// when & then
		assertThatThrownBy(() -> reviewServiceImpl.save(reservationId, givenRequest))
			.isInstanceOf(ReviewException.class)
			.hasMessageContaining(ReviewErrorCode.DUPLICATE_REVIEW.getMessage());

		verify(reviewRepository).existsByReservationId(reservationId);
		verify(reviewRepository, never()).save(any());
	}
}