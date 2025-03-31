package com.backend.domain.member.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.backend.domain.member.domain.MemberRole;
import com.backend.domain.member.dto.MemberRequest;
import com.backend.domain.member.dto.MemberResponse;
import com.backend.domain.member.entity.Member;
import com.backend.domain.member.exception.MemberErrorCode;
import com.backend.domain.member.exception.MemberException;
import com.backend.domain.member.repository.MemberRepository;
import com.backend.global.util.BaseTest;

import com.navercorp.fixturemonkey.ArbitraryBuilder;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest extends BaseTest {

	@InjectMocks
	private MemberServiceImpl memberService;

	@Mock
	private MemberRepository memberRepository;

	private final ArbitraryBuilder<Member> arbitraryBuilder = fixtureMonkeyBuilder
		.giveMeBuilder(Member.class)
		.set("phone", "010-1234-5678")
		.set("email", "test@naver.com")
		.set("role", MemberRole.USER)
		.set("name", "test");

	@Test
	@DisplayName("회원 추가 정보 저장 [Service] - Success")
	void t01() {
		// Given
		MemberRequest.form givenRequest = fixtureMonkeyValidation.giveMeOne(MemberRequest.form.class);

		Member givenMember = fixtureMonkeyBuilder.giveMeBuilder(Member.class)
			.set("memberId", 1L)
			.set("isAddInfo", false)
			.set("memberId", null)
			.set("phone", "010-1234-5678")
			.set("email", "test@naver.com")
			.set("nickname", "테스트")
			.set("role", MemberRole.USER)
			.set("name", "test")
			.sample();

		when(memberRepository.findById(givenMember.getMemberId())).thenReturn(Optional.of(givenMember));

		// When
		Long saveAddInfo = memberService.saveAddInfo(givenMember.getMemberId(), givenRequest);

		// Then
		assertThat(saveAddInfo).isEqualTo(givenMember.getMemberId());
		assertThat(givenMember.getNickname()).isEqualTo(givenRequest.nickname());
		assertThat(givenMember.getProfileImg()).isEqualTo(givenRequest.profileImg());
		assertThat(givenMember.getDescription()).isEqualTo(givenRequest.description());
		assertThat(givenMember.getIsAddInfo()).isTrue();
		verify(memberRepository, times(1)).findById(givenMember.getMemberId());
	}

	@Test
	@DisplayName("회원 추가 정보 저장 [MEMBER_NOT_FOUND] [Service] - Fail")
	void t02() {
		// Given
		Long invalidMemberId = 600L;
		MemberRequest.form givenRequest = fixtureMonkeyValidation.giveMeOne(MemberRequest.form.class);

		when(memberRepository.findById(invalidMemberId)).thenReturn(Optional.empty());

		// When & Then
		assertThatThrownBy(() -> memberService.saveAddInfo(invalidMemberId, givenRequest))
			.isInstanceOf(MemberException.class)
			.hasMessage(MemberErrorCode.MEMBER_NOT_FOUND.getMessage());

		verify(memberRepository, times(1)).findById(invalidMemberId);
	}

	@Test
	@DisplayName("회원 추가 정보 저장 [ALREADY_ADDED_INFO] [Service] - Fail")
	void t03() {
		// Given
		MemberRequest.form givenRequest = fixtureMonkeyValidation.giveMeOne(MemberRequest.form.class);

		Member alreadyAddedMember = fixtureMonkeyBuilder.giveMeBuilder(Member.class)
			.set("memberId", 1L)
			.set("isAddInfo", true) // 이미 추가된 상태
			.sample();

		when(memberRepository.findById(alreadyAddedMember.getMemberId())).thenReturn(Optional.of(alreadyAddedMember));

		// When & Then
		assertThatThrownBy(() -> memberService.saveAddInfo(alreadyAddedMember.getMemberId(), givenRequest))
			.isInstanceOf(MemberException.class)
			.hasMessage(MemberErrorCode.ALREADY_ADDED_INFO.getMessage());

		verify(memberRepository, times(1)).findById(alreadyAddedMember.getMemberId());
	}

	@Test
	@DisplayName("회원 상세 정보 조회 [Service] - Success")
	void t04() {
		// Given
		Long memberId = 1L;

		Member givenMember = arbitraryBuilder
			.set("memberId", memberId)
			.set("profileImg", "http://example.com/profile.jpg")
			.set("description", "자기소개입니다.")
			.set("nickname", "닉네임")
			.sample();

		when(memberRepository.findById(memberId)).thenReturn(Optional.of(givenMember));

		// When
		MemberResponse.Detail result = memberService.getMemberDetail(memberId);

		// Then
		assertThat(result).isNotNull();
		assertThat(result.memberId()).isEqualTo(givenMember.getMemberId());
		assertThat(result.name()).isEqualTo(givenMember.getName());
		assertThat(result.email()).isEqualTo(givenMember.getEmail());
		assertThat(result.nickname()).isEqualTo(givenMember.getNickname());
		assertThat(result.phone()).isEqualTo(givenMember.getPhone());
		assertThat(result.profileImg()).isEqualTo(givenMember.getProfileImg());
		assertThat(result.description()).isEqualTo(givenMember.getDescription());
		verify(memberRepository, times(1)).findById(memberId);
	}

	@Test
	@DisplayName("회원 정보 수정 [Service] - Success")
	void t05() {
		// Given
		Long memberId = 1L;
		MemberRequest.form givenRequest = new MemberRequest.form("수정된닉네임", "http://new.url/profile.jpg", "수정된 자기소개");

		Member givenMember = arbitraryBuilder
			.set("memberId", memberId)
			.set("nickname", "이전닉네임")
			.set("profileImg", "http://old.url/profile.jpg")
			.set("description", "이전 자기소개")
			.sample();

		when(memberRepository.findById(memberId)).thenReturn(Optional.of(givenMember));

		// When
		Long updatedId = memberService.updateMember(memberId, givenRequest);

		// Then
		assertThat(updatedId).isEqualTo(memberId);
		assertThat(givenMember.getNickname()).isEqualTo(givenRequest.nickname());
		assertThat(givenMember.getProfileImg()).isEqualTo(givenRequest.profileImg());
		assertThat(givenMember.getDescription()).isEqualTo(givenRequest.profileImg());
		verify(memberRepository, times(1)).findById(memberId);
	}
}