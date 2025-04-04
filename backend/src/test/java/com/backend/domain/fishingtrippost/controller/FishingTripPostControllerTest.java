package com.backend.domain.fishingtrippost.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;
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

import com.backend.domain.fishingtrippost.dto.request.FishingTripPostRequest;
import com.backend.domain.fishingtrippost.exception.FishingTripPostErrorCode;
import com.backend.domain.fishingtrippost.exception.FishingTripPostException;
import com.backend.domain.fishingtrippost.service.FishingTripPostService;
import com.backend.domain.fishpoint.exception.FishPointErrorCode;
import com.backend.domain.fishpoint.exception.FishPointException;
import com.backend.domain.member.exception.MemberErrorCode;
import com.backend.domain.member.exception.MemberException;
import com.backend.global.auth.WithMockCustomUser;
import com.backend.global.config.TestSecurityConfig;
import com.backend.global.exception.GlobalErrorCode;
import com.backend.global.util.BaseTest;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

import com.navercorp.fixturemonkey.ArbitraryBuilder;

@Slf4j
@WebMvcTest(FishingTripPostController.class)
@ExtendWith(MockitoExtension.class)
@Import(TestSecurityConfig.class)
class FishingTripPostControllerTest extends BaseTest {

	@MockitoBean
	private FishingTripPostService fishingTripPostService;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	final ArbitraryBuilder<FishingTripPostRequest.Form> arbitraryBuilder = fixtureMonkeyBuilder
		.giveMeBuilder(FishingTripPostRequest.Form.class)
		.set("subject", "동출 구합니다!")
		.set("content", "다같이 낚시가요~")
		.set("recruitmentCount", 5)
		.set("currentCount", 0)
		.set("isShipFish", false)
		.set("fishingDate", ZonedDateTime.of(2025, 6, 10, 8, 0, 0, 0, ZoneId.of("Asia/Seoul")))
		.set("fishingPointId", 1L)
		.set("fileIdList", List.of(1L, 2L));

	@Test
	@DisplayName("동출 게시글 저장 [Controller] - Success")
	@WithMockCustomUser
	void t01() throws Exception {
		FishingTripPostRequest.Form requestDto = arbitraryBuilder.sample();
		Long savedId = 0L;

		when(fishingTripPostService.createFishingTripPost(1L, requestDto)).thenReturn(savedId);

		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/fishing-trip-post")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(requestDto)));

		result
			.andExpect(status().isCreated())
			.andExpect(header().string("Location", savedId.toString()))
			.andExpect(jsonPath("$.success").value(true));
	}

	@Test
	@DisplayName("동출 게시글 저장 [존재하지 않는 Member] [Controller] - Fail")
	@WithMockCustomUser
	void t02() throws Exception {
		FishingTripPostRequest.Form requestDto = arbitraryBuilder.sample();

		doThrow(new MemberException(MemberErrorCode.MEMBER_NOT_FOUND))
			.when(fishingTripPostService).createFishingTripPost(anyLong(), any());

		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/fishing-trip-post")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(requestDto)));

		result.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value(MemberErrorCode.MEMBER_NOT_FOUND.getCode()))
			.andExpect(jsonPath("$.message").value(MemberErrorCode.MEMBER_NOT_FOUND.getMessage()))
			.andExpect(jsonPath("$.success").value(false));
	}

	@Test
	@DisplayName("동출 게시글 저장 [존재하지 않는 FishPoint] [Controller] - Fail")
	@WithMockCustomUser
	void t03() throws Exception {
		FishingTripPostRequest.Form requestDto = arbitraryBuilder.sample();

		doThrow(new FishPointException(FishPointErrorCode.FISH_POINT_NOT_FOUND))
			.when(fishingTripPostService).createFishingTripPost(anyLong(), any());

		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/fishing-trip-post")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(requestDto)));

		result.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value(FishPointErrorCode.FISH_POINT_NOT_FOUND.getCode()))
			.andExpect(jsonPath("$.message").value(FishPointErrorCode.FISH_POINT_NOT_FOUND.getMessage()))
			.andExpect(jsonPath("$.success").value(false));
	}

	@Test
	@DisplayName("동출 게시글 저장 [subject null] [Controller] - Fail")
	@WithMockCustomUser
	void t04() throws Exception {
		FishingTripPostRequest.Form requestDto = arbitraryBuilder.set("subject", null).sample();

		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/fishing-trip-post")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(requestDto)));

		result.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value(GlobalErrorCode.NOT_VALID.getCode()))
			.andExpect(jsonPath("$.data[0].field").value("subject"))
			.andExpect(jsonPath("$.success").value(false));
	}

	@Test
	@DisplayName("동출 게시글 저장 [fishingPointId null] [Controller] - Fail")
	@WithMockCustomUser
	void t05() throws Exception {
		FishingTripPostRequest.Form requestDto = arbitraryBuilder.set("fishingPointId", null).sample();

		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/fishing-trip-post")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(requestDto)));

		result.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value(GlobalErrorCode.NOT_VALID.getCode()))
			.andExpect(jsonPath("$.data[0].field").value("fishingPointId"))
			.andExpect(jsonPath("$.success").value(false));
	}

	@Test
	@DisplayName("동출 게시글 수정 [Controller] - Success")
	@WithMockCustomUser
	void t06() throws Exception {
		// Given
		Long postId = 123L;
		FishingTripPostRequest.Form requestDto = arbitraryBuilder.sample();

		when(fishingTripPostService.updateFishingTripPost(anyLong(), eq(postId), any()))
			.thenReturn(postId);

		// When
		ResultActions result = mockMvc.perform(
			MockMvcRequestBuilders.patch("/api/v1/fishing-trip-post/{fishingTripPostId}", postId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto)));

		// Then
		result
			.andExpect(status().isCreated())
			.andExpect(header().string("Location", postId.toString()))
			.andExpect(jsonPath("$.success").value(true));
	}

	@Test
	@DisplayName("동출 게시글 수정 [FISHING_TRIP_POST_NOT_FOUND] [Controller] - Fail")
	@WithMockCustomUser
	void t07() throws Exception {
		// Given
		Long postId = 999L;
		FishingTripPostRequest.Form requestDto = arbitraryBuilder.sample();

		doThrow(new FishingTripPostException(FishingTripPostErrorCode.FISHING_TRIP_POST_NOT_FOUND))
			.when(fishingTripPostService).updateFishingTripPost(anyLong(), eq(postId), any());

		// When
		ResultActions result = mockMvc.perform(
			MockMvcRequestBuilders.patch("/api/v1/fishing-trip-post/{fishingTripPostId}", postId)
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
	@DisplayName("동출 게시글 수정 [UNAUTHORIZED_AUTHOR] [Controller] - Fail")
	@WithMockCustomUser
	void t08() throws Exception {
		// Given
		Long postId = 999L;
		FishingTripPostRequest.Form requestDto = arbitraryBuilder.sample();

		doThrow(new FishingTripPostException(FishingTripPostErrorCode.FISHING_TRIP_POST_UNAUTHORIZED_AUTHOR))
			.when(fishingTripPostService).updateFishingTripPost(anyLong(), eq(postId), any());

		// When
		ResultActions result = mockMvc.perform(
			MockMvcRequestBuilders.patch("/api/v1/fishing-trip-post/{fishingTripPostId}", postId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto)));

		// Then
		result
			.andExpect(status().isForbidden())
			.andExpect(jsonPath("$.code").value(FishingTripPostErrorCode.FISHING_TRIP_POST_UNAUTHORIZED_AUTHOR.getCode()))
			.andExpect(jsonPath("$.message").value(FishingTripPostErrorCode.FISHING_TRIP_POST_UNAUTHORIZED_AUTHOR.getMessage()))
			.andExpect(jsonPath("$.success").value(false));
	}

	@Test
	@DisplayName("동출 게시글 수정 [subject null] [Controller] - Fail")
	@WithMockCustomUser
	void t09() throws Exception {
		// Given
		Long postId = 1L;
		FishingTripPostRequest.Form requestDto = arbitraryBuilder.set("subject", null).sample();

		// When
		ResultActions result = mockMvc.perform(
			MockMvcRequestBuilders.patch("/api/v1/fishing-trip-post/{fishingTripPostId}", postId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto)));

		// Then
		result
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value(GlobalErrorCode.NOT_VALID.getCode()))
			.andExpect(jsonPath("$.data[0].field").value("subject"))
			.andExpect(jsonPath("$.success").value(false));
	}

}
