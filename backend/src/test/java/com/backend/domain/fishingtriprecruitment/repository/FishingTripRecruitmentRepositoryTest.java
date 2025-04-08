package com.backend.domain.fishingtriprecruitment.repository;

import static org.assertj.core.api.Assertions.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.LongStream;

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
import com.backend.domain.fishingtriprecruitment.dto.response.FishingTripRecruitmentResponse;
import com.backend.domain.fishingtriprecruitment.entity.FishingTripRecruitment;
import com.backend.domain.member.domain.MemberRole;
import com.backend.domain.member.entity.Member;
import com.backend.domain.member.repository.MemberQueryRepository;
import com.backend.domain.member.repository.MemberRepository;
import com.backend.domain.member.repository.MemberRepositoryImpl;
import com.backend.global.config.JpaAuditingConfig;
import com.backend.global.config.QuerydslConfig;
import com.backend.global.dto.request.GlobalRequest;
import com.backend.global.dto.response.ScrollResponse;
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

	@Test
	@DisplayName("동출 모집 신청자 페이징 조회 [Repository] - Success")
	void t02() {
		// Given
		Member savedMember = memberRepository.save(memberArbitraryBuilder
			.set("nickname", "테스터")
			.set("email", "test1@example.com")
			.sample());

		FishingTripPost savedPost = fishingTripPostRepository.save(fishingTripPostArbitraryBuilder
			.set("memberId", savedMember.getMemberId())
			.sample());

		List<FishingTripRecruitment> recruitments = LongStream.rangeClosed(1, 5)
			.mapToObj(i -> fishingTripRecruitmentArbitraryBuilder
				.set("fishingTripPostId", savedPost.getFishingTripPostId())
				.set("memberId", savedMember.getMemberId())
				.set("introduction", "신청자 소개글 " + i)
				.set("recruitmentStatus", RecruitmentStatus.PENDING)
				.build().sample())
			.toList();

		recruitments.forEach(fishingTripRecruitmentRepository::save);

		GlobalRequest.CursorRequest cursor = new GlobalRequest.CursorRequest(
			"asc", "createdAt", "next", null, null, 3
		);

		// When
		ScrollResponse<FishingTripRecruitmentResponse.DetailPage> response =
			fishingTripRecruitmentRepository.findDetailPageByFishingTripPostIdAndStatus(
				cursor, savedPost.getFishingTripPostId(), RecruitmentStatus.PENDING
			);

		// Then
		assertThat(response.content()).hasSize(3);
		assertThat(response.isFirst()).isTrue();
		assertThat(response.isLast()).isFalse();
	}

	@Test
	@DisplayName("게시글 ID로 APPROVED 상태인 신청자들의 회원 ID 조회 [Repository] - Success")
	void t03() {
		// Given
		FishingTripPost post = fishingTripPostRepository.save(fishingTripPostArbitraryBuilder.sample());

		List<Member> members = LongStream.rangeClosed(1, 3)
			.mapToObj(i -> memberRepository.save(
				memberArbitraryBuilder
					.set("nickname", "강태공" + i)
					.set("email", "test0" + i + "@naver.com")
					.set("phone", "010-0000-000" + i)
					.sample()))
			.toList();

		List<FishingTripRecruitment> recruitments = members.stream()
			.map(member -> fishingTripRecruitmentArbitraryBuilder
				.set("fishingTripPostId", post.getFishingTripPostId())
				.set("memberId", member.getMemberId())
				.set("recruitmentStatus", RecruitmentStatus.APPROVED)
				.sample())
			.toList();

		recruitments.forEach(fishingTripRecruitmentRepository::save);

		// When
		List<Long> result = fishingTripRecruitmentRepository.findMemberIdListByPostId(post.getFishingTripPostId());

		// Then
		List<Long> expectedIds = members.stream().map(Member::getMemberId).toList();
		assertThat(result).hasSize(3);
		assertThat(result).containsExactlyInAnyOrderElementsOf(expectedIds);
	}
}