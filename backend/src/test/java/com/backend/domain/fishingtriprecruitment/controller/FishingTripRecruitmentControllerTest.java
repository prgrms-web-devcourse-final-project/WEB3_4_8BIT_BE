package com.backend.domain.fishingtriprecruitment.controller;

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

import com.backend.domain.fishingtrippost.exception.FishingTripPostErrorCode;
import com.backend.domain.fishingtrippost.exception.FishingTripPostException;
import com.backend.domain.fishingtriprecruitment.dto.request.FishingTripRecruitmentRequest;
import com.backend.domain.fishingtriprecruitment.service.FishingTripRecruitmentService;
import com.backend.domain.member.exception.MemberErrorCode;
import com.backend.domain.member.exception.MemberException;
import com.backend.global.auth.WithMockCustomUser;
import com.backend.global.config.TestSecurityConfig;
import com.backend.global.exception.GlobalErrorCode;
import com.backend.global.util.BaseTest;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.navercorp.fixturemonkey.ArbitraryBuilder;

@WebMvcTest(FishingTripRecruitmentController.class)
@Import(TestSecurityConfig.class)
@ExtendWith(MockitoExtension.class)
class FishingTripRecruitmentControllerTest extends BaseTest {

	@MockitoBean
	private FishingTripRecruitmentService fishingTripRecruitmentService;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	private final ArbitraryBuilder<FishingTripRecruitmentRequest.Create> createBuilder =
		fixtureMonkeyBuilder.giveMeBuilder(FishingTripRecruitmentRequest.Create.class)
			.set("fishingTripPostId", 1L)
			.set("introduction", "열심히 하겠습니다!")
			.set("fishingLevel", "BEGINNER");

	@Test
	@DisplayName("동출 모집 신청 [Controller] - Success")
	@WithMockCustomUser
	void t01() throws Exception {
		// Given
		FishingTripRecruitmentRequest.Create requestDto = createBuilder.sample();
		Long savedId = 1L;

		when(fishingTripRecruitmentService.createFishingTripRecruitment(anyLong(), any()))
			.thenReturn(savedId);

		// When
		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/fishing-trip-recruitment")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(requestDto)));

		// Then
		result
			.andExpect(status().isCreated())
			.andExpect(header().string("Location", savedId.toString()))
			.andExpect(jsonPath("$.success").value(true));
	}

	@Test
	@DisplayName("동출 모집 신청 실패 [MEMBER_NOT_FOUND] [Controller] - Fail")
	@WithMockCustomUser
	void t02() throws Exception {
		// Given
		FishingTripRecruitmentRequest.Create requestDto = createBuilder.sample();

		doThrow(new MemberException(MemberErrorCode.MEMBER_NOT_FOUND))
			.when(fishingTripRecruitmentService).createFishingTripRecruitment(anyLong(), any());

		// When
		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/fishing-trip-recruitment")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(requestDto)));

		// Then
		result
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value(MemberErrorCode.MEMBER_NOT_FOUND.getCode()))
			.andExpect(jsonPath("$.message").value(MemberErrorCode.MEMBER_NOT_FOUND.getMessage()))
			.andExpect(jsonPath("$.success").value(false));
	}

	@Test
	@DisplayName("동출 모집 신청 실패 [FISHING_TRIP_POST_NOT_FOUND] [Controller] - Fail")
	@WithMockCustomUser
	void t03() throws Exception {
		// Given
		FishingTripRecruitmentRequest.Create requestDto = createBuilder
			.set("fishingTripPostId", 999L)
			.sample();

		doThrow(new FishingTripPostException(FishingTripPostErrorCode.FISHING_TRIP_POST_NOT_FOUND))
			.when(fishingTripRecruitmentService).createFishingTripRecruitment(anyLong(), any());

		// When
		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/fishing-trip-recruitment")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(requestDto)));

		// Then
		result
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value(FishingTripPostErrorCode.FISHING_TRIP_POST_NOT_FOUND.getCode()))
			.andExpect(jsonPath("$.message").value(FishingTripPostErrorCode.FISHING_TRIP_POST_NOT_FOUND.getMessage()))
			.andExpect(jsonPath("$.success").value(false));
	}

	@Test
	@DisplayName("동출 모집 신청 실패 [introduction null] [Controller] - Fail")
	@WithMockCustomUser
	void t04() throws Exception {
		// Given
		FishingTripRecruitmentRequest.Create requestDto = createBuilder
			.set("introduction", null)
			.sample();

		// When
		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/fishing-trip-recruitment")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(requestDto)));

		// Then
		result
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value(GlobalErrorCode.NOT_VALID.getCode()))
			.andExpect(jsonPath("$.data[0].field").value("introduction"))
			.andExpect(jsonPath("$.data[0].reason").value("소개글은 필수입니다."))
			.andExpect(jsonPath("$.success").value(false));
	}

	@Test
	@DisplayName("동출 모집 신청 실패 [INVALID_FISHING_LEVEL] [Controller] - Fail")
	@WithMockCustomUser
	void t05() throws Exception {
		// Given
		FishingTripRecruitmentRequest.Create requestDto = createBuilder
			.set("fishingLevel", "UNKNOWN")
			.sample();

		// When
		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/fishing-trip-recruitment")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(requestDto)));

		// Then
		result
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value(GlobalErrorCode.NOT_VALID.getCode()))
			.andExpect(jsonPath("$.message").value(GlobalErrorCode.NOT_VALID.getMessage()))
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.data[0].field").value("fishingLevel"))
			.andExpect(jsonPath("$.data[0].reason").value("올바른 낚시 실력을 입력해주세요."));
	}

}
