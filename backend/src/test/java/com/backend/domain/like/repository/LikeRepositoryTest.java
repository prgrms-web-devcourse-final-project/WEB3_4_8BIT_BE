package com.backend.domain.like.repository;

import static org.assertj.core.api.Assertions.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.backend.domain.fishingtrippost.entity.FishingTripPost;
import com.backend.domain.fishingtrippost.repository.FishingTripPostQueryRepository;
import com.backend.domain.fishingtrippost.repository.FishingTripPostRepository;
import com.backend.domain.fishingtrippost.repository.FishingTripPostRepositoryImpl;
import com.backend.domain.fishpoint.entity.FishPoint;
import com.backend.domain.fishpoint.repository.FishPointQueryRepository;
import com.backend.domain.fishpoint.repository.FishPointRepository;
import com.backend.domain.fishpoint.repository.FishPointRepositoryImpl;
import com.backend.domain.like.domain.LikeTargetType;
import com.backend.domain.like.entity.Like;
import com.backend.domain.member.domain.MemberRole;
import com.backend.domain.member.domain.Provider;
import com.backend.domain.member.entity.Member;
import com.backend.domain.member.repository.MemberQueryRepository;
import com.backend.domain.member.repository.MemberRepository;
import com.backend.domain.member.repository.MemberRepositoryImpl;
import com.backend.global.config.JpaAuditingConfig;
import com.backend.global.config.QuerydslConfig;
import com.backend.global.util.BaseTest;

import jakarta.persistence.EntityManager;

import com.navercorp.fixturemonkey.ArbitraryBuilder;

@DataJpaTest
@Import({
	JpaAuditingConfig.class,
	MemberRepositoryImpl.class,
	MemberQueryRepository.class,
	FishPointRepositoryImpl.class,
	FishPointQueryRepository.class,
	FishingTripPostRepositoryImpl.class,
	FishingTripPostQueryRepository.class,
	LikeRepositoryImpl.class,
	LikeQueryRepository.class,
	QuerydslConfig.class,
})
class LikeRepositoryTest extends BaseTest {

	@Autowired
	private LikeRepository likeRepository;

	@Autowired
	private FishingTripPostRepository fishingTripPostRepository;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private FishPointRepository fishPointRepository;

	@Autowired
	private EntityManager em;

	final ArbitraryBuilder<Like> likeArbitraryBuilder = fixtureMonkeyBuilder
		.giveMeBuilder(Like.class)
		.set("likeId", null)
		.set("isDeleted", false);

	final ArbitraryBuilder<FishingTripPost> fishingTripPostArbitraryBuilder = fixtureMonkeyBuilder
		.giveMeBuilder(FishingTripPost.class)
		.set("fishingTripPostId", null)
		.set("subject", "테스트 제목")
		.set("content", "테스트 내용")
		.set("recruitmentCount", 5)
		.set("currentCount", 0)
		.set("isShipFish", false)
		.set("fishingDate", ZonedDateTime.of(2025, 4, 11, 8, 0, 0, 0, ZoneId.of("Asia/Seoul")));

	final ArbitraryBuilder<Member> memberArbitraryBuilder = fixtureMonkeyBuilder
		.giveMeBuilder(Member.class)
		.set("memberId", null)
		.set("nickname", "닉네임_" + UUID.randomUUID().toString().substring(0, 6))
		.set("nickname", "테스트")
		.set("email", UUID.randomUUID() + "@example.com")
		.set("phone", "010-" + UUID.randomUUID().toString().substring(0, 8))
		.set("role", MemberRole.USER)
		.set("provider", Provider.KAKAO)
		.set("providerId", UUID.randomUUID().toString())
		.set("isAddInfo", false);

	@Test
	@DisplayName("좋아요 저장 [Repository] - Success")
	void t01() {
		// given
		Member savedMember = memberRepository.save(memberArbitraryBuilder.sample());
		FishPoint savedFishPoint = fishPointRepository.save(createRandomFishPoint());

		FishingTripPost savedPost = fishingTripPostRepository.save(
			fishingTripPostArbitraryBuilder
				.set("fishingTripPostId", null)
				.set("memberId", savedMember.getMemberId())
				.set("fishPointId", savedFishPoint.getFishPointId())
				.sample()
		);

		Like givenLike = likeArbitraryBuilder
			.set("likeId", null)
			.set("memberId", savedMember.getMemberId())
			.set("targetType", LikeTargetType.FISHING_TRIP_POST)
			.set("targetId", savedPost.getFishingTripPostId())
			.sample();

		// when
		Like saved = likeRepository.save(givenLike);

		// then
		assertThat(saved).isNotNull();
		assertThat(saved.getLikeId()).isNotNull();
	}

	@Test
	@DisplayName("특정 게시글의 좋아요 수 조회 [Repository] - Success")
	void t02() {
		// given

		int result = 5;
		Member member = memberRepository.save(memberArbitraryBuilder.sample());
		FishPoint point = fishPointRepository.save(createRandomFishPoint());

		FishingTripPost post = fishingTripPostRepository.save(
			fishingTripPostArbitraryBuilder
				.set("memberId", member.getMemberId())
				.set("fishPointId", point.getFishPointId())
				.sample()
		);

		for (int i = 0; i < result; i++) {
			Member liker = createUniqueMember();
			likeRepository.save(
				likeArbitraryBuilder
					.set("memberId", liker.getMemberId())
					.set("targetType", LikeTargetType.FISHING_TRIP_POST)
					.set("targetId", post.getFishingTripPostId())
					.sample()
			);
		}
		// when
		Long count = likeRepository.countByTargetTypeAndTargetId(LikeTargetType.FISHING_TRIP_POST,
			post.getFishingTripPostId());

		// then
		assertThat(count).isEqualTo(5L);
	}

	@Test
	@DisplayName("좋아요 존재 여부 확인 [Repository] - Success")
	void t03() {
		// given
		Member member = memberRepository.save(memberArbitraryBuilder.sample());
		FishPoint point = fishPointRepository.save(createRandomFishPoint());
		FishingTripPost post = fishingTripPostRepository.save(
			fishingTripPostArbitraryBuilder
				.set("memberId", member.getMemberId())
				.set("fishPointId", point.getFishPointId())
				.sample()
		);

		likeRepository.save(likeArbitraryBuilder
			.set("memberId", member.getMemberId())
			.set("targetType", LikeTargetType.FISHING_TRIP_POST)
			.set("targetId", post.getFishingTripPostId())
			.sample());

		// when
		boolean exists = likeRepository.existsByMemberIdAndTargetTypeAndTargetIdAndIsDeletedFalse(
			member.getMemberId(), LikeTargetType.FISHING_TRIP_POST, post.getFishingTripPostId());

		// then
		assertThat(exists).isTrue();
	}

	@Test
	@DisplayName("좋아요 단건 조회 [Repository] - Success")
	void t04() {
		// given
		Member member = memberRepository.save(memberArbitraryBuilder.sample());
		FishPoint point = fishPointRepository.save(createRandomFishPoint());
		FishingTripPost post = fishingTripPostRepository.save(
			fishingTripPostArbitraryBuilder.set("memberId", member.getMemberId())
				.set("fishPointId", point.getFishPointId())
				.sample()
		);

		Like saved = likeRepository.save(likeArbitraryBuilder
			.set("memberId", member.getMemberId())
			.set("targetType", LikeTargetType.FISHING_TRIP_POST)
			.set("targetId", post.getFishingTripPostId())
			.sample());

		// when
		Optional<Like> result = likeRepository.findByMemberIdAndTargetTypeAndTargetId(
			member.getMemberId(), LikeTargetType.FISHING_TRIP_POST, post.getFishingTripPostId());

		// then
		assertThat(result).isPresent();
		assertThat(result.get().getTargetId()).isEqualTo(post.getFishingTripPostId());
		assertThat(result.get().getTargetType()).isEqualTo(LikeTargetType.FISHING_TRIP_POST);
		assertThat(result.get().getLikeId()).isEqualTo(saved.getLikeId());
	}

	@Test
	@DisplayName("좋아요 소프트 삭제 처리 [Repository] - Success")
	void t05() {
		// given
		Member member = memberRepository.save(memberArbitraryBuilder.sample());
		FishPoint point = fishPointRepository.save(createRandomFishPoint());

		FishingTripPost post = fishingTripPostRepository.save(
			fishingTripPostArbitraryBuilder
				.set("memberId", member.getMemberId())
				.set("fishPointId", point.getFishPointId())
				.sample()
		);

		likeRepository.save(
			likeArbitraryBuilder
				.set("memberId", member.getMemberId())
				.set("targetType", LikeTargetType.FISHING_TRIP_POST)
				.set("targetId", post.getFishingTripPostId())
				.sample());

		// when
		likeRepository.deleteByMemberIdAndTargetTypeAndTargetId(
			member.getMemberId(),
			LikeTargetType.FISHING_TRIP_POST,
			post.getFishingTripPostId()
		);

		em.flush();
		em.clear();

		// then
		Like deleted = likeRepository.findByMemberIdAndTargetTypeAndTargetId(
			member.getMemberId(),
			LikeTargetType.FISHING_TRIP_POST,
			post.getFishingTripPostId()
		).get();

		assertThat(deleted.getLikeId()).isNotNull();
		assertThat(deleted.getMemberId()).isEqualTo(member.getMemberId());
		assertThat(deleted.getTargetType()).isEqualTo(LikeTargetType.FISHING_TRIP_POST);
		assertThat(deleted.getIsDeleted()).isTrue();
	}

	@Test
	@DisplayName("삭제된 좋아요 복구 처리 [Repository] - Success")
	void t06() {
		// given
		Member member = memberRepository.save(memberArbitraryBuilder.sample());
		FishPoint point = fishPointRepository.save(createRandomFishPoint());
		FishingTripPost post = fishingTripPostRepository.save(
			fishingTripPostArbitraryBuilder
				.set("memberId", member.getMemberId())
				.set("fishPointId", point.getFishPointId())
				.sample()
		);

		Like like = likeRepository.save(
			likeArbitraryBuilder
				.set("memberId", member.getMemberId())
				.set("targetType", LikeTargetType.FISHING_TRIP_POST)
				.set("targetId", post.getFishingTripPostId())
				.sample());

		likeRepository.deleteByMemberIdAndTargetTypeAndTargetId(
			member.getMemberId(), LikeTargetType.FISHING_TRIP_POST, post.getFishingTripPostId());

		likeRepository.restoreByMemberIdAndTargetTypeAndTargetId(
			member.getMemberId(), LikeTargetType.FISHING_TRIP_POST, post.getFishingTripPostId());

		// when
		Like restored = likeRepository.save(like);

		// then
		assertThat(restored.getLikeId()).isNotNull();
		assertThat(restored.getMemberId()).isEqualTo(member.getMemberId());
		assertThat(restored.getTargetType()).isEqualTo(LikeTargetType.FISHING_TRIP_POST);
		assertThat(restored.getIsDeleted()).isFalse();
	}

	@Test
	@DisplayName("소프트 삭제된 좋아요 영구 삭제 처리 [Repository] - Success")
	void t07() {
		// given
		Member member = memberRepository.save(memberArbitraryBuilder.sample());
		FishPoint point = fishPointRepository.save(createRandomFishPoint());

		FishingTripPost post = fishingTripPostRepository.save(
			fishingTripPostArbitraryBuilder
				.set("memberId", member.getMemberId())
				.set("fishPointId", point.getFishPointId())
				.sample()
		);

		likeRepository.save(
			likeArbitraryBuilder
				.set("memberId", member.getMemberId())
				.set("targetType", LikeTargetType.FISHING_TRIP_POST)
				.set("targetId", post.getFishingTripPostId())
				.set("isDeleted", true)
				.sample());

		// when
		int deletedCount = likeRepository.deleteAllSoftDeletedLikes();

		// then
		assertThat(deletedCount).isGreaterThan(0);
		assertThat(deletedCount).isEqualTo(1);
	}

	private Member createUniqueMember() {
		return memberRepository.save(
			fixtureMonkeyBuilder.giveMeBuilder(Member.class)
				.set("memberId", null)
				.set("nickname", "닉네임_" + UUID.randomUUID().toString().substring(0, 6))
				.set("nickname", "테스트")
				.set("email", UUID.randomUUID() + "@example.com")
				.set("phone", "010-" + UUID.randomUUID().toString().substring(0, 8))
				.set("role", MemberRole.USER)
				.set("provider", Provider.KAKAO)
				.set("providerId", UUID.randomUUID().toString())
				.set("isAddInfo", false)
				.sample()
		);
	}
}