package com.backend.domain.review.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.ZonedDateTime;
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
import com.backend.global.dto.request.GlobalRequest;
import com.backend.global.dto.response.ScrollResponse;
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
		Long givenMemberId = 1L;
		Long givenReservationId = 1L;
		ReviewRequest.Create givenRequest = fixtureMonkeyValidation.giveMeOne(ReviewRequest.Create.class);

		Review givenReview = ReviewConverter.fromReviewRequestCreate(givenMemberId, givenReservationId, givenRequest);
		ReflectionTestUtils.setField(givenReview, "reviewId", 1L);

		given(reviewRepository.existsByReservationId(givenReservationId)).willReturn(false);
		given(reviewRepository.save(any(Review.class))).willReturn(givenReview);

		//when
		Long savedReviewId = reviewServiceImpl.save(givenMemberId, givenReservationId, givenRequest);

	    //then
		verify(reviewRepository).existsByReservationId(givenReservationId);
		verify(reviewRepository).save(any(Review.class));
		assertThat(savedReviewId).isEqualTo(1L);
	}

	@Test
	@DisplayName("리뷰 저장 [Service] - Fail (중복 리뷰)")
	void t02() {
		// given
		Long givenMemberId = 1L;
		Long givenReservationId = 1L;
		ReviewRequest.Create givenRequest = fixtureMonkeyValidation.giveMeOne(ReviewRequest.Create.class);

		given(reviewRepository.existsByReservationId(givenReservationId)).willReturn(true);

		// when & then
		assertThatThrownBy(() -> reviewServiceImpl.save(givenMemberId, givenReservationId, givenRequest))
			.isInstanceOf(ReviewException.class)
			.hasMessageContaining(ReviewErrorCode.DUPLICATE_REVIEW.getMessage());

		verify(reviewRepository).existsByReservationId(givenReservationId);
		verify(reviewRepository, never()).save(any());
	}

	@Test
	@DisplayName("게시글 ID로 리뷰 목록 오프셋 조회 [Service] - Success")
	void t03() {
		// given
		Long givenMemberId = 1L;
		Long givenPostId = 1L;
		Pageable pageable = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "createdAt"));
		List<ReviewWithMemberResponse> reviewList = fixtureMonkeyValidation.giveMe(ReviewWithMemberResponse.class, 2);
		Page<ReviewWithMemberResponse> givenPage = new PageImpl<>(reviewList, pageable, reviewList.size());

		given(reviewRepository.findReviewsWithMemberByPostId(givenMemberId, givenPostId, pageable)).willReturn(givenPage);

		// when
		Slice<ReviewWithMemberResponse> result = reviewServiceImpl.getReviewListByPostId(givenMemberId, givenPostId, pageable);

		// then
		assertThat(result).hasSize(2);
		verify(reviewRepository).findReviewsWithMemberByPostId(givenMemberId, givenPostId, pageable);
	}

	@Test
	@DisplayName("게시글 ID로 리뷰 목록 오프셋 조회 [Service] - Empty")
	void t04() {
		// given
		Long givenMemberId = 1L;
		Long givenPostId = 999L;
		Pageable pageable = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "createdAt"));
		Page<ReviewWithMemberResponse> emptyPage = Page.empty(pageable);

		given(reviewRepository.findReviewsWithMemberByPostId(givenMemberId, givenPostId, pageable)).willReturn(emptyPage);

		// when
		Slice<ReviewWithMemberResponse> result = reviewServiceImpl.getReviewListByPostId(givenMemberId, givenPostId, pageable);

		// then
		assertThat(result).isEmpty();
		verify(reviewRepository).findReviewsWithMemberByPostId(givenMemberId, givenPostId, pageable);
	}

	@Test
	@DisplayName("회원 ID로 리뷰 목록 조회 [Service] - Success")
	void t05() {
		// given
		Long givenMemberId = 1L;
		Pageable pageable = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "createdAt"));
		List<ReviewWithMemberResponse> reviewList = fixtureMonkeyValidation.giveMe(ReviewWithMemberResponse.class, 3);
		Page<ReviewWithMemberResponse> givenPage = new PageImpl<>(reviewList, pageable, reviewList.size());

		given(reviewRepository.findReviewsWithMemberByMemberId(givenMemberId, pageable)).willReturn(givenPage);

		// when
		Slice<ReviewWithMemberResponse> result = reviewServiceImpl.getReviewListByMemberId(givenMemberId, pageable);

		// then
		assertThat(result).hasSize(3);
		verify(reviewRepository).findReviewsWithMemberByMemberId(givenMemberId, pageable);
	}

	@Test
	@DisplayName("회원 ID로 리뷰 목록 조회 [Service] - Empty")
	void t06() {
		// given
		Long givenMemberId = 999L;
		Pageable pageable = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "createdAt"));
		Page<ReviewWithMemberResponse> emptyPage = Page.empty(pageable);

		given(reviewRepository.findReviewsWithMemberByMemberId(givenMemberId, pageable)).willReturn(emptyPage);

		// when
		Slice<ReviewWithMemberResponse> result = reviewServiceImpl.getReviewListByMemberId(givenMemberId, pageable);

		// then
		assertThat(result).isEmpty();
		verify(reviewRepository).findReviewsWithMemberByMemberId(givenMemberId, pageable);
	}

	@Test
	@DisplayName("게시글 ID로 리뷰 커서 조회 [Service] - Success (작성자가 아닌 경우)")
	void t07() {
		// given
		Long givenPostId = 1L;
		Long givenRequestMemberId = 999L;
		GlobalRequest.CursorRequest cursorRequest = new GlobalRequest.CursorRequest(
			null, null, null, null, null, 3
		);

		ReviewWithMemberResponse givenReview = new ReviewWithMemberResponse(
			101L,
			5,
			"리뷰 내용",
			List.of("img1.jpg", "img2.png"),
			givenPostId,
			null,
			"작성자 닉네임",
			"profile.jpg",
			false,
			ZonedDateTime.now()
		);
		List<ReviewWithMemberResponse> content = List.of(givenReview);

		ScrollResponse<ReviewWithMemberResponse> expected = ScrollResponse.from(
			content,
			cursorRequest.size(),
			content.size(),
			true,
			true
		);

		given(reviewRepository.findReviewsByPostIdWithCursor(givenPostId, givenRequestMemberId, cursorRequest))
			.willReturn(expected);

		// when
		ScrollResponse<ReviewWithMemberResponse> result =
			reviewServiceImpl.getReviewListByPostIdWithCursor(givenPostId, givenRequestMemberId, cursorRequest);

		// then
		assertThat(result.content()).hasSize(1);
		assertThat(result.isFirst()).isTrue();
		assertThat(result.isLast()).isTrue();
		assertThat(result.content().get(0).isAuthor()).isFalse();
		assertThat(result.content().get(0).memberId()).isNull();
		verify(reviewRepository).findReviewsByPostIdWithCursor(givenPostId, givenRequestMemberId, cursorRequest);
	}

	@Test
	@DisplayName("게시글 ID로 리뷰 커서 조회 [Service] - Success (다음 페이지가 있는 경우)")
	void t08() {
		// given
		Long givenPostId = 1L;
		Long givenMemberId = 1L;
		GlobalRequest.CursorRequest cursorRequest = new GlobalRequest.CursorRequest(
			null, null, null, null, null, 3
		);

		List<ReviewWithMemberResponse> fullContent = fixtureMonkeyRecord.giveMeBuilder(ReviewWithMemberResponse.class)
			.set("shipFishingPostId", givenPostId)
			.set("isAuthor", true)
			.set("memberId", givenMemberId)
			.sampleList(4);
		List<ReviewWithMemberResponse> expectedContent = fullContent.subList(0, cursorRequest.size());

		ScrollResponse<ReviewWithMemberResponse> expected = ScrollResponse.from(
			expectedContent,
			cursorRequest.size(),
			expectedContent.size(),
			true,
			false
		);

		given(reviewRepository.findReviewsByPostIdWithCursor(givenPostId, givenMemberId, cursorRequest))
			.willReturn(expected);

		// when
		ScrollResponse<ReviewWithMemberResponse> result =
			reviewServiceImpl.getReviewListByPostIdWithCursor(givenPostId, givenMemberId, cursorRequest);

		// then
		assertThat(result.content()).hasSize(3);
		assertThat(result.isFirst()).isTrue();
		assertThat(result.isLast()).isFalse();
		assertThat(result.content()).allSatisfy(r -> {
			assertThat(r.isAuthor()).isTrue();
			assertThat(r.memberId()).isEqualTo(givenMemberId);
		});
		verify(reviewRepository).findReviewsByPostIdWithCursor(givenPostId, givenMemberId, cursorRequest);
	}

	@Test
	@DisplayName("회원 ID로 리뷰 커서 조회 [Service] - Success")
	void t09() {
		// given
		Long givenMemberId = 1L;
		Long givenPostId = 1L;
		GlobalRequest.CursorRequest cursorRequest = new GlobalRequest.CursorRequest(
			null, null, null, null, null, 3
		);

		List<ReviewWithMemberResponse> content = fixtureMonkeyRecord.giveMeBuilder(ReviewWithMemberResponse.class)
			.set("shipFishingPostId", givenPostId)
			.set("isAuthor", true)
			.set("memberId", givenMemberId)
			.sampleList(2);

		ScrollResponse<ReviewWithMemberResponse> expected = ScrollResponse.from(
			content,
			cursorRequest.size(),
			content.size(),
			true,
			true
		);

		given(reviewRepository.findReviewsByMemberIdWithCursor(givenMemberId, cursorRequest))
			.willReturn(expected);

		// when
		ScrollResponse<ReviewWithMemberResponse> result =
			reviewServiceImpl.getReviewListByMemberIdWithCursor(givenMemberId, cursorRequest);

		// then
		assertThat(result.content()).hasSize(2);
		assertThat(result.isFirst()).isTrue();
		assertThat(result.isLast()).isTrue();
		assertThat(result.content()).allSatisfy(r -> {
			assertThat(r.isAuthor()).isTrue();
			assertThat(r.memberId()).isEqualTo(givenMemberId);
		});
		verify(reviewRepository).findReviewsByMemberIdWithCursor(givenMemberId, cursorRequest);
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