package com.backend.domain.review.repository;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.backend.domain.member.entity.Member;
import com.backend.domain.member.repository.MemberQueryRepository;
import com.backend.domain.member.repository.MemberRepository;
import com.backend.domain.member.repository.MemberRepositoryImpl;
import com.backend.domain.review.dto.response.ReviewWithMemberResponse;
import com.backend.domain.review.entity.Review;
import com.backend.domain.shipfishingpost.entity.ShipFishingPost;
import com.backend.domain.shipfishingpost.repository.ShipFishingPostQueryRepository;
import com.backend.domain.shipfishingpost.repository.ShipFishingPostRepository;
import com.backend.domain.shipfishingpost.repository.ShipFishingPostRepositoryImpl;
import com.backend.global.config.JpaAuditingConfig;
import com.backend.global.config.QuerydslConfig;
import com.backend.global.util.BaseTest;

import com.navercorp.fixturemonkey.ArbitraryBuilder;

@DataJpaTest
@Import({
	JpaAuditingConfig.class,
	ReviewRepositoryImpl.class,
	MemberRepositoryImpl.class,
	MemberQueryRepository.class,
	ShipFishingPostRepositoryImpl.class,
	ShipFishingPostQueryRepository.class,
	QuerydslConfig.class})
class ReviewRepositoryTest extends BaseTest {

	@Autowired
	private ReviewRepository reviewRepository;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private ShipFishingPostRepository shipFishingPostRepository;

	private ArbitraryBuilder<Review> getReviewBuilder() {
		return fixtureMonkeyBuilder
			.giveMeBuilder(Review.class)
			.set("reviewId", null);
	}

	private Member saveTestMember() {
		return memberRepository.save(
			fixtureMonkeyBuilder.giveMeBuilder(Member.class)
				.set("memberId", null)
				.set("nickname", "강태공")
				.set("name", "강태공")
				.set("email", "test@gmail.com")
				.set("phone", "010-1234-1234")
				.sample()
		);
	}

	private ShipFishingPost saveTestPost() {
		return shipFishingPostRepository.save(
			fixtureMonkeyBuilder.giveMeBuilder(ShipFishingPost.class)
				.set("shipFishingPostId", null)
				.set("shipId", 1L)
				.set("subject", "subject")
				.sample()
		);
	}

	private Review createReview(Long reservationId, Long memberId, Long postId) {
		return getReviewBuilder()
			.set("reservationId", reservationId)
			.set("memberId", memberId)
			.set("shipFishingPostId", postId)
			.sample();
	}

	private void saveTestReviews(Member member, ShipFishingPost post) {
		reviewRepository.save(createReview(1L, member.getMemberId(), post.getShipFishingPostId()));
		reviewRepository.save(createReview(2L, member.getMemberId(), post.getShipFishingPostId()));
	}

	@Test
	@DisplayName("선상 낚시 리뷰 저장 [Repository] - Success")
	void t01() {
		// given
		Review givenReview = getReviewBuilder().set("reservationId", 1L).sample();

		// when
		Review savedReview = reviewRepository.save(givenReview);

		// then
		assertThat(savedReview).isNotNull();
		assertThat(savedReview.getReviewId()).isNotNull();
	}

	@Test
	@DisplayName("예약 ID로 리뷰 존재 여부 확인 [Repository] - true 반환")
	void t02() {
		// given
		Long reservationId = 1L;
		reviewRepository.save(getReviewBuilder().set("reservationId", reservationId).sample());

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

	@Test
	@DisplayName("게시글 ID로 리뷰 조회 [Repository] - Success")
	void t04() {
		// given
		Member givenMember = saveTestMember();
		ShipFishingPost givenPost = saveTestPost();
		saveTestReviews(givenMember, givenPost);

		Pageable pageable = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "createdAt"));

		// when
		Page<ReviewWithMemberResponse> result = reviewRepository.findReviewsWithMemberByPostId(
			givenPost.getShipFishingPostId(), pageable
		);

		// then
		assertThat(result).isNotEmpty();
		assertThat(result).hasSize(2);
	}

	@Test
	@DisplayName("작성자 ID로 리뷰 조회 [Repository] - Success")
	void t05() {
		// given
		Member givenMember = saveTestMember();
		ShipFishingPost givenPost = saveTestPost();
		saveTestReviews(givenMember, givenPost);

		Pageable pageable = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "createdAt"));

		// when
		Page<ReviewWithMemberResponse> result = reviewRepository.findReviewsWithMemberByMemberId(
			givenMember.getMemberId(), pageable
		);

		// then
		assertThat(result).isNotEmpty();
		assertThat(result).hasSize(2);
	}
}