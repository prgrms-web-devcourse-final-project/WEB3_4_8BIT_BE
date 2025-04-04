package com.backend.domain.fish.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.backend.domain.fish.dto.FishResponse;
import com.backend.domain.fish.exception.FishErrorCode;
import com.backend.domain.fish.exception.FishException;
import com.backend.domain.fish.service.FishService;
import com.backend.global.auth.WithMockCustomUser;
import com.backend.global.config.TestSecurityConfig;
import com.backend.global.util.BaseTest;

@WebMvcTest(FishController.class)
@ExtendWith(MockitoExtension.class)
@Import({TestSecurityConfig.class})
class FishControllerTest extends BaseTest {

	@MockitoBean
	private FishService fishService;

	@Autowired
	private MockMvc mockMvc;

	@Test
	@DisplayName("물고기 상세 조회 [Controller] - Success")
	@WithMockCustomUser
	void t01() throws Exception {
		// Given
		Long givenFishId = 1L;

		FishResponse.Detail givenDetail = fixtureMonkeyRecord
			.giveMeBuilder(FishResponse.Detail.class)
			.set("fishId", givenFishId)
			.sample();

		when(fishService.getFishDetail(givenFishId)).thenReturn(givenDetail);

		// When
		ResultActions resultActions = mockMvc.perform(get("/api/v1/fishes/{fishId}", givenFishId));

		// Then
		resultActions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.data.fishId").value(givenFishId));
	}

	@Test
	@DisplayName("물고기 상세 조회 [Not Found] [Controller] - Fail")
	@WithMockCustomUser
	void t02() throws Exception {
		// Given
		Long givenFishId = 1L;

		when(fishService.getFishDetail(givenFishId)).thenThrow(new FishException(FishErrorCode.FISH_NOT_FOUND));

		// When
		ResultActions resultActions = mockMvc.perform(get("/api/v1/fishes/{fishId}", givenFishId));

		// Then
		resultActions
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.code").value(FishErrorCode.FISH_NOT_FOUND.getCode()))
			.andExpect(jsonPath("$.message").value(FishErrorCode.FISH_NOT_FOUND.getMessage()));
	}
}