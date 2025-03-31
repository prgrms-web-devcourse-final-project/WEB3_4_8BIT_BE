package com.backend.domain.captain.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.backend.domain.captain.dto.Request.CaptainRequest;
import com.backend.domain.captain.entity.Captain;
import com.backend.domain.captain.repository.CaptainRepository;
import com.backend.domain.member.entity.Member;
import com.backend.domain.member.exception.MemberErrorCode;
import com.backend.domain.member.exception.MemberException;
import com.backend.domain.member.repository.MemberRepository;
import com.backend.global.util.BaseTest;

@ExtendWith(MockitoExtension.class)
class CaptainServiceTest extends BaseTest {

	@InjectMocks
	private CaptainServiceImpl captainService;

	@Mock
	private CaptainRepository captainRepository;

	@Mock
	private MemberRepository memberRepository;

	@Test
	@DisplayName("선장 저장 [Service] - Success")
	void t01() {
		// Given
		CaptainRequest.Create givenRequestDto = fixtureMonkeyValidation.giveMeBuilder(CaptainRequest.Create.class)
			.set("nickname", "해적왕")
			.set("profileImg", "http://example.com/image.jpg")
			.set("descrption", "해적왕이 되고싶은 루피 입니다.")
			.set("shipLicenseNumber", "1-2019123456")
			.set("shipList", List.of(1L, 2L, 3L))
			.sample();

		Member givenMember = fixtureMonkeyBuilder.giveMeBuilder(Member.class)
			.set("memberId", 1L)
			.set("isAddInfo", false)
			.sample();

		when(memberRepository.findById(givenMember.getMemberId()))
			.thenReturn(Optional.of(givenMember));

		when(captainRepository.save(any(Captain.class)))
			.thenAnswer(invocation -> invocation.getArgument(0));

		// When
		Long savedId = captainService.createCaptain(givenMember.getMemberId(), givenRequestDto);

		// Then
		assertThat(savedId).isEqualTo(givenMember.getMemberId());

		// member가 업데이트 되었는지 확인
		assertThat(givenMember.getNickname()).isEqualTo(givenRequestDto.nickname());
		assertThat(givenMember.getProfileImg()).isEqualTo(givenRequestDto.profileImg());
		assertThat(givenMember.getDescription()).isEqualTo(givenRequestDto.descrption());
		assertThat(givenMember.getRole().name()).isEqualTo("CAPTAIN");
		assertThat(givenMember.getIsAddInfo()).isTrue();

		verify(memberRepository, times(1)).findById(givenMember.getMemberId());
		verify(captainRepository, times(1)).save(any(Captain.class));
	}


	@Test
	@DisplayName("선장 저장 [Member Not Exists] [Service] - Fail")
	void t02() {
		// Given
		CaptainRequest.Create givenRequestDto = fixtureMonkeyValidation.giveMeBuilder(CaptainRequest.Create.class)
			.set("nickname", "해적왕")
			.set("shipLicenseNumber", "1-2019123456")
			.sample();

		Long nonExistMemberId = 999L;

		when(memberRepository.findById(nonExistMemberId)).thenReturn(Optional.empty());

		// When & Then
		assertThatThrownBy(() -> captainService.createCaptain(nonExistMemberId, givenRequestDto))
			.isExactlyInstanceOf(MemberException.class)
			.hasMessage(MemberErrorCode.MEMBER_NOT_FOUND.getMessage());

		verify(memberRepository, times(1)).findById(nonExistMemberId);
		verify(captainRepository, never()).save(any());
	}

}