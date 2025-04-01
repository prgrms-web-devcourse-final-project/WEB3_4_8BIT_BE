package com.backend.domain.review.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;

import com.backend.domain.review.converter.ReviewConverter;
import com.backend.domain.review.dto.request.ReviewRequest;
import com.backend.domain.review.dto.response.ReviewWithMemberResponse;
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
		Long memberId = 1L;
		Long reservationId = 1L;
		ReviewRequest.Create givenRequest = fixtureMonkeyValidation.giveMeOne(ReviewRequest.Create.class);

		Review givenReview = ReviewConverter.fromReviewRequestCreate(memberId, reservationId, givenRequest);
		ReflectionTestUtils.setField(givenReview, "reviewId", 1L);

		given(reviewRepository.existsByReservationId(reservationId)).willReturn(false);
		given(reviewRepository.save(any(Review.class))).willReturn(givenReview);

		//when
		Long savedReviewId = reviewServiceImpl.save(memberId, reservationId, givenRequest);

	    //then
		verify(reviewRepository).existsByReservationId(reservationId);
		verify(reviewRepository).save(any(Review.class));
		assertThat(savedReviewId).isEqualTo(1L);
	}

	@Test
	@DisplayName("리뷰 저장 [Service] - Fail (중복 리뷰)")
	void t02() {
		// given
		Long memberId = 1L;
		Long reservationId = 1L;
		ReviewRequest.Create givenRequest = fixtureMonkeyValidation.giveMeOne(ReviewRequest.Create.class);

		given(reviewRepository.existsByReservationId(reservationId)).willReturn(true);

		// when & then
		assertThatThrownBy(() -> reviewServiceImpl.save(memberId, reservationId, givenRequest))
			.isInstanceOf(ReviewException.class)
			.hasMessageContaining(ReviewErrorCode.DUPLICATE_REVIEW.getMessage());

		verify(reviewRepository).existsByReservationId(reservationId);
		verify(reviewRepository, never()).save(any());
	}

	@Test
	@DisplayName("게시글 ID로 리뷰 목록 조회 [Service] - Success")
	void t03() {
		// given
		Long postId = 1L;
		Pageable pageable = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "createdAt"));
		List<ReviewWithMemberResponse> reviewList = fixtureMonkeyValidation.giveMe(ReviewWithMemberResponse.class, 2);
		Page<ReviewWithMemberResponse> givenPage = new PageImpl<>(reviewList, pageable, reviewList.size());

		given(reviewRepository.findReviewsWithMemberByPostId(postId, pageable)).willReturn(givenPage);

		// when
		Slice<ReviewWithMemberResponse> result = reviewServiceImpl.getReviewListByPostId(postId, pageable);

		// then
		assertThat(result).hasSize(2);
		verify(reviewRepository).findReviewsWithMemberByPostId(postId, pageable);
	}

	@Test
	@DisplayName("게시글 ID로 리뷰 목록 조회 [Service] - Empty")
	void t04() {
		// given
		Long postId = 999L;
		Pageable pageable = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "createdAt"));
		Page<ReviewWithMemberResponse> emptyPage = Page.empty(pageable);

		given(reviewRepository.findReviewsWithMemberByPostId(postId, pageable)).willReturn(emptyPage);

		// when
		Slice<ReviewWithMemberResponse> result = reviewServiceImpl.getReviewListByPostId(postId, pageable);

		// then
		assertThat(result).isEmpty();
		verify(reviewRepository).findReviewsWithMemberByPostId(postId, pageable);
	}

	@Test
	@DisplayName("회원 ID로 리뷰 목록 조회 [Service] - Success")
	void t05() {
		// given
		Long memberId = 1L;
		Pageable pageable = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "createdAt"));
		List<ReviewWithMemberResponse> reviewList = fixtureMonkeyValidation.giveMe(ReviewWithMemberResponse.class, 3);
		Page<ReviewWithMemberResponse> givenPage = new PageImpl<>(reviewList, pageable, reviewList.size());

		given(reviewRepository.findReviewsWithMemberByMemberId(memberId, pageable)).willReturn(givenPage);

		// when
		Slice<ReviewWithMemberResponse> result = reviewServiceImpl.getReviewListByMemberId(memberId, pageable);

		// then
		assertThat(result).hasSize(3);
		verify(reviewRepository).findReviewsWithMemberByMemberId(memberId, pageable);
	}

	@Test
	@DisplayName("회원 ID로 리뷰 목록 조회 [Service] - Empty")
	void t06() {
		// given
		Long memberId = 999L;
		Pageable pageable = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "createdAt"));
		Page<ReviewWithMemberResponse> emptyPage = Page.empty(pageable);

		given(reviewRepository.findReviewsWithMemberByMemberId(memberId, pageable)).willReturn(emptyPage);

		// when
		Slice<ReviewWithMemberResponse> result = reviewServiceImpl.getReviewListByMemberId(memberId, pageable);

		// then
		assertThat(result).isEmpty();
		verify(reviewRepository).findReviewsWithMemberByMemberId(memberId, pageable);
	}

	@Test
	@DisplayName("리뷰 삭제 [Service] - Success")
	void t010() {
		// given
		Long givenMemberId = 1L;
		Long givenReviewId = 10L;
		Review givenReview = fixtureMonkeyBuilder
			.giveMeBuilder(Review.class)
			.set("reviewId", givenReviewId)
			.set("memberId", givenMemberId)
			.sample();

		given(reviewRepository.findById(givenReviewId)).willReturn(java.util.Optional.of(givenReview));

		// when
		reviewServiceImpl.delete(givenMemberId, givenReviewId);

		// then
		verify(reviewRepository).findById(givenReviewId);
		verify(reviewRepository).delete(givenReview);
	}

	@Test
	@DisplayName("리뷰 삭제 [Service] - Fail (리뷰가 존재하지 않음)")
	void t11() {
		// given
		Long givenMemberId = 1L;
		Long givenReviewId = 999L;

		given(reviewRepository.findById(givenReviewId)).willReturn(java.util.Optional.empty());

		// when & then
		assertThatThrownBy(() -> reviewServiceImpl.delete(givenMemberId, givenReviewId))
			.isInstanceOf(ReviewException.class)
			.hasMessageContaining(ReviewErrorCode.NOT_FOUND_REVIEW.getMessage());

		verify(reviewRepository).findById(givenReviewId);
		verify(reviewRepository, never()).delete(any());
	}

	@Test
	@DisplayName("리뷰 삭제 [Service] - Fail (작성자가 아님)")
	void t12() {
		// given
		Long givenMemberId = 1L;
		Long givenReviewId = 10L;
		Long otherMemberId = 2L;

		Review givenReview = fixtureMonkeyBuilder
			.giveMeBuilder(Review.class)
			.set("reviewId", givenReviewId)
			.set("memberId", otherMemberId)
			.sample();

		given(reviewRepository.findById(givenReviewId)).willReturn(java.util.Optional.of(givenReview));

		// when & then
		assertThatThrownBy(() -> reviewServiceImpl.delete(givenMemberId, givenReviewId))
			.isInstanceOf(ReviewException.class)
			.hasMessageContaining(ReviewErrorCode.FORBIDDEN_REVIEW_DELETE.getMessage());

		verify(reviewRepository).findById(givenReviewId);
		verify(reviewRepository, never()).delete(any());
	}
}