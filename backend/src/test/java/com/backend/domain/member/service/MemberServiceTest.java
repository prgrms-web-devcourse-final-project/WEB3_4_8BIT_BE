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

import com.backend.domain.member.dto.MemberRequest;
import com.backend.domain.member.entity.Member;
import com.backend.domain.member.exception.MemberErrorCode;
import com.backend.domain.member.exception.MemberException;
import com.backend.domain.member.repository.MemberRepository;
import com.backend.global.Util.BaseTest;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest extends BaseTest {

	@InjectMocks
	private MemberServiceImpl memberService;

	@Mock
	private MemberRepository memberRepository;

	@Test
	@DisplayName("회원 추가 정보 저장 [Service] - Success")
	void t01() {
		// Given
		MemberRequest.Create givenRequest = fixtureMonkeyValidation.giveMeOne(MemberRequest.Create.class);

		Member givenMember = fixtureMonkeyBuilder.giveMeBuilder(Member.class)
			.set("memberId", 1L)
			.set("isAddInfo", false)
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
		MemberRequest.Create givenRequest = fixtureMonkeyValidation.giveMeOne(MemberRequest.Create.class);

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
		MemberRequest.Create givenRequest = fixtureMonkeyValidation.giveMeOne(MemberRequest.Create.class);

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

}