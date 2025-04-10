package com.backend.domain.comment.controller;

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

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;

import com.backend.domain.comment.dto.request.CommentRequest;
import com.backend.domain.comment.exception.CommentErrorCode;
import com.backend.domain.comment.exception.CommentExpection;
import com.backend.domain.comment.service.CommentService;
import com.backend.domain.fishingtrippost.exception.FishingTripPostErrorCode;
import com.backend.global.auth.WithMockCustomUser;
import com.backend.global.config.TestSecurityConfig;
import com.backend.global.exception.GlobalErrorCode;
import com.backend.global.util.BaseTest;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.navercorp.fixturemonkey.ArbitraryBuilder;

@WebMvcTest(CommentController.class)
@ExtendWith(MockitoExtension.class)
@Import({TestSecurityConfig.class})
class CommentControllerTest extends BaseTest {

	@MockitoBean
	private CommentService commentService;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	private final Arbitrary<String> englishStringLength = Arbitraries.strings()
		.withCharRange('a', 'z')
		.withCharRange('A', 'Z')
		.ofMinLength(1).ofMaxLength(10);

	private final ArbitraryBuilder<CommentRequest.Create> createArbitraryBuilder = fixtureMonkeyRecord
		.giveMeBuilder(CommentRequest.Create.class)
		.set("content", englishStringLength);

	@Test
	@DisplayName("댓글 저장 [Controller] - Success")
	@WithMockCustomUser
	void t01() throws Exception {
		// Given
		Long givenFishingTripPostId = 1L;
		Long givenSavedCommentId = 1L;
		CommentRequest.Create givenRequestDto = createArbitraryBuilder.sample();

		when(commentService.createComment(givenFishingTripPostId, 1L, givenRequestDto))
			.thenReturn(givenSavedCommentId);

		// When
		ResultActions resultActions = mockMvc
			.perform(MockMvcRequestBuilders.post(
					"/api/v1/fishing-trip-post/{fishingTripPostId}/comment",
					givenFishingTripPostId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(givenRequestDto)));

		// Then
		resultActions
			.andExpect(status().isCreated())
			.andExpect(header().string("Location", "1"))
			.andExpect(jsonPath("$.success").value(true));
	}

	@Test
	@DisplayName("댓글 저장 [Content] [Not Blank] [Controller] - Fail")
	@WithMockCustomUser
	void t02() throws Exception {
		// Given
		Long givenFishingTripPostId = 1L;
		CommentRequest.Create givenRequestDto = createArbitraryBuilder
			.set("content", null)
			.sample();

		// When
		ResultActions resultActions = mockMvc
			.perform(MockMvcRequestBuilders.post(
					"/api/v1/fishing-trip-post/{fishingTripPostId}/comment",
					givenFishingTripPostId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(givenRequestDto)));

		// Then
		resultActions
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.timestamp").exists())
			.andExpect(jsonPath("$.code").value(GlobalErrorCode.NOT_VALID.getCode()))
			.andExpect(jsonPath("$.data[0].field").value("content"))
			.andExpect(jsonPath("$.data[0].reason").value("내용은 필수 항목입니다."))
			.andExpect(jsonPath("$.message").value("요청하신 유효성 검증에 실패하였습니다."))
			.andExpect(jsonPath("$.success").value(false));
	}

	@Test
	@DisplayName("댓글 저장 [Content] [Empty String] [Controller] - Fail")
	@WithMockCustomUser
	void t03() throws Exception {
		// Given
		Long givenFishingTripPostId = 1L;
		CommentRequest.Create givenRequestDto = createArbitraryBuilder
			.set("content", "")
			.sample();

		// When
		ResultActions resultActions = mockMvc
			.perform(MockMvcRequestBuilders.post(
					"/api/v1/fishing-trip-post/{fishingTripPostId}/comment",
					givenFishingTripPostId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(givenRequestDto)));
		// Then
		resultActions
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.timestamp").exists())
			.andExpect(jsonPath("$.code").value(GlobalErrorCode.NOT_VALID.getCode()))
			.andExpect(jsonPath("$.data[0].field").value("content"))
			.andExpect(jsonPath("$.data[0].reason").value("내용은 1자 이상 100자 이하여야 합니다."))
			.andExpect(jsonPath("$.message").value("요청하신 유효성 검증에 실패하였습니다."))
			.andExpect(jsonPath("$.success").value(false));
	}

	@Test
	@DisplayName("댓글 저장 [Content] [Blank String] [Controller] - Fail")
	@WithMockCustomUser
	void t04() throws Exception {
		// Given
		Long givenFishingTripPostId = 1L;
		CommentRequest.Create givenRequestDto = createArbitraryBuilder
			.set("content", "   ")
			.sample();

		// When
		ResultActions resultActions = mockMvc
			.perform(MockMvcRequestBuilders.post(
					"/api/v1/fishing-trip-post/{fishingTripPostId}/comment",
					givenFishingTripPostId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(givenRequestDto)));
		// Then
		resultActions
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.timestamp").exists())
			.andExpect(jsonPath("$.code").value(GlobalErrorCode.NOT_VALID.getCode()))
			.andExpect(jsonPath("$.data[0].field").value("content"))
			.andExpect(jsonPath("$.data[0].reason").value("내용은 필수 항목입니다."))
			.andExpect(jsonPath("$.message").value("요청하신 유효성 검증에 실패하였습니다."))
			.andExpect(jsonPath("$.success").value(false));
	}

	@Test
	@DisplayName("댓글 저장 [FishingTripPost Not Found] [Controller] - Fail")
	@WithMockCustomUser
	void t06() throws Exception {
		// Given
		Long givenFishingTripPostId = 1L;
		CommentRequest.Create givenRequestDto = createArbitraryBuilder.sample();

		// Mock service to throw exception for invalid post ID
		doThrow(new CommentExpection(FishingTripPostErrorCode.FISHING_TRIP_POST_NOT_FOUND))
			.when(commentService)
			.createComment(givenFishingTripPostId, 1L, givenRequestDto);

		// When
		ResultActions resultActions = mockMvc
			.perform(MockMvcRequestBuilders.post(
					"/api/v1/fishing-trip-post/{fishingTripPostId}/comment",
					givenFishingTripPostId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(givenRequestDto)));

		// Then
		resultActions
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.timestamp").exists())
			.andExpect(jsonPath("$.code").value(FishingTripPostErrorCode.FISHING_TRIP_POST_NOT_FOUND.getCode()))
			.andExpect(jsonPath("$.message").value(FishingTripPostErrorCode.FISHING_TRIP_POST_NOT_FOUND.getMessage()))
			.andExpect(jsonPath("$.success").value(false));
	}

	@Test
	@DisplayName("댓글 저장 [Parent Not Found] [Controller] - Fail")
	@WithMockCustomUser
	void t07() throws Exception {
		// Given
		Long givenFishingTripPostId = 1L;
		CommentRequest.Create givenRequestDto = createArbitraryBuilder.sample();

		doThrow(new CommentExpection(CommentErrorCode.PARENT_NOT_FOUND))
			.when(commentService)
			.createComment(givenFishingTripPostId, 1L, givenRequestDto);

		// When
		ResultActions resultActions = mockMvc
			.perform(MockMvcRequestBuilders.post(
					"/api/v1/fishing-trip-post/{fishingTripPostId}/comment",
					givenFishingTripPostId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(givenRequestDto)));

		// Then
		resultActions
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.timestamp").exists())
			.andExpect(jsonPath("$.code").value(CommentErrorCode.PARENT_NOT_FOUND.getCode()))
			.andExpect(jsonPath("$.message").value(CommentErrorCode.PARENT_NOT_FOUND.getMessage()))
			.andExpect(jsonPath("$.success").value(false));
	}
}