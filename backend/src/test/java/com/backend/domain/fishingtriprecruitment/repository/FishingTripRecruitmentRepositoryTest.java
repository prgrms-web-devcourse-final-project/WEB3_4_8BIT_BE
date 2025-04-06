package com.backend.domain.fishingtriprecruitment.repository;

import static org.assertj.core.api.Assertions.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.backend.domain.fishingtrippost.entity.FishingTripPost;
import com.backend.domain.fishingtrippost.repository.FishingTripPostQueryRepository;
import com.backend.domain.fishingtrippost.repository.FishingTripPostRepository;
import com.backend.domain.fishingtrippost.repository.FishingTripPostRepositoryImpl;
import com.backend.domain.fishingtriprecruitment.domain.FishingLevel;
import com.backend.domain.fishingtriprecruitment.domain.RecruitmentStatus;
import com.backend.domain.fishingtriprecruitment.entity.FishingTripRecruitment;
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
	FishingTripPostRepositoryImpl.class,
	FishingTripPostQueryRepository.class,
	FishingTripRecruitmentRepositoryImpl.class,
	FishingTripRecruitmentQueryRepository.class,
	QuerydslConfig.class
})
class FishingTripRecruitmentRepositoryTest extends BaseTest {

	@Autowired
	private FishingTripRecruitmentRepository fishingTripRecruitmentRepository;

	@Autowired
	private FishingTripPostRepository fishingTripPostRepository;

	@Autowired
	private MemberRepository memberRepository;

	final ArbitraryBuilder<FishingTripRecruitment> fishingTripRecruitmentArbitraryBuilder = fixtureMonkeyBuilder
		.giveMeBuilder(FishingTripRecruitment.class)
		.set("fishingTripRecruitmentId", null)
		.set("introduction", "왕초보인데 잘부탁 드립니다!")
		.set("fishingLevel", FishingLevel.ADVANCED)
		.set("recruitmentStatus", RecruitmentStatus.PENDING);

	final ArbitraryBuilder<FishingTripPost> fishingTripPostArbitraryBuilder = fixtureMonkeyBuilder
		.giveMeBuilder(FishingTripPost.class)
		.set("fishingTripPostId", null)
		.set("subject", "테스트 제목")
		.set("content", "테스트 내용")
		.set("recruitmentCount", 5)
		.set("currentCount", 0)
		.set("isShipFish", false)
		.set("fishingDate", ZonedDateTime.of(2025, 6, 10, 8, 0, 0, 0, ZoneId.of("Asia/Seoul")))
		.set("fishPointId", 1L)
		.set("fileIdList", List.of(1L, 2L, 3L));

	final ArbitraryBuilder<Member> memberArbitraryBuilder = fixtureMonkeyBuilder
		.giveMeBuilder(Member.class)
		.set("memberId", null)
		.set("nickname", "강태공")
		.set("name", "테스트")
		.set("email", "test@example.com")
		.set("phone", "010-1111-2222")
		.set("role", MemberRole.USER);

	@Test
	@DisplayName("동출 모집 신청 저장 [Repository] - Success")
	void t01() throws Exception {
		// given
		FishingTripPost savedFishingTripPost = fishingTripPostRepository.save(fishingTripPostArbitraryBuilder.sample());
		Member savedMember = memberRepository.save(memberArbitraryBuilder.sample());

		FishingTripRecruitment fishingTripRecruitment = fishingTripRecruitmentArbitraryBuilder
			.set("fishingTripRecruitmentId", null)
			.set("memberId", savedMember.getMemberId())
			.set("fishingTripPostId", savedFishingTripPost.getFishingTripPostId())
			.sample();

		// when
		FishingTripRecruitment saved = fishingTripRecruitmentRepository.save(fishingTripRecruitment);

		// then
		assertThat(saved).isNotNull();
		assertThat(saved.getFishingTripPostId()).isNotNull();
	}
}