package com.backend.domain.fishingtrippost.repository;

import static org.assertj.core.api.Assertions.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;

import com.backend.domain.fishingtrippost.entity.FishingTripPost;
import com.backend.domain.fishpoint.entity.FishPoint;
import com.backend.domain.fishpoint.repository.FishPointRepository;
import com.backend.domain.fishpoint.repository.FishPointRepositoryImpl;
import com.backend.domain.member.domain.MemberRole;
import com.backend.domain.member.entity.Member;
import com.backend.domain.member.repository.MemberQueryRepository;
import com.backend.domain.member.repository.MemberRepository;
import com.backend.domain.member.repository.MemberRepositoryImpl;
import com.backend.global.config.JpaAuditingConfig;
import com.backend.global.config.QuerydslConfig;
import com.backend.global.util.BaseTest;

import com.navercorp.fixturemonkey.ArbitraryBuilder;

@DataJpaTest
@Import({
	JpaAuditingConfig.class,
	MemberRepositoryImpl.class,
	MemberQueryRepository.class,
	FishPointRepositoryImpl.class,
	FishingTripPostRepositoryImpl.class,
	QuerydslConfig.class,
})
class FishingTripPostRepositoryTest extends BaseTest {

	@Autowired
	private FishingTripPostRepository fishingTripPostRepository;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private FishPointRepository fishPointRepository;

	final ArbitraryBuilder<FishingTripPost> fishingTripPostArbitraryBuilder = fixtureMonkeyBuilder
		.giveMeBuilder(FishingTripPost.class)
		.set("fishingTripPostId", null)
		.set("subject", "테스트 제목")
		.set("content", "테스트 내용")
		.set("recruitmentCount", 5)
		.set("currentCount", 0)
		.set("isShipFish", false)
		.set("fishingDate", ZonedDateTime.of(2025, 6, 10, 8, 0, 0, 0, ZoneId.of("Asia/Seoul")))
		.set("fileIdList", List.of(1L, 2L, 3L));

	final ArbitraryBuilder<Member> memberArbitraryBuilder = fixtureMonkeyBuilder
		.giveMeBuilder(Member.class)
		.set("memberId", null)
		.set("nickname", "강태공")
		.set("name", "테스트")
		.set("email", "test@example.com")
		.set("phone", "010-1111-2222")
		.set("role", MemberRole.USER);

	final Arbitrary<String> englishString = Arbitraries.strings()
		.withCharRange('a', 'z')
		.withCharRange('A', 'Z')
		.ofMinLength(1).ofMaxLength(50);

	final ArbitraryBuilder<FishPoint> fishPointArbitraryBuilder = fixtureMonkeyBuilder
		.giveMeBuilder(FishPoint.class)
		.set("fishPointId", null)
		.set("fishPointName", englishString)
		.set("fishPointDetailName", englishString)
		.set("longitude", 36.4)
		.set("latitude", 128.5);

	@Test
	@DisplayName("동출 게시글 저장 [Repository] - Success")
	void t01() {
		// given
		Member savedMember = memberRepository.save(memberArbitraryBuilder.sample());
		FishPoint savedFishPoint = fishPointRepository.save(fishPointArbitraryBuilder.sample());

		FishingTripPost givenPost = fishingTripPostArbitraryBuilder
			.set("fishingTripPostId", null)
			.set("memberId", savedMember.getMemberId())
			.set("fishPointId", savedFishPoint.getFishPointId())
			.sample();

		// when
		FishingTripPost saved = fishingTripPostRepository.save(givenPost);

		// then
		assertThat(saved).isNotNull();
		assertThat(saved.getFishingTripPostId()).isNotNull();
	}

	@Test
	@DisplayName("동출 게시글 Id로 조회 [Repository] - Success")
	void t02() {
		// given
		Member savedMember = memberRepository.save(memberArbitraryBuilder.sample());
		FishPoint savedFishPoint = fishPointRepository.save(fishPointArbitraryBuilder.sample());

		FishingTripPost givenPost = fishingTripPostArbitraryBuilder
			.set("fishingTripPostId", null)
			.set("memberId", savedMember.getMemberId())
			.set("fishPointId", savedFishPoint.getFishPointId())
			.sample();

		FishingTripPost saved = fishingTripPostRepository.save(givenPost);

		// when
		FishingTripPost result = fishingTripPostRepository.findById(saved.getFishingTripPostId()).orElse(null);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getFishingTripPostId()).isEqualTo(saved.getFishingTripPostId());
		assertThat(result.getSubject()).isEqualTo("테스트 제목");
		assertThat(result.getContent()).isEqualTo("테스트 내용");
	}

}
