package com.backend.domain.captain.controller;

import static org.hamcrest.Matchers.contains;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.backend.domain.captain.dto.Request.CaptainRequest;
import com.backend.domain.captain.dto.Response.CaptainResponse;
import com.backend.domain.captain.exception.CaptainErrorCode;
import com.backend.domain.captain.exception.CaptainException;
import com.backend.domain.captain.service.CaptainService;
import com.backend.global.auth.WithMockCustomUser;
import com.backend.global.config.TestSecurityConfig;
import com.backend.global.exception.GlobalErrorCode;
import com.backend.global.util.BaseTest;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

import com.navercorp.fixturemonkey.ArbitraryBuilder;

@Slf4j
@WebMvcTest(CaptainController.class)
@ExtendWith(MockitoExtension.class)
@Import(TestSecurityConfig.class)
class CaptainControllerTest extends BaseTest {

	@MockitoBean
	private CaptainService captainService;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	final ArbitraryBuilder<CaptainRequest.Create> arbitraryBuilder = fixtureMonkeyRecord
		.giveMeBuilder(CaptainRequest.Create.class)
		.set("nickname", "해적왕")
		.set("profileImg", "http://example.com/image.jpg")
		.set("descrption", "해적왕이 되고싶은 루피 입니다.")
		.set("shipLicenseNumber", "1-2019123456")
		.set("shipList", List.of(1L, 2L, 3L));

	@Test
	@DisplayName("선장 저장 [Controller] - Success")
	@WithMockCustomUser
	void t01() throws Exception {
		// Given
		CaptainRequest.Create requestDto = arbitraryBuilder.sample();
		Long savedId = 1L;

		when(captainService.createCaptain(1L, requestDto)).thenReturn(savedId);

		// When
		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/members/captains")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(requestDto)));

		// Then
		result
			.andExpect(status().isCreated())
			.andExpect(header().string("Location", savedId.toString()))
			.andExpect(jsonPath("$.success").value(true));
	}

	@Test
	@DisplayName("선장 저장 [nickname null] [Controller] - Fail")
	@WithMockCustomUser
	void t02() throws Exception {
		CaptainRequest.Create requestDto = arbitraryBuilder.set("nickname", null).sample();

		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/members/captains")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(requestDto)));

		result
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value(GlobalErrorCode.NOT_VALID.getCode()))
			.andExpect(jsonPath("$.data[0].field").value("nickname"))
			.andExpect(jsonPath("$.data[0].reason").value("닉네임은 필수 항목입니다."))
			.andExpect(jsonPath("$.success").value(false));
	}

	@Test
	@DisplayName("선장 저장 [shipLicenseNumber null] [Controller] - Fail")
	@WithMockCustomUser
	void t03() throws Exception {
		CaptainRequest.Create requestDto = arbitraryBuilder.set("shipLicenseNumber", null).sample();

		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/members/captains")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(requestDto)));

		result
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value(GlobalErrorCode.NOT_VALID.getCode()))
			.andExpect(jsonPath("$.data[0].field").value("shipLicenseNumber"))
			.andExpect(jsonPath("$.data[0].reason").value("선박 운전 면허 번호는 필수 항목입니다."))
			.andExpect(jsonPath("$.success").value(false));
	}

	@Test
	@DisplayName("선장 저장 [descrption null] [Controller] - Fail")
	@WithMockCustomUser
	void t04() throws Exception {
		CaptainRequest.Create requestDto = arbitraryBuilder.set("descrption", null).sample();

		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/members/captains")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(requestDto)));

		result
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.data[0].field").value("descrption"))
			.andExpect(jsonPath("$.data[0].reason").value("자기 소개글은 필수 항목입니다."))
			.andExpect(jsonPath("$.success").value(false));
	}

	@Test
	@DisplayName("선장 저장 [nickname length > 30] [Controller] - Fail")
	@WithMockCustomUser
	void t05() throws Exception {
		String longNickname = "a".repeat(31);
		CaptainRequest.Create request = arbitraryBuilder.set("nickname", longNickname).sample();

		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/members/captains")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)));

		result
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value(GlobalErrorCode.NOT_VALID.getCode()))
			.andExpect(jsonPath("$.data[0].field").value("nickname"))
			.andExpect(jsonPath("$.data[0].reason").value("닉네임은 최대 30자까지 가능합니다."))
			.andExpect(jsonPath("$.success").value(false));
	}

	@Test
	@DisplayName("선장 상세 조회 [Controller] - Success")
	@WithMockCustomUser
	void t06() throws Exception {
		// Given
		Long memberId = 1L;
		CaptainResponse.Detail givenResponse = fixtureMonkeyValidation.giveMeBuilder(CaptainResponse.Detail.class)
			.set("memberId", memberId)
			.set("email", "test@naver.com")
			.set("name", "루피")
			.set("nickname", "해적왕")
			.set("profileImg", "http://example.com/image.jpg")
			.set("description", "해적왕이 되고싶은 루피 입니다.")
			.set("shipLicenseNumber", "1-2019123456")
			.set("shipList", List.of(1L, 2L, 3L))
			.sample();

		when(captainService.getCaptainDetail(memberId)).thenReturn(givenResponse);

		// When
		ResultActions result = mockMvc.perform(
			MockMvcRequestBuilders.get("/api/v1/members/captains")
				.contentType(MediaType.APPLICATION_JSON));

		log.info("{}", result);

		// Then
		result
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.data.memberId").value(1L))
			.andExpect(jsonPath("$.data.nickname").value("해적왕"))
			.andExpect(jsonPath("$.data.name").value("루피"))
			.andExpect(jsonPath("$.data.profileImg").value("http://example.com/image.jpg"))
			.andExpect(jsonPath("$.data.description").value("해적왕이 되고싶은 루피 입니다."))
			.andExpect(jsonPath("$.data.shipLicenseNumber").value("1-2019123456"))
			.andExpect(jsonPath("$.data.shipList", contains(1, 2, 3)));
	}

	@Test
	@DisplayName("선장 상세 조회 [CAPTAIN_NOT_FOUND] [Controller] - Fail")
	@WithMockCustomUser
	void t07() throws Exception {
		// Given
		doThrow(new CaptainException(CaptainErrorCode.CAPTAIN_NOT_FOUND))
			.when(captainService).getCaptainDetail(anyLong());

		// When
		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/members/captains"));

		// Then
		result
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.code").value(CaptainErrorCode.CAPTAIN_NOT_FOUND.getCode()))
			.andExpect(jsonPath("$.message").value(CaptainErrorCode.CAPTAIN_NOT_FOUND.getMessage()));
	}

	@Test
	@DisplayName("선장 상세 조회 [NOT_CAPTAIN] [Controller] - Fail")
	@WithMockCustomUser
	void t08() throws Exception {
		// Given
		doThrow(new CaptainException(CaptainErrorCode.NOT_CAPTAIN))
			.when(captainService).getCaptainDetail(anyLong());

		// When
		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/members/captains"));

		// Then
		result
			.andExpect(status().isForbidden())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.code").value(CaptainErrorCode.NOT_CAPTAIN.getCode()))
			.andExpect(jsonPath("$.message").value(CaptainErrorCode.NOT_CAPTAIN.getMessage()));
	}

	@Test
	@WithMockCustomUser
	@DisplayName("선장 배 정보 수정 [Controller] - Success")
	void t09() throws Exception {
		// Given
		CaptainRequest.Update updateRequest = new CaptainRequest.Update(List.of(2L, 3L, 4L));

		Long captainId = 1L;

		when(captainService.updateCaptainShipList(eq(captainId), any())).thenReturn(captainId);

		// When
		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/members/captains")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(updateRequest)));

		// Then
		result
			.andExpect(status().isCreated())
			.andExpect(header().exists("Location"))
			.andExpect(header().string("Location", captainId.toString()))
			.andExpect(jsonPath("$.success").value(true));
	}

	@Test
	@WithMockCustomUser
	@DisplayName("선장 배 정보 수정 [shipList = null] [Controller] - Fail")
	void t10() throws Exception {
		// Given
		String requestBody = """
			{
				"shipList": null
			}
			""";

		// When
		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/members/captains")
			.contentType(MediaType.APPLICATION_JSON)
			.content(requestBody));

		// Then
		result
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value(GlobalErrorCode.NOT_VALID.getCode()))
			.andExpect(jsonPath("$.data[0].field").value("shipList"))
			.andExpect(jsonPath("$.data[0].reason").value("배는 최소 1개 등록해야합니다."))
			.andExpect(jsonPath("$.success").value(false));
	}

}
