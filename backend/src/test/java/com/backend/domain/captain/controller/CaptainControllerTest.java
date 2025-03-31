package com.backend.domain.captain.controller;

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
import com.backend.domain.captain.service.CaptainService;
import com.backend.global.auth.WithMockCustomUser;
import com.backend.global.config.TestSecurityConfig;
import com.backend.global.util.BaseTest;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.navercorp.fixturemonkey.ArbitraryBuilder;

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
			.andExpect(jsonPath("$.code").value(5001))
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
			.andExpect(jsonPath("$.code").value(5001))
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
			.andExpect(jsonPath("$.code").value(5001))
			.andExpect(jsonPath("$.data[0].field").value("nickname"))
			.andExpect(jsonPath("$.data[0].reason").value("닉네임은 최대 30자까지 가능합니다."))
			.andExpect(jsonPath("$.success").value(false));
	}
}
