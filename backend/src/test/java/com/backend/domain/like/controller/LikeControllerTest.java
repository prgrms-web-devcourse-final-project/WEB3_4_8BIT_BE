package com.backend.domain.like.controller;

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

import com.backend.domain.fishingtrippost.exception.FishingTripPostErrorCode;
import com.backend.domain.fishingtrippost.exception.FishingTripPostException;
import com.backend.domain.like.domain.LikeTargetType;
import com.backend.domain.like.dto.request.LikeRequest;
import com.backend.domain.like.dto.response.LikeResponse;
import com.backend.domain.like.service.LikeService;
import com.backend.domain.shipfishingpost.exception.ShipFishingPostErrorCode;
import com.backend.domain.shipfishingpost.exception.ShipFishingPostException;
import com.backend.global.auth.WithMockCustomUser;
import com.backend.global.config.TestSecurityConfig;
import com.backend.global.dto.response.ScrollResponse;
import com.backend.global.util.BaseTest;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(LikeController.class)
@ExtendWith(MockitoExtension.class)
@Import(TestSecurityConfig.class)
class LikeControllerTest extends BaseTest {

	@MockitoBean
	private LikeService likeService;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	@WithMockCustomUser
	@DisplayName("좋아요 토글 [Controller] - Success")
	void t01() throws Exception {

		LikeRequest givenRequestDto = fixtureMonkeyBuilder.giveMeBuilder(LikeRequest.class)
			.set("targetType", LikeTargetType.FISHING_TRIP_POST)
			.set("targetId", 1L)
			.sample();

		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/likes/toggle")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(givenRequestDto)));

		result.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true));

		verify(likeService).toggleLike(eq(1L), any());
	}

	@Test
	@WithMockCustomUser
	@DisplayName("좋아요 토글 [대상 없음] [Controller] - Fail")
	void t02() throws Exception {
		LikeRequest request = new LikeRequest(LikeTargetType.FISHING_TRIP_POST, 999L);

		doThrow(new FishingTripPostException(FishingTripPostErrorCode.FISHING_TRIP_POST_NOT_FOUND))
			.when(likeService).toggleLike(eq(1L), any());

		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/likes/toggle")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)));

		result.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.code").value(FishingTripPostErrorCode.FISHING_TRIP_POST_NOT_FOUND.getCode()))
			.andExpect(jsonPath("$.message").value(FishingTripPostErrorCode.FISHING_TRIP_POST_NOT_FOUND.getMessage()));
	}

	@Test
	@WithMockCustomUser
	@DisplayName("좋아요한 동출 모집 게시글 조회 [Controller] - Success")
	void t03() throws Exception {
		LikeResponse.FishingTripPostLikedDetailResponse dto = LikeResponse.FishingTripPostLikedDetailResponse.builder()
			.fishingTripPostId(1L)
			.subject("테스트")
			.regionId(100L)
			.regionType(null)
			.content("내용")
			.recruitmentCount(5)
			.postStatus(null)
			.imageUrl("https://cdn.example.com/test.jpg")
			.commentCount(10L)
			.likeCount(5L)
			.build();
		ScrollResponse<LikeResponse.FishingTripPostLikedDetailResponse> response =
			ScrollResponse.from(List.of(dto), 10, 1, true, true);

		when(likeService.getLikedFishingTripPosts(any(), eq(1L))).thenReturn(response);

		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/likes/fishing-trip-post")
			.param("size", "10"));

		result.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.data.content[0].fishingTripPostId").value(1L))
			.andExpect(jsonPath("$.data.content[0].subject").value("테스트"))
			.andExpect(jsonPath("$.data.content[0].regionId").value(100L))
			.andExpect(jsonPath("$.data.content[0].content").value("내용"))
			.andExpect(jsonPath("$.data.content[0].recruitmentCount").value(5))
			.andExpect(jsonPath("$.data.content[0].imageUrl").value("https://cdn.example.com/test.jpg"))
			.andExpect(jsonPath("$.data.content[0].commentCount").value(10))
			.andExpect(jsonPath("$.data.content[0].likeCount").value(5))
			.andExpect(jsonPath("$.data.pageSize").value(10))
			.andExpect(jsonPath("$.data.numberOfElements").value(1))
			.andExpect(jsonPath("$.data.isFirst").value(true))
			.andExpect(jsonPath("$.data.isLast").value(true));
	}

	@Test
	@WithMockCustomUser
	@DisplayName("좋아요한 동출 모집 게시글 조회 [게시글 없음] [Controller] - Fail")
	void t04() throws Exception {
		when(likeService.getLikedFishingTripPosts(any(), eq(1L)))
			.thenThrow(new FishingTripPostException(FishingTripPostErrorCode.FISHING_TRIP_POST_NOT_FOUND));

		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/likes/fishing-trip-post")
			.param("size", "10"));

		result.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.code").value(FishingTripPostErrorCode.FISHING_TRIP_POST_NOT_FOUND.getCode()))
			.andExpect(jsonPath("$.message").value(FishingTripPostErrorCode.FISHING_TRIP_POST_NOT_FOUND.getMessage()));
	}

	@Test
	@WithMockCustomUser
	@DisplayName("좋아요한 선상 낚시 게시글 조회 [Controller] - Success")
	void t05() throws Exception {
		LikeResponse.ShipFishingPostLikedDetailResponse dto = LikeResponse.ShipFishingPostLikedDetailResponse.builder()
			.shipFishingPostId(1L)
			.subject("테스트")
			.location("부산")
			.price(80000L)
			.fileUrl("https://cdn.example.com/ship.jpg")
			.fishNameList(List.of("참돔", "우럭"))
			.reviewEverRate(4.5)
			.reviewCount(12L)
			.likeCount(7L)
			.build();
		ScrollResponse<LikeResponse.ShipFishingPostLikedDetailResponse> response =
			ScrollResponse.from(List.of(dto), 10, 1, true, true);

		when(likeService.getLikedShipFishingPosts(any(), eq(1L))).thenReturn(response);

		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/likes/ship-fishing-post")
			.param("size", "10"));

		result.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.data.content[0].shipFishingPostId").value(1L))
			.andExpect(jsonPath("$.data.content[0].subject").value("테스트"))
			.andExpect(jsonPath("$.data.content[0].location").value("부산"))
			.andExpect(jsonPath("$.data.content[0].price").value(80000))
			.andExpect(jsonPath("$.data.content[0].fileUrl").value("https://cdn.example.com/ship.jpg"))
			.andExpect(jsonPath("$.data.content[0].fishNameList[0]").value("참돔"))
			.andExpect(jsonPath("$.data.content[0].fishNameList[1]").value("우럭"))
			.andExpect(jsonPath("$.data.content[0].reviewEverRate").value(4.5))
			.andExpect(jsonPath("$.data.content[0].reviewCount").value(12))
			.andExpect(jsonPath("$.data.content[0].likeCount").value(7))
			.andExpect(jsonPath("$.data.pageSize").value(10))
			.andExpect(jsonPath("$.data.numberOfElements").value(1))
			.andExpect(jsonPath("$.data.isFirst").value(true))
			.andExpect(jsonPath("$.data.isLast").value(true));
	}

	@Test
	@WithMockCustomUser
	@DisplayName("좋아요한 선상 낚시 게시글 조회 [게시글 없음] [Controller] - Fail")
	void t06() throws Exception {
		when(likeService.getLikedShipFishingPosts(any(), eq(1L)))
			.thenThrow(new ShipFishingPostException(ShipFishingPostErrorCode.POSTS_NOT_FOUND));

		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/likes/ship-fishing-post")
			.param("size", "10"));

		result.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.code").value(ShipFishingPostErrorCode.POSTS_NOT_FOUND.getCode()))
			.andExpect(jsonPath("$.message").value(ShipFishingPostErrorCode.POSTS_NOT_FOUND.getMessage()));
	}
}