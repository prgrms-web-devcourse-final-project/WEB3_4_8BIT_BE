package com.backend.domain.captain.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.backend.domain.captain.dto.Response.CaptainResponse;
import com.backend.domain.captain.entity.Captain;
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
@EnableJpaRepositories(basePackages = {
	"com.backend.domain.captain.repository",
	"com.backend.domain.member.repository"
})
@EntityScan(basePackages = {
	"com.backend.domain.captain.entity",
	"com.backend.domain.member.entity"
})
@Import({
	JpaAuditingConfig.class,
	QuerydslConfig.class,
	CaptainRepositoryImpl.class,
	CaptainQueryRepository.class,
	MemberRepositoryImpl.class,
	MemberQueryRepository.class
})
class CaptainRepositoryTest extends BaseTest {

	@Autowired
	private CaptainRepository captainRepository;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	TestEntityManager em;

	private final ArbitraryBuilder<Member> memberArbitraryBuilder = fixtureMonkeyBuilder.giveMeBuilder(Member.class)
		.set("memberId", null)
		.set("phone", "010-1234-5678")
		.set("email", "test@naver.com")
		.set("nickname", "테스트")
		.set("role", MemberRole.USER)
		.set("name", "test");

	final ArbitraryBuilder<Captain> captainArbitraryBuilder = fixtureMonkeyBuilder.giveMeBuilder(Captain.class)
		.set("memberId", 1L)
		.set("shipLicenseNumber", "1-2019123456")
		.set("shipList", List.of(101L, 102L));

	@Test
	@DisplayName("선장 저장 [Repository] - Success")
	void t01() {
		Member savedMember = memberRepository.save(memberArbitraryBuilder.sample());

		Captain captain = captainArbitraryBuilder
			.set("memberId", savedMember.getMemberId())
			.sample();

		// When
		Captain savedCaptain = captainRepository.save(captain);

		// Then
		assertThat(savedCaptain.getMemberId()).isNotNull();
		assertThat(savedCaptain.getShipLicenseNumber()).isEqualTo("1-2019123456");
		assertThat(savedCaptain.getShipList()).isEqualTo(List.of(101L, 102L));
	}

	@Test
	@DisplayName("선장 상세 조회 [Repository] - Success")
	void t02() {
		// Given
		Member member = memberArbitraryBuilder.sample();
		Member savedMember = em.persist(member);

		Captain captain = captainArbitraryBuilder
			.set("memberId", savedMember.getMemberId())
			.sample();

		em.persist(captain);
		em.flush();

		// When
		Optional<CaptainResponse.Detail> result = captainRepository.findDetailById(savedMember.getMemberId());

		// Then
		assertThat(result).isPresent();
		assertThat(result.get().memberId()).isEqualTo(savedMember.getMemberId());
		assertThat(result.get().shipLicenseNumber()).isEqualTo("1-2019123456");
		assertThat(result.get().shipList()).isEqualTo(List.of(101L, 102L));
	}

}
