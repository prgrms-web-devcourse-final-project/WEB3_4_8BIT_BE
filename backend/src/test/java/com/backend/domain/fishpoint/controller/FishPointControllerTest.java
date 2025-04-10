package com.backend.domain.fishpoint.controller;

import static com.backend.global.util.BaseTest.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.backend.domain.fishpoint.dto.response.FishPointResponse;
import com.backend.domain.fishpoint.service.FishPointService;
import com.backend.global.auth.WithMockCustomUser;
import com.backend.global.config.TestSecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;

@Import(TestSecurityConfig.class)
@WebMvcTest(controllers = FishPointController.class)
class FishPointControllerTest {

	@MockitoBean
	private FishPointService fishPointService;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	@WithMockCustomUser
	@DisplayName("지역 기반 낚시 포인트 조회 [Controller] - Success")
	void t01() throws Exception {
		// given
		Long regionId = 13L;
		List<FishPointResponse.Basic> givenBasicList = fixtureMonkeyRecord.giveMe(FishPointResponse.Basic.class, 2);
		given(fishPointService.getFishPointsByRegionId(regionId)).willReturn(givenBasicList);

		// when
		ResultActions resultActions = mockMvc.perform(get("/api/v1/fish-points/regions/{regionId}", regionId));

		// then
		resultActions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.data.length()").value(2));
	}

	@Test
	@WithMockCustomUser
	@DisplayName("낚시 포인트 검색 [Controller] - Success")
	void t02() throws Exception {
		// given
		String region = "제주";
		List<FishPointResponse.Basic> givenBasicList = fixtureMonkeyRecord.giveMe(FishPointResponse.Basic.class, 3);
		given(fishPointService.searchFishPoints(region)).willReturn(givenBasicList);

		// when
		ResultActions resultActions = mockMvc.perform(get("/api/v1/fish-points/search")
			.param("region", region));

		// then
		resultActions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.data.length()").value(3));
	}

	@Test
	@WithMockCustomUser
	@DisplayName("인기 낚시 포인트 조회 [Controller] - Success")
	void t03() throws Exception {
		// given
		List<FishPointResponse.Popularity> givenPopularityList = fixtureMonkeyRecord.giveMe(FishPointResponse.Popularity.class, 3);
		given(fishPointService.getPopularityFishPoints()).willReturn(givenPopularityList);

		// when
		ResultActions resultActions = mockMvc.perform(get("/api/v1/fish-points/popular"));

		// then
		resultActions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.data.length()").value(3));
	}

	@Test
	@WithMockCustomUser
	@DisplayName("낚시 포인트 상세 조회 [Controller] - Success")
	void t04() throws Exception {
		// given
		Long fishPointId = 1L;
		FishPointResponse.Detail givenDetail = fixtureMonkeyRecord.giveMeOne(FishPointResponse.Detail.class);
		given(fishPointService.getFishPointDetail(fishPointId)).willReturn(givenDetail);

		// when
		ResultActions resultActions = mockMvc.perform(get("/api/v1/fish-points/{fishPointId}", fishPointId));

		// then
		resultActions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.data.fishPointName").value(givenDetail.fishPointName()));
	}
}