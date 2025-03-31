package com.backend.domain.member.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.backend.domain.member.dto.MemberRequest;
import com.backend.domain.member.dto.MemberResponse;
import com.backend.domain.member.exception.MemberErrorCode;
import com.backend.domain.member.exception.MemberException;
import com.backend.domain.member.service.MemberService;
import com.backend.global.auth.WithMockCustomUser;
import com.backend.global.config.TestSecurityConfig;
import com.backend.global.util.BaseTest;
import com.fasterxml.jackson.databind.ObjectMapper;

@Import({TestSecurityConfig.class})
@WebMvcTest(MemberController.class)
@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
public class MemberControllerTest extends BaseTest {

	@MockitoBean
	private MemberService memberService;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	@DisplayName("회원 추가 정보 입력 [Controller] - Success")
	@WithMockCustomUser
	void t01() throws Exception {
		// Given
		MemberRequest.Create givenRequest = fixtureMonkeyValidation.giveMeOne(MemberRequest.Create.class);
		Long memberId = 1L;

		when(memberService.saveAddInfo(eq(memberId), any())).thenReturn(memberId);

		// When
		ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/members")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(givenRequest)));

		// Then
		resultActions
			.andExpect(status().isCreated())
			.andExpect(header().exists("Location"))
			.andExpect(header().string("Location", memberId.toString()))
			.andExpect(jsonPath("$.success").value(true));
	}

	@Test
	@WithMockCustomUser
	@DisplayName("회원 추가 정보 입력 [MEMBER_NOT_FOUND] [Controller] - Fail")
	void t02() throws Exception {
		// Given
		MemberRequest.Create givenRequest = fixtureMonkeyValidation.giveMeOne(MemberRequest.Create.class);

		doThrow(new MemberException(MemberErrorCode.MEMBER_NOT_FOUND))
			.when(memberService).saveAddInfo(anyLong(), any());

		// When
		ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/members")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(givenRequest)));

		// Then
		resultActions
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value(3003))
			.andExpect(jsonPath("$.message").value("해당 사용자를 찾을 수 없습니다."))
			.andExpect(jsonPath("$.success").value(false));
	}

	@Test
	@WithMockCustomUser
	@DisplayName("회원 추가 정보 입력 [ALREADY_ADDED_INFO] [Controller] - Fail")
	void t03() throws Exception {
		// Given
		MemberRequest.Create givenRequest = fixtureMonkeyValidation.giveMeOne(MemberRequest.Create.class);

		doThrow(new MemberException(MemberErrorCode.ALREADY_ADDED_INFO))
			.when(memberService).saveAddInfo(anyLong(), any());

		// When
		ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/members")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(givenRequest)));

		// Then
		resultActions
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value(3004))
			.andExpect(jsonPath("$.message").value("이미 추가 정보를 입력한 회원입니다."))
			.andExpect(jsonPath("$.success").value(false));
	}

	@Test
	@WithMockCustomUser
	@DisplayName("회원 추가 정보 입력 [nickname null] [Controller] - Fail")
	void t04() throws Exception {
		// Given
		MemberRequest.Create invalidRequest = new MemberRequest.Create(
			"",
			"http://example.com/profile.jpg",
			"자기소개입니다."
		);

		// When
		ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/members")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(invalidRequest)));

		// Then
		resultActions
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value(5001))
			.andExpect(jsonPath("$.data[0].field").value("nickname"))
			.andExpect(jsonPath("$.data[0].reason").value("닉네임은 필수 항목입니다."))
			.andExpect(jsonPath("$.message").value("요청하신 유효성 검증에 실패하였습니다."))
			.andExpect(jsonPath("$.success").value(false));
	}

	@Test
	@WithMockCustomUser
	@DisplayName("회원 추가 정보 입력 [description null] [Controller] - Fail")
	void t05() throws Exception {
		// Given
		MemberRequest.Create invalidRequest = new MemberRequest.Create(
			"닉네임",
			"http://example.com/profile.jpg",
			""
		);

		// When
		ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/members")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(invalidRequest)));

		// Then
		resultActions
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value(5001))
			.andExpect(jsonPath("$.data[0].field").value("description"))
			.andExpect(jsonPath("$.data[0].reason").value("자기소개는 필수 항목입니다."))
			.andExpect(jsonPath("$.message").value("요청하신 유효성 검증에 실패하였습니다."))
			.andExpect(jsonPath("$.success").value(false));
	}

	@Test
	@WithMockCustomUser
	@DisplayName("회원 추가 정보 입력 [nickname too long] [Controller] - Fail")
	void t06() throws Exception {
		// Given
		String tooLongNickname = "a".repeat(31);
		MemberRequest.Create invalidRequest = new MemberRequest.Create(
			tooLongNickname,
			"http://example.com/profile.jpg",
			"자기소개입니다."
		);

		// When
		ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/members")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(invalidRequest)));

		// Then
		resultActions
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value(5001))
			.andExpect(jsonPath("$.data[0].field").value("nickname"))
			.andExpect(jsonPath("$.data[0].reason").value("닉네임은 최대 30자까지 가능합니다."))
			.andExpect(jsonPath("$.message").value("요청하신 유효성 검증에 실패하였습니다."))
			.andExpect(jsonPath("$.success").value(false));
	}

	@Test
	@WithMockCustomUser
	@DisplayName("회원 정보 조회 [Controller] - Success")
	void t07() throws Exception {
		// Given
		Long memberId = 1L;
		MemberResponse.Detail response = MemberResponse.Detail.builder()
			.memberId(memberId)
			.email("test@naver.com")
			.name("홍길동")
			.nickname("테스트닉")
			.phone("010-1234-5678")
			.profileImg("http://example.com/profile.jpg")
			.description("자기소개입니다.")
			.build();

		when(memberService.getMemberDetail(memberId)).thenReturn(response);

		// When
		ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/members"));

		// Then
		resultActions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.data.name").value("홍길동"))
			.andExpect(jsonPath("$.data.email").value("test@naver.com"))
			.andExpect(jsonPath("$.data.nickname").value("테스트닉"));
	}

	@Test
	@WithMockCustomUser
	@DisplayName("회원 정보 조회 [MEMBER_NOT_FOUND] [Controller] - Fail")
	void t08() throws Exception {
		// Given
		doThrow(new MemberException(MemberErrorCode.MEMBER_NOT_FOUND))
			.when(memberService).getMemberDetail(anyLong());

		// When
		ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/members"));

		// Then
		resultActions
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value(3003))
			.andExpect(jsonPath("$.message").value("해당 사용자를 찾을 수 없습니다."))
			.andExpect(jsonPath("$.success").value(false));
	}

}
