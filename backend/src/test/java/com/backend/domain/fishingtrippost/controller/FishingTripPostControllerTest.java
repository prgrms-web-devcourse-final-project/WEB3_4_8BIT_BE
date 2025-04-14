package com.backend.domain.fishingtrippost.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

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

import com.backend.domain.fishingtrippost.domain.PostStatus;
import com.backend.domain.fishingtrippost.dto.request.FishingTripPostRequest;
import com.backend.domain.fishingtrippost.dto.response.FishingTripPostResponse;
import com.backend.domain.fishingtrippost.exception.FishingTripPostErrorCode;
import com.backend.domain.fishingtrippost.exception.FishingTripPostException;
import com.backend.domain.fishingtrippost.service.FishingTripPostService;
import com.backend.domain.fishpoint.exception.FishPointErrorCode;
import com.backend.domain.fishpoint.exception.FishPointException;
import com.backend.domain.member.exception.MemberErrorCode;
import com.backend.domain.member.exception.MemberException;
import com.backend.domain.region.entity.RegionType;
import com.backend.global.auth.WithMockCustomUser;
import com.backend.global.config.TestSecurityConfig;
import com.backend.global.dto.response.ScrollResponse;
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

	final ArbitraryBuilder<FishingTripPostRequest.Create> arbitraryBuilder = fixtureMonkeyBuilder
		.giveMeBuilder(FishingTripPostRequest.Create.class)
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
		FishingTripPostRequest.Create requestDto = arbitraryBuilder.sample();
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
		FishingTripPostRequest.Create requestDto = arbitraryBuilder.sample();

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
		FishingTripPostRequest.Create requestDto = arbitraryBuilder.sample();

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
		FishingTripPostRequest.Create requestDto = arbitraryBuilder.set("subject", null).sample();

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
		FishingTripPostRequest.Create requestDto = arbitraryBuilder.set("fishingPointId", null).sample();

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
		Long memberId = 1L;

		List<Long> updatedFileIds = List.of(10L, 20L);
		FishingTripPostRequest.Create requestDto = FishingTripPostRequest.Create.builder()
			.subject("수정된 제목")
			.content("수정된 내용")
			.recruitmentCount(3)
			.isShipFish(true)
			.fishingDate(ZonedDateTime.now().plusDays(3))
			.fishingPointId(99L)
			.regionId(2L)
			.fileIdList(updatedFileIds)
			.build();

		when(fishingTripPostService.updateFishingTripPost(eq(memberId), eq(postId), any()))
			.thenReturn(requestDto.fishingPointId());

		// When
		ResultActions result = mockMvc.perform(
			MockMvcRequestBuilders.patch("/api/v1/fishing-trip-post/{fishingTripPostId}", postId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto))
		);

		// Then
		result
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.data").value(requestDto.fishingPointId()));
	}

	@Test
	@DisplayName("동출 게시글 수정 [FISHING_TRIP_POST_NOT_FOUND] [Controller] - Fail")
	@WithMockCustomUser
	void t07() throws Exception {
		// Given
		Long postId = 999L;
		FishingTripPostRequest.Create requestDto = arbitraryBuilder.sample();

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
		FishingTripPostRequest.Create requestDto = arbitraryBuilder.sample();

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
			.andExpect(
				jsonPath("$.code").value(FishingTripPostErrorCode.FISHING_TRIP_POST_UNAUTHORIZED_AUTHOR.getCode()))
			.andExpect(jsonPath("$.message").value(
				FishingTripPostErrorCode.FISHING_TRIP_POST_UNAUTHORIZED_AUTHOR.getMessage()))
			.andExpect(jsonPath("$.success").value(false));
	}

	@Test
	@DisplayName("동출 게시글 수정 [subject null] [Controller] - Fail")
	@WithMockCustomUser
	void t09() throws Exception {
		// Given
		Long postId = 1L;
		FishingTripPostRequest.Create requestDto = arbitraryBuilder.set("subject", null).sample();

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

	@Test
	@DisplayName("동출 게시글 상세 조회 [Controller] - Success")
	@WithMockCustomUser // 이 어노테이션이 memberId를 1L로 설정한다고 가정
	void t10() throws Exception {
		// Given
		Long postId = 1L;
		Long memberId = 1L;

		Map<Long, String> fileUrlMap = Map.of(
			101L, "https://cdn.example.com/1.jpg",
			102L, "https://cdn.example.com/2.jpg",
			103L, "https://cdn.example.com/3.jpg"
		);

		FishingTripPostResponse.Detail responseDto = FishingTripPostResponse.Detail.builder()
			.fishingTripPostId(postId)
			.name("루피")
			.subject("같이 갑시다")
			.content("초보 환영")
			.currentCount(1)
			.recruitmentCount(5)
			.createDate(ZonedDateTime.parse("2025-04-01T12:00:00+09:00"))
			.fishingDate(ZonedDateTime.parse("2025-04-10T06:00:00+09:00"))
			.fishPointDetailName("남해 앞바다")
			.fishPointName("남해")
			.longitude(128.12345)
			.latitude(37.12345)
			.fileUrlList(fileUrlMap)
			.likeCount(12L)
			.isLiked(true)
			.postStatus(PostStatus.RECRUITING)
			.isPostOwner(false)
			.build();

		when(fishingTripPostService.getFishingTripPostDetail(eq(memberId), eq(postId))).thenReturn(responseDto);

		// When
		ResultActions result = mockMvc.perform(
			MockMvcRequestBuilders.get("/api/v1/fishing-trip-post")
				.param("id", postId.toString())
				.accept(MediaType.APPLICATION_JSON)
		);

		// Then
		result
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.data.fishingTripPostId").value(postId))
			.andExpect(jsonPath("$.data.name").value("루피"))
			.andExpect(jsonPath("$.data.subject").value("같이 갑시다"))
			.andExpect(jsonPath("$.data.content").value("초보 환영"))
			.andExpect(jsonPath("$.data.currentCount").value(1))
			.andExpect(jsonPath("$.data.recruitmentCount").value(5))
			.andExpect(jsonPath("$.data.createDate").value("2025-04-01T12:00:00+09:00"))
			.andExpect(jsonPath("$.data.fishingDate").value("2025-04-10T06:00:00+09:00"))
			.andExpect(jsonPath("$.data.fishPointDetailName").value("남해 앞바다"))
			.andExpect(jsonPath("$.data.fishPointName").value("남해"))
			.andExpect(jsonPath("$.data.longitude").value(128.12345))
			.andExpect(jsonPath("$.data.latitude").value(37.12345))
			.andExpect(jsonPath("$.data.fileUrlList.101").value("https://cdn.example.com/1.jpg"))
			.andExpect(jsonPath("$.data.fileUrlList.102").value("https://cdn.example.com/2.jpg"))
			.andExpect(jsonPath("$.data.fileUrlList.103").value("https://cdn.example.com/3.jpg"))
			.andExpect(jsonPath("$.data.likeCount").value(12))
			.andExpect(jsonPath("$.data.isLiked").value(true))
			.andExpect(jsonPath("$.data.postStatus").value("RECRUITING"))
			.andExpect(jsonPath("$.data.isPostOwner").value(false));
	}

	@Test
	@DisplayName("동출 게시글 상세 조회 [FISHING_TRIP_POST_NOT_FOUND] [Controller] - Fail")
	@WithMockCustomUser
	void t11() throws Exception {
		// Given
		Long postId = 999L;
		Long memberId = 1L; // @WithMockCustomUser로 들어오는 memberId

		doThrow(new FishingTripPostException(FishingTripPostErrorCode.FISHING_TRIP_POST_NOT_FOUND))
			.when(fishingTripPostService).getFishingTripPostDetail(eq(memberId), eq(postId));

		// When
		ResultActions result = mockMvc.perform(
			MockMvcRequestBuilders.get("/api/v1/fishing-trip-post")
				.param("id", postId.toString())
				.accept(MediaType.APPLICATION_JSON)
		);

		// Then
		result
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value(FishingTripPostErrorCode.FISHING_TRIP_POST_NOT_FOUND.getCode()))
			.andExpect(jsonPath("$.message").value(FishingTripPostErrorCode.FISHING_TRIP_POST_NOT_FOUND.getMessage()))
			.andExpect(jsonPath("$.success").value(false));
	}

	@Test
	@DisplayName("동출 게시글 모집 완료 처리 [Controller] - Success")
	@WithMockCustomUser
	void t12() throws Exception {
		// Given
		Long postId = 100L;
		Long memberId = 1L;

		// When
		ResultActions result = mockMvc.perform(
			MockMvcRequestBuilders.patch("/api/v1/fishing-trip-post/{fishingTripPostId}/completed", postId)
				.contentType(MediaType.APPLICATION_JSON)
		);

		// Then
		result
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true));

		verify(fishingTripPostService).completeFishingTripPost(eq(memberId), eq(postId));
	}

	@Test
	@DisplayName("동출 게시글 모집 완료 처리 [UNAUTHORIZED_AUTHOR] [Controller] - Fail")
	@WithMockCustomUser
	void t13() throws Exception {
		// Given
		Long postId = 100L;

		doThrow(new FishingTripPostException(FishingTripPostErrorCode.FISHING_TRIP_POST_UNAUTHORIZED_AUTHOR))
			.when(fishingTripPostService).completeFishingTripPost(anyLong(), eq(postId));

		// When
		ResultActions result = mockMvc.perform(
			MockMvcRequestBuilders.patch("/api/v1/fishing-trip-post/{fishingTripPostId}/completed", postId)
				.contentType(MediaType.APPLICATION_JSON)
		);

		// Then
		result
			.andExpect(status().isForbidden())
			.andExpect(jsonPath("$.code").value(FishingTripPostErrorCode.FISHING_TRIP_POST_UNAUTHORIZED_AUTHOR.getCode()))
			.andExpect(jsonPath("$.message").value(FishingTripPostErrorCode.FISHING_TRIP_POST_UNAUTHORIZED_AUTHOR.getMessage()))
			.andExpect(jsonPath("$.success").value(false));
	}

	@Test
	@DisplayName("동출 게시글 모집 완료 처리 [POST_NOT_FOUND] [Controller] - Fail")
	@WithMockCustomUser
	void t14() throws Exception {
		// Given
		Long postId = 999L;

		doThrow(new FishingTripPostException(FishingTripPostErrorCode.FISHING_TRIP_POST_NOT_FOUND))
			.when(fishingTripPostService).completeFishingTripPost(anyLong(), eq(postId));

		// When
		ResultActions result = mockMvc.perform(
			MockMvcRequestBuilders.patch("/api/v1/fishing-trip-post/{fishingTripPostId}/completed", postId)
				.contentType(MediaType.APPLICATION_JSON)
		);

		// Then
		result
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value(FishingTripPostErrorCode.FISHING_TRIP_POST_NOT_FOUND.getCode()))
			.andExpect(jsonPath("$.message").value(FishingTripPostErrorCode.FISHING_TRIP_POST_NOT_FOUND.getMessage()))
			.andExpect(jsonPath("$.success").value(false));
	}

	// @Test
	// @WithMockCustomUser
	// @DisplayName("동출 게시글 스크롤 조회 [Controller] - Success")
	// void t15() throws Exception {
	// 	// Given
	// 	Long postId = 1L;
	//
	// 	FishingTripPostResponse.DetailPage detailPage = FishingTripPostResponse.DetailPage.builder()
	// 		.fishingTripPostId(postId)
	// 		.regionType(null)
	// 		.subject("스크롤 제목")
	// 		.content("스크롤 내용")
	// 		.fishingDate(ZonedDateTime.parse("2025-06-10T08:00:00+09:00"))
	// 		.createdAt(ZonedDateTime.parse("2025-04-09T04:00:00+09:00"))
	// 		.recruitmentCount(5)
	// 		.postStatus(PostStatus.RECRUITING)
	// 		.imageUrl("https://cdn.example.com/file.jpg")
	// 		.build();
	//
	// 	ScrollResponse<FishingTripPostResponse.DetailPage> response = ScrollResponse.from(
	// 		List.of(detailPage), 10, 1, true, true
	// 	);
	//
	// 	when(fishingTripPostService.getDetailPage(any(), isNull(), isNull(), isNull(), isNull()))
	// 		.thenReturn(response);
	//
	// 	// When
	// 	ResultActions result = mockMvc.perform(
	// 		MockMvcRequestBuilders.get("/api/v1/fishing-trip-post/scroll")
	// 			.param("order", "createdAt")
	// 			.param("sort", "desc")
	// 			.param("type", "next")
	// 			.param("fieldValue", "2025-04-09T04:00:00+09:00")
	// 			.param("id", postId.toString())
	// 			.param("size", "10")
	// 	);
	//
	// 	// Then
	// 	result.andExpect(status().isOk())
	// 		.andExpect(jsonPath("$.success").value(true))
	// 		.andExpect(jsonPath("$.data.content").isArray())
	// 		.andExpect(jsonPath("$.data.content[0].fishingTripPostId").value(postId))
	// 		.andExpect(jsonPath("$.data.content[0].imageUrl").value("https://cdn.example.com/file.jpg"))
	// 		.andExpect(jsonPath("$.data.pageSize").value(10))
	// 		.andExpect(jsonPath("$.data.numberOfElements").value(1))
	// 		.andExpect(jsonPath("$.data.isFirst").value(true))
	// 		.andExpect(jsonPath("$.data.isLast").value(true));
	// }

	@Test
	@WithMockCustomUser
	@DisplayName("동출 게시글 참여자 정보 조회 [Controller] - Success")
	void t16() throws Exception {
		// Given
		Long postId = 1L;
		Long memberId = 1L;

		FishingTripPostResponse.FishingTripPostParticipationDetail responseDto =
			new FishingTripPostResponse.FishingTripPostParticipationDetail(
				postId,
				5,
				2,
				PostStatus.RECRUITING,
				true,
				false,
				99L, // postOwnerId
				"루피", // ownerNickname
				"https://cdn.example.com/루피.jpg", // ownerProfileImageUrl
				List.of(
					new FishingTripPostResponse.ParticipantDetail(10L, "참가자1", "https://cdn.example.com/참가자1.jpg"),
					new FishingTripPostResponse.ParticipantDetail(11L, "참가자2", "https://cdn.example.com/참가자2.jpg")
				)
			);

		when(fishingTripPostService.getFishingTripPostParticipationDetail(eq(memberId), eq(postId)))
			.thenReturn(responseDto);

		// When
		ResultActions result = mockMvc.perform(
			MockMvcRequestBuilders.get("/api/v1/fishing-trip-post/participation")
				.param("fishingTripPostId", postId.toString())
				.accept(MediaType.APPLICATION_JSON)
		);

		// Then
		result
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.data.fishingTripPostId").value(postId))
			.andExpect(jsonPath("$.data.recruitmentCount").value(5))
			.andExpect(jsonPath("$.data.currentCount").value(2))
			.andExpect(jsonPath("$.data.postStatus").value("RECRUITING"))
			.andExpect(jsonPath("$.data.isApplicant").value(true))
			.andExpect(jsonPath("$.data.isCurrentUserOwner").value(false))
			.andExpect(jsonPath("$.data.postOwnerId").value(99))
			.andExpect(jsonPath("$.data.ownerNickname").value("루피"))
			.andExpect(jsonPath("$.data.ownerProfileImageUrl").value("https://cdn.example.com/루피.jpg"))
			.andExpect(jsonPath("$.data.participants[0].nickname").value("참가자1"))
			.andExpect(jsonPath("$.data.participants[0].profileImageUrl").value("https://cdn.example.com/참가자1.jpg"))
			.andExpect(jsonPath("$.data.participants[1].nickname").value("참가자2"))
			.andExpect(jsonPath("$.data.participants[1].profileImageUrl").value("https://cdn.example.com/참가자2.jpg"));
	}

	@Test
	@WithMockCustomUser
	@DisplayName("내가 신청한 동출 게시글 스크롤 조회 [Controller] - Success")
	void t17() throws Exception {
		// Given
		Long postId = 100L;
		Long memberId = 1L;

		FishingTripPostResponse.MyFishingTripPostDetailPage dto1 =
			new FishingTripPostResponse.MyFishingTripPostDetailPage(
				postId,
				"같이 갑시다",
				1L,
				"남해",
				"남해 앞바다",
				ZonedDateTime.parse("2025-06-10T08:00:00+09:00"),
				ZonedDateTime.parse("2025-04-09T04:00:00+09:00"),
				1,
				5,
				PostStatus.RECRUITING,
				12L,
				3L
			);

		ScrollResponse<FishingTripPostResponse.MyFishingTripPostDetailPage> response =
			ScrollResponse.from(List.of(dto1), 10, 1, true, true);

		when(fishingTripPostService.getMyFishingTripPostDetailPage(any(), eq(memberId), eq(PostStatus.RECRUITING)))
			.thenReturn(response);

		// When
		ResultActions result = mockMvc.perform(
			MockMvcRequestBuilders.get("/api/v1/fishing-trip-post/my-participate")
				.param("order", "createdAt")
				.param("sort", "desc")
				.param("type", "next")
				.param("status", "RECRUITING")
				.param("fieldValue", "2025-04-09T04:00:00+09:00")
				.param("id", postId.toString())
				.param("size", "10")
				.accept(MediaType.APPLICATION_JSON)
		);

		// Then
		result.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.data.content[0].fishingTripPostId").value(postId))
			.andExpect(jsonPath("$.data.content[0].subject").value("같이 갑시다"))
			.andExpect(jsonPath("$.data.content[0].fishingPointId").value(1L))
			.andExpect(jsonPath("$.data.content[0].fishingPointName").value("남해"))
			.andExpect(jsonPath("$.data.content[0].fishingPointDetailName").value("남해 앞바다"))
			.andExpect(jsonPath("$.data.content[0].postStatus").value("RECRUITING"))
			.andExpect(jsonPath("$.data.content[0].commentCount").value(12))
			.andExpect(jsonPath("$.data.content[0].likeCount").value(3))
			.andExpect(jsonPath("$.data.pageSize").value(10))
			.andExpect(jsonPath("$.data.numberOfElements").value(1))
			.andExpect(jsonPath("$.data.isFirst").value(true))
			.andExpect(jsonPath("$.data.isLast").value(true));
	}

	@Test
	@WithMockCustomUser
	@DisplayName("내가 작성한 동출 게시글 스크롤 조회 [Controller] - Success")
	void t18() throws Exception {
		// Given
		Long postId = 100L;
		Long memberId = 1L;

		FishingTripPostResponse.MyFishingTripPostDetailPage dto1 =
			new FishingTripPostResponse.MyFishingTripPostDetailPage(
				postId,
				"같이 갑시다",
				1L,
				"남해",
				"남해 앞바다",
				ZonedDateTime.parse("2025-06-10T08:00:00+09:00"),
				ZonedDateTime.parse("2025-04-09T04:00:00+09:00"),
				1,
				5,
				PostStatus.RECRUITING,
				12L,
				3L
			);

		ScrollResponse<FishingTripPostResponse.MyFishingTripPostDetailPage> response =
			ScrollResponse.from(List.of(dto1), 10, 1, true, true);

		when(fishingTripPostService.getMyPostFishingTripPostDetailPage(any(), eq(memberId), eq(PostStatus.RECRUITING)))
			.thenReturn(response);

		// When
		ResultActions result = mockMvc.perform(
			MockMvcRequestBuilders.get("/api/v1/fishing-trip-post/my-post")
				.param("order", "createdAt")
				.param("sort", "desc")
				.param("type", "next")
				.param("status", "RECRUITING")
				.param("fieldValue", "2025-04-09T04:00:00+09:00")
				.param("id", postId.toString())
				.param("size", "10")
				.accept(MediaType.APPLICATION_JSON)
		);

		// Then
		result.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.data.content[0].fishingTripPostId").value(postId))
			.andExpect(jsonPath("$.data.content[0].subject").value("같이 갑시다"))
			.andExpect(jsonPath("$.data.content[0].fishingPointId").value(1L))
			.andExpect(jsonPath("$.data.content[0].fishingPointName").value("남해"))
			.andExpect(jsonPath("$.data.content[0].fishingPointDetailName").value("남해 앞바다"))
			.andExpect(jsonPath("$.data.content[0].postStatus").value("RECRUITING"))
			.andExpect(jsonPath("$.data.content[0].commentCount").value(12))
			.andExpect(jsonPath("$.data.content[0].likeCount").value(3))
			.andExpect(jsonPath("$.data.pageSize").value(10))
			.andExpect(jsonPath("$.data.numberOfElements").value(1))
			.andExpect(jsonPath("$.data.isFirst").value(true))
			.andExpect(jsonPath("$.data.isLast").value(true));
	}

	@Test
	@WithMockCustomUser
	@DisplayName("HOT 동출 게시글 조회 [Controller] - Success")
	void t19() throws Exception {
		// Given
		List<FishingTripPostResponse.HotPost> hotPosts = List.of(
			new FishingTripPostResponse.HotPost(
				100L,
				"지려버린 낚시",
				1L,
				RegionType.JEJU,
				"https://cdn.example.com/image1.jpg",
				30L
			),
			new FishingTripPostResponse.HotPost(
				101L,
				"혼자 낚시 금지",
				2L,
				RegionType.SEOUL,
				null, // 이미지 없는 케이스
				27L
			)
		);

		when(fishingTripPostService.getHotPost()).thenReturn(hotPosts);

		// When
		ResultActions result = mockMvc.perform(
			MockMvcRequestBuilders.get("/api/v1/fishing-trip-post/hot-post")
				.accept(MediaType.APPLICATION_JSON)
		);

		// Then
		result
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.data").isArray())
			.andExpect(jsonPath("$.data[0].fishingTripPostId").value(100L))
			.andExpect(jsonPath("$.data[0].subject").value("지려버린 낚시"))
			.andExpect(jsonPath("$.data[0].regionId").value(1L))
			.andExpect(jsonPath("$.data[0].regionType").value("JEJU"))
			.andExpect(jsonPath("$.data[0].imageUrl").value("https://cdn.example.com/image1.jpg"))
			.andExpect(jsonPath("$.data[0].hotScore").value(30))
			.andExpect(jsonPath("$.data[1].fishingTripPostId").value(101L))
			.andExpect(jsonPath("$.data[1].imageUrl").doesNotExist()) // null 처리
			.andExpect(jsonPath("$.data[1].hotScore").value(27));
	}
}