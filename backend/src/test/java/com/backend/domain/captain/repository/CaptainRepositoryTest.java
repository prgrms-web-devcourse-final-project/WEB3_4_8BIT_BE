package com.backend.domain.captain.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.backend.domain.captain.dto.Response.CaptainResponse;
import com.backend.domain.captain.entity.Captain;
import com.backend.domain.member.domain.MemberRole;
import com.backend.domain.member.domain.Provider;
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

	final ArbitraryBuilder<Captain> arbitraryBuilder = fixtureMonkeyBuilder.giveMeBuilder(Captain.class)
		.set("memberId", 1L)
		.set("shipLicenseNumber", "1-2019123456")
		.set("shipList", List.of(101L, 102L));

	@Test
	@DisplayName("선장 저장 [Repository] - Success")
	void t01() {
		// Given
		Captain givenCaptain = arbitraryBuilder.sample();

		// When
		Captain savedCaptain = captainRepository.save(givenCaptain);

		// Then
		assertThat(savedCaptain.getMemberId()).isNotNull();
		assertThat(savedCaptain.getShipLicenseNumber()).isEqualTo("1-2019123456");
		assertThat(savedCaptain.getShipList()).isEqualTo(List.of(101L, 102L));
	}

	@Test
	@DisplayName("선장 상세 조회 [Repository] - Success")
	void t02() {
		// Given
		Member member = createAndSaveMember();

		Captain captain = Captain.builder()
			.memberId(member.getMemberId())
			.shipLicenseNumber("1-2019123456")
			.shipList(List.of(101L, 102L))
			.build();

		captainRepository.save(captain);

		// When
		Optional<CaptainResponse.Detail> result = captainRepository.findDetailById(member.getMemberId());

		// Then
		assertThat(result).isPresent();
		assertThat(result.get().memberId()).isEqualTo(member.getMemberId());
		assertThat(result.get().shipLicenseNumber()).isEqualTo("1-2019123456");
		assertThat(result.get().shipList()).isEqualTo(List.of(101L, 102L));
	}

	private Member createAndSaveMember() {
		Member member = Member.builder()
			.email("test@naver.com")
			.name("루피")
			.nickname("해적왕")
			.phone("010-9874-1935")
			.profileImg("http://example.com/image1.jpg")
			.description("해적왕이 되고싶은 루피 입니다.")
			.role(MemberRole.CAPTAIN)
			.provider(Provider.KAKAO)
			.providerId("kakao_123")
			.isAddInfo(true)
			.build();
		return memberRepository.save(member);
	}
}