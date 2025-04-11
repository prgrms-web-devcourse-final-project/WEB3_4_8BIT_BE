package com.backend.domain.member.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.backend.domain.member.domain.MemberRole;
import com.backend.domain.member.entity.Member;
import com.backend.global.util.BaseTest;
import com.backend.global.config.JpaAuditingConfig;
import com.backend.global.config.QuerydslConfig;

import com.navercorp.fixturemonkey.ArbitraryBuilder;

@DataJpaTest
@Import({QuerydslConfig.class, JpaAuditingConfig.class, MemberRepositoryImpl.class, MemberQueryRepository.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MemberRepositoryTest extends BaseTest {

	@Autowired
	private MemberRepository memberRepository;

	private final ArbitraryBuilder<Member> arbitraryBuilder = fixtureMonkeyBuilder
		.giveMeBuilder(Member.class)
		.set("memberId", null)
		.set("phone", "010-1234-5678")
		.set("email", "test@naver.com")
		.set("nickname", "테스트")
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

	@Test
	@DisplayName("회원 ID 목록으로 이메일 리스트 조회 [Repository] - Success")
	void t04() {
		// given
		Member member1 = arbitraryBuilder.set("nickname", "tester1")
			.set("phone", "010-0000-0001")
			.set("email", "test1@naver.com").sample();

		Member member2 = arbitraryBuilder.set("nickname", "tester2")
			.set("phone", "010-0000-0002")
			.set("email", "test2@naver.com").sample();

		Member member3 = arbitraryBuilder.set("nickname", "tester3")
			.set("phone", "010-0000-0003")
			.set("email", "test3@naver.com").sample();

		Member saved1 = memberRepository.save(member1);
		Member saved2 = memberRepository.save(member2);
		Member saved3 = memberRepository.save(member3);

		List<Long> memberIds = List.of(saved1.getMemberId(), saved2.getMemberId(), saved3.getMemberId());

		// when
		List<String> result = memberRepository.findEmailListByIdList(memberIds);

		// then
		assertThat(result).hasSize(3);
		assertThat(result).containsExactlyInAnyOrder("test1@naver.com", "test2@naver.com", "test3@naver.com");
	}
}