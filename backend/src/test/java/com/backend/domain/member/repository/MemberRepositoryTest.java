package com.backend.domain.member.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.backend.domain.member.domain.MemberRole;
import com.backend.domain.member.entity.Member;
import com.backend.global.Util.BaseTest;
import com.backend.global.config.JpaAuditingConfig;
import com.backend.global.config.QuerydslConfig;

import com.navercorp.fixturemonkey.ArbitraryBuilder;

@DataJpaTest
@Import({QuerydslConfig.class, JpaAuditingConfig.class, MemberRepositoryImpl.class, MemberQueryRepository.class})
class MemberRepositoryTest extends BaseTest {

	@Autowired
	private MemberRepository memberRepository;

	private final ArbitraryBuilder<Member> arbitraryBuilder = fixtureMonkeyBuilder
		.giveMeBuilder(Member.class)
		.set("memberId", null)
		.set("phone", "010-1234-5678")
		.set("email","test@naver.com")
		.set("nickname","테스트")
		.set("role", MemberRole.USER)
		.set("name", "test");

	@Test
	@DisplayName("회원 정보 저장 [Repository] - Success")
	void t01() {
		// Given
		Member givenMember = arbitraryBuilder
			.sample();

		// When
		Member saved = memberRepository.save(givenMember);

		// Then
		assertThat(saved).isNotNull();
		assertThat(saved.getMemberId()).isNotNull();
		assertThat(saved.getName()).isEqualTo("test");
		assertThat(saved.getPhone()).isEqualTo("010-1234-5678");
	}

	@Test
	@DisplayName("핸드폰으로 회원 조회 [Repository] - Success")
	void t02() {
		String phone = "010-1234-5678";
		Member givenMember = arbitraryBuilder
			.sample();

		memberRepository.save(givenMember);

		// When
		Optional<Member> result = memberRepository.findByPhone(phone);

		// Then
		assertThat(result).isPresent();
		assertThat(result.get().getPhone()).isEqualTo(phone);
		assertThat(result.get().getName()).isEqualTo(givenMember.getName());

	}

	@Test
	@DisplayName("member id로 회원 조회 [Repository] - Success")
	void t03() {
		// given
		Member givenMember = arbitraryBuilder
			.sample();

		Member saved = memberRepository.save(givenMember);

		// when
		Optional<Member> result = memberRepository.findById(saved.getMemberId());

		// then
		assertThat(result).isPresent();
		assertThat(result.get().getName()).isEqualTo("test");
		assertThat(result.get().getPhone()).isEqualTo("010-1234-5678");
	}
}