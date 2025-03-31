package com.backend.domain.review.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.backend.domain.review.dto.request.ReviewRequest;
import com.backend.domain.review.dto.response.ReviewWithMemberResponse;
import com.backend.domain.review.service.ReviewService;
import com.backend.global.auth.WithMockCustomUser;
import com.backend.global.config.TestSecurityConfig;
import com.backend.global.util.BaseTest;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.navercorp.fixturemonkey.ArbitraryBuilder;

@WebMvcTest(ReviewController.class)
@Import(TestSecurityConfig.class)
class ReviewControllerTest extends BaseTest {

	@MockitoBean
	private ReviewService reviewService;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	private final ArbitraryBuilder<ReviewRequest.Create> arbitraryBuilder = fixtureMonkeyRecord
		.giveMeBuilder(ReviewRequest.Create.class)
		.set("rating", 5)
		.set("content", "test content")
		.set("shipFishingPostId", 1L);

	@Test
	@DisplayName("선상 낚시 리뷰 저장 [Controller] - Success")
	@WithMockCustomUser
	void t01() throws Exception {
		// given
		Long reservationId = 1L;
		ReviewRequest.Create givenRequest = fixtureMonkeyValidation.giveMeOne(ReviewRequest.Create.class);

		Long givenSavedReviewId = 1L;
		when(reviewService.save(1L, reservationId, givenRequest)).thenReturn(givenSavedReviewId);

		// when
		ResultActions resultActions = mockMvc.perform(post("/api/v1/reservations/{reservationId}/reviews", reservationId)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(givenRequest)));

		// then
		resultActions
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.success").value(true));
	}

	@Test
	@DisplayName("선상 낚시 리뷰 저장 [rating Null] [Controller] - Fail")
	@WithMockCustomUser
	void t2() throws Exception {
		// given
		ReviewRequest.Create givenRequest = arbitraryBuilder.set("rating", null).sample();

		// when
		ResultActions resultActions = mockMvc.perform(post("/api/v1/reservations/{reservationId}/reviews", 1L)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(givenRequest)));

		// then
		resultActions
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.timestamp").exists())
			.andExpect(jsonPath("$.code").value(5001))
			.andExpect(jsonPath("$.data[0].field").value("rating"))
			.andExpect(jsonPath("$.data[0].reason").value("별점은 필수 항목입니다."))
			.andExpect(jsonPath("$.success").value(false));
	}

	@Test
	@DisplayName("선상 낚시 리뷰 저장 [rating < 1] [Controller] - Fail")
	@WithMockCustomUser
	void t3() throws Exception {
		// given
		ReviewRequest.Create givenRequest = arbitraryBuilder.set("rating", 0).sample();

		// when
		ResultActions resultActions = mockMvc.perform(post("/api/v1/reservations/{reservationId}/reviews", 1L)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(givenRequest)));

		// then
		resultActions
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.timestamp").exists())
			.andExpect(jsonPath("$.code").value(5001))
			.andExpect(jsonPath("$.data[0].field").value("rating"))
			.andExpect(jsonPath("$.data[0].reason").value("별점은 최소 1점 이상이어야 합니다."))
			.andExpect(jsonPath("$.success").value(false));
	}

	@Test
	@DisplayName("선상 낚시 리뷰 저장 [rating > 5] [Controller] - Fail")
	@WithMockCustomUser
	void t4() throws Exception {
		// given
		ReviewRequest.Create givenRequest = arbitraryBuilder.set("rating", 6).sample();

		// when
		ResultActions resultActions = mockMvc.perform(post("/api/v1/reservations/{reservationId}/reviews", 1L)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(givenRequest)));

		// then
		resultActions
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.timestamp").exists())
			.andExpect(jsonPath("$.code").value(5001))
			.andExpect(jsonPath("$.data[0].field").value("rating"))
			.andExpect(jsonPath("$.data[0].reason").value("별점은 최대 5점 이하여야 합니다."))
			.andExpect(jsonPath("$.success").value(false));
	}

	@Test
	@DisplayName("선상 낚시 리뷰 저장 [content Blank] [Controller] - Fail")
	@WithMockCustomUser
	void t5() throws Exception {
		// given
		ReviewRequest.Create givenRequest = arbitraryBuilder.set("content", " ").sample();

		// when
		ResultActions resultActions = mockMvc.perform(post("/api/v1/reservations/{reservationId}/reviews", 1L)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(givenRequest)));

		// then
		resultActions
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.timestamp").exists())
			.andExpect(jsonPath("$.code").value(5001))
			.andExpect(jsonPath("$.data[0].field").value("content"))
			.andExpect(jsonPath("$.data[0].reason").value("내용은 필수 항목입니다."))
			.andExpect(jsonPath("$.success").value(false));
	}

	@Test
	@DisplayName("선상 낚시 리뷰 저장 [shipFishingPostId Null] [Controller] - Fail")
	@WithMockCustomUser
	void t6() throws Exception {
		// given
		ReviewRequest.Create givenRequest = arbitraryBuilder.set("shipFishingPostId", null).sample();

		// when
		ResultActions resultActions = mockMvc.perform(post("/api/v1/reservations/{reservationId}/reviews", 1L)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(givenRequest)));

		// then
		resultActions
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.timestamp").exists())
			.andExpect(jsonPath("$.code").value(5001))
			.andExpect(jsonPath("$.data[0].field").value("shipFishingPostId"))
			.andExpect(jsonPath("$.data[0].reason").value("선상 낚시 게시글 ID는 필수 항목입니다."))
			.andExpect(jsonPath("$.success").value(false));
	}

	@Test
	@DisplayName("선상 낚시 리뷰 조회 [Controller] - Success")
	@WithMockCustomUser
	void t7() throws Exception {
		// given
		Long postId = 1L;
		Pageable pageable = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "createdAt"));
		List<ReviewWithMemberResponse> givenResponse = fixtureMonkeyValidation.giveMe(ReviewWithMemberResponse.class, 5);

		Page<ReviewWithMemberResponse> slicedPage =
			new PageImpl<>(givenResponse.subList(0, 3), pageable, givenResponse.size());

		when(reviewService.getReviewListByPostId(eq(postId), any(Pageable.class)))
			.thenReturn(slicedPage);

		// when
		ResultActions resultActions = mockMvc.perform(get("/api/v1/ship-posts/{postId}/reviews", postId)
			.contentType(MediaType.APPLICATION_JSON));

		// then
		resultActions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.data.content.length()").value(3));
	}

	@Test
	@DisplayName("내가 작성한 리뷰 조회 [Controller] - Success")
	@WithMockCustomUser
	void t8() throws Exception {
		// given
		Long memberId = 1L;
		Pageable pageable = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "createdAt"));
		List<ReviewWithMemberResponse> givenResponse =
			fixtureMonkeyValidation.giveMe(ReviewWithMemberResponse.class, 4);

		Page<ReviewWithMemberResponse> slicedPage =
			new PageImpl<>(givenResponse.subList(0, 3), pageable, givenResponse.size());

		when(reviewService.getReviewListByMemberId(eq(memberId), any(Pageable.class)))
			.thenReturn(slicedPage);

		// when
		ResultActions resultActions = mockMvc.perform(get("/api/v1/members/{memberId}/reviews", memberId)
			.contentType(MediaType.APPLICATION_JSON));

		// then
		resultActions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.data.content.length()").value(3));
	}
}