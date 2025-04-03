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
import com.backend.domain.captain.dto.Response.CaptainResponse;
import com.backend.domain.captain.entity.Captain;
import com.backend.domain.captain.exception.CaptainErrorCode;
import com.backend.domain.captain.exception.CaptainException;
import com.backend.domain.captain.repository.CaptainRepository;
import com.backend.domain.member.domain.MemberRole;
import com.backend.domain.member.entity.Member;
import com.backend.domain.member.exception.MemberErrorCode;
import com.backend.domain.member.exception.MemberException;
import com.backend.domain.member.repository.MemberRepository;
import com.backend.global.util.BaseTest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
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
			.set("description", "해적왕이 되고싶은 루피 입니다.")
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
		assertThat(givenMember.getDescription()).isEqualTo(givenRequestDto.description());
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

	@Test
	@DisplayName("선장 상세 조회 [Service] - Success")
	void t03() {
		// Given
		CaptainResponse.Detail givenResponseDto = fixtureMonkeyValidation.giveMeBuilder(CaptainResponse.Detail.class)
			.set("memberId", 1L)
			.set("name", "루피")
			.set("nickname", "해적왕")
			.set("description", "해적왕이 되고싶은 루피 입니다.")
			.set("role", MemberRole.CAPTAIN)
			.set("shipLicenseNumber", "1-2019123456")
			.set("shipList", List.of(101L, 102L))
			.sample();

		when(captainRepository.findDetailById(givenResponseDto.memberId()))
			.thenReturn(Optional.of(givenResponseDto));

		// When
		CaptainResponse.Detail result = captainService.getCaptainDetail(givenResponseDto.memberId());

		// Then
		verify(captainRepository, times(1)).findDetailById(givenResponseDto.memberId());
		assertThat(result).isEqualTo(givenResponseDto);
	}

	@Test
	@DisplayName("선장 상세 조회 [CAPTAIN_NOT_FOUND] [Service] - Fail")
	void t04() {
		// Given
		Long invalidCaptainId = 999L;

		when(captainRepository.findDetailById(invalidCaptainId)).thenReturn(Optional.empty());

		// When & Then
		assertThatThrownBy(() -> captainService.getCaptainDetail(invalidCaptainId))
			.isInstanceOf(CaptainException.class)
			.hasFieldOrPropertyWithValue("errorCode", CaptainErrorCode.CAPTAIN_NOT_FOUND)
			.hasMessageContaining(CaptainErrorCode.CAPTAIN_NOT_FOUND.getMessage());

		verify(captainRepository, times(1)).findDetailById(invalidCaptainId);
	}

	@Test
	@DisplayName("선장 상세 조회 [NOT_CAPTAIN] 일반 멤버가 접근 - Fail")
	void t05() {
		// Given
		Long memberId = 1L;

		// 일반 멤버 (선장이 아님)
		CaptainResponse.Detail givenResponseDto = fixtureMonkeyValidation.giveMeBuilder(CaptainResponse.Detail.class)
			.set("memberId", memberId)
			.set("role", MemberRole.USER)
			.sample();

		when(captainRepository.findDetailById(memberId)).thenReturn(Optional.of(givenResponseDto));

		// When & Then
		assertThatThrownBy(() -> captainService.getCaptainDetail(memberId))
			.isInstanceOf(CaptainException.class)
			.hasFieldOrPropertyWithValue("errorCode", CaptainErrorCode.NOT_CAPTAIN)
			.hasMessageContaining(CaptainErrorCode.NOT_CAPTAIN.getMessage());

		verify(captainRepository, times(1)).findDetailById(memberId);

	}

	@Test
	@DisplayName("선장 보유 배 정보 수정 [Service] - Success")
	void t06() {
		// Given
		Long captainId = 1L;
		CaptainRequest.Update givenRequest = fixtureMonkeyValidation.giveMeBuilder(CaptainRequest.Update.class)
			.set("shipList", List.of(2L, 3L, 4L))
			.sample();

		Captain existingCaptain = fixtureMonkeyBuilder.giveMeBuilder(Captain.class)
			.set("memberId", captainId)
			.set("shipList", List.of(1L))
			.sample();

		when(captainRepository.findById(captainId)).thenReturn(Optional.of(existingCaptain));

		// When
		Long updatedId = captainService.updateCaptainShipList(captainId, givenRequest);

		// Then
		assertThat(updatedId).isEqualTo(captainId);
		assertThat(existingCaptain.getShipList()).isEqualTo(givenRequest.shipList());

		verify(captainRepository, times(1)).findById(captainId);
	}

	@Test
	@DisplayName("선장 보유 배 정보 수정 [CAPTAIN_NOT_FOUND] [Service] - Fail")
	void t07() {
		// Given
		Long invalidCaptainId = 999L;
		CaptainRequest.Update givenRequest = fixtureMonkeyValidation.giveMeBuilder(CaptainRequest.Update.class)
			.set("shipList", List.of(2L, 3L, 4L))
			.sample();

		when(captainRepository.findById(invalidCaptainId)).thenReturn(Optional.empty());

		// When & Then
		assertThatThrownBy(() -> captainService.updateCaptainShipList(invalidCaptainId, givenRequest))
			.isInstanceOf(CaptainException.class)
			.hasFieldOrPropertyWithValue("errorCode", CaptainErrorCode.CAPTAIN_NOT_FOUND)
			.hasMessageContaining(CaptainErrorCode.CAPTAIN_NOT_FOUND.getMessage());

		verify(captainRepository, times(1)).findById(invalidCaptainId);
	}

}
