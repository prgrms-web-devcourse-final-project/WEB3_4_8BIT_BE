package com.backend.domain.fishencyclopedia.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

import com.backend.domain.fishencyclopedia.dto.request.FishEncyclopediaRequest;
import com.backend.domain.fishencyclopedia.service.FishEncyclopediaService;
import com.backend.global.util.BaseTest;
import com.backend.global.auth.WithMockCustomUser;
import com.backend.global.config.TestSecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.navercorp.fixturemonkey.ArbitraryBuilder;

@WebMvcTest(FishEncyclopediaController.class)
@ExtendWith(MockitoExtension.class)
@Import({TestSecurityConfig.class})
public class FishEncyclopediaControllerTest extends BaseTest {

	@MockitoBean
	private FishEncyclopediaService fishEncyclopediaService;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	final ArbitraryBuilder<FishEncyclopediaRequest.Create> arbitraryBuilder = fixtureMonkeyRecord
		.giveMeBuilder(FishEncyclopediaRequest.Create.class)
		.set("fishId", 1L)
		.set("length", 10)
		.set("fishPointId", 1L);

	@Test
	@DisplayName("물고기 도감 저장 [Controller] - Success")
	@WithMockCustomUser
	void t01() throws Exception {
		// Given
		FishEncyclopediaRequest.Create givenCreate = fixtureMonkeyValidation.giveMeOne(
			FishEncyclopediaRequest.Create.class
		);

		Long givenSaveFishEncyclopediaId = 1L;
		when(fishEncyclopediaService.createFishEncyclopedia(givenCreate, 1L)).thenReturn(givenSaveFishEncyclopediaId);

		// When
		ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/fish/encyclopedias")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(givenCreate)));

		// Then
		resultActions
			.andExpect(status().isCreated())
			.andExpect(header().exists("Location"))
			.andExpect(header().string("Location", givenSaveFishEncyclopediaId.toString()))
			.andExpect(jsonPath("$.success").value(true));
	}

	@Test
	@DisplayName("물고기 도감 저장 [fishId Null] [Controller] - Fail")
	@WithMockCustomUser
	void t02() throws Exception {
		// Given
		FishEncyclopediaRequest.Create givenCreate = arbitraryBuilder.set("fishId", null).sample();

		// When
		ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/fish/encyclopedias")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(givenCreate)));

		// Then
		resultActions
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.timestamp").exists())
			.andExpect(jsonPath("$.code").value(5001))
			.andExpect(jsonPath("$.data[0].field").value("fishId"))
			.andExpect(jsonPath("$.data[0].reason").value("물고기 ID는 필수 항목입니다."))
			.andExpect(jsonPath("$.message").value("요청하신 유효성 검증에 실패하였습니다."))
			.andExpect(jsonPath("$.success").value(false));
	}

	@Test
	@DisplayName("물고기 도감 저장 [length Null] [Controller] - Fail")
	@WithMockCustomUser
	void t03() throws Exception {
		// Given
		FishEncyclopediaRequest.Create givenCreate = arbitraryBuilder.set("length", null).sample();

		// When
		ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/fish/encyclopedias")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(givenCreate)));

		// Then
		resultActions
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.timestamp").exists())
			.andExpect(jsonPath("$.code").value(5001))
			.andExpect(jsonPath("$.data[0].field").value("length"))
			.andExpect(jsonPath("$.data[0].reason").value("물고기 길이는 필수 항목입니다."))
			.andExpect(jsonPath("$.message").value("요청하신 유효성 검증에 실패하였습니다."))
			.andExpect(jsonPath("$.success").value(false));
	}

	@Test
	@DisplayName("물고기 도감 저장 [length Min] [Controller] - Fail")
	@WithMockCustomUser
	void t04() throws Exception {
		// Given
		FishEncyclopediaRequest.Create givenCreate = arbitraryBuilder.set("length", 0).sample();

		// When
		ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/fish/encyclopedias")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(givenCreate)));

		// Then
		resultActions
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.timestamp").exists())
			.andExpect(jsonPath("$.code").value(5001))
			.andExpect(jsonPath("$.data[0].field").value("length"))
			.andExpect(jsonPath("$.data[0].reason").value("물고기 길이는 1cm 이상이어야 합니다."))
			.andExpect(jsonPath("$.message").value("요청하신 유효성 검증에 실패하였습니다."))
			.andExpect(jsonPath("$.success").value(false));
	}

	@Test
	@DisplayName("물고기 도감 저장 [fishPointId Null] [Controller] - Fail")
	@WithMockCustomUser
	void t05() throws Exception {
		// Given
		FishEncyclopediaRequest.Create givenCreate = arbitraryBuilder.set("fishPointId", null).sample();

		// When
		ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/fish/encyclopedias")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(givenCreate)));

		// Then
		resultActions
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.timestamp").exists())
			.andExpect(jsonPath("$.code").value(5001))
			.andExpect(jsonPath("$.data[0].field").value("fishPointId"))
			.andExpect(jsonPath("$.data[0].reason").value("낚시 포인트 ID는 필수 항목입니다."))
			.andExpect(jsonPath("$.message").value("요청하신 유효성 검증에 실패하였습니다."))
			.andExpect(jsonPath("$.success").value(false));
	}
}
