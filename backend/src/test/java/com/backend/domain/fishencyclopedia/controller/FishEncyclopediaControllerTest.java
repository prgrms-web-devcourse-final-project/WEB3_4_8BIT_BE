package com.backend.domain.fishencyclopedia.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.backend.domain.fish.entity.Fish;
import com.backend.domain.fishencyclopedia.dto.request.FishEncyclopediaRequest;
import com.backend.domain.fishencyclopedia.dto.response.FishEncyclopediaResponse;
import com.backend.domain.fishencyclopedia.service.FishEncyclopediaService;
import com.backend.global.auth.WithMockCustomUser;
import com.backend.global.config.TestSecurityConfig;
import com.backend.global.dto.GlobalRequest;
import com.backend.global.exception.GlobalErrorCode;
import com.backend.global.util.BaseTest;
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
		.set("count", 10)
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
		ResultActions resultActions = mockMvc.perform(post("/api/v1/fishes/encyclopedias")
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
		ResultActions resultActions = mockMvc.perform(post("/api/v1/fishes/encyclopedias")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(givenCreate)));

		// Then
		resultActions
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.timestamp").exists())
			.andExpect(jsonPath("$.code").value(GlobalErrorCode.NOT_VALID.getCode()))
			.andExpect(jsonPath("$.data[0].field").value("fishId"))
			.andExpect(jsonPath("$.data[0].reason").value("물고기 ID는 필수 항목입니다."))
			.andExpect(jsonPath("$.message").value(GlobalErrorCode.NOT_VALID.getMessage()))
			.andExpect(jsonPath("$.success").value(false));
	}

	@Test
	@DisplayName("물고기 도감 저장 [length Null] [Controller] - Fail")
	@WithMockCustomUser
	void t03() throws Exception {
		// Given
		FishEncyclopediaRequest.Create givenCreate = arbitraryBuilder.set("length", null).sample();

		// When
		ResultActions resultActions = mockMvc.perform(post("/api/v1/fishes/encyclopedias")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(givenCreate)));

		// Then
		resultActions
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.timestamp").exists())
			.andExpect(jsonPath("$.code").value(GlobalErrorCode.NOT_VALID.getCode()))
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
		ResultActions resultActions = mockMvc.perform(post("/api/v1/fishes/encyclopedias")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(givenCreate)));

		// Then
		resultActions
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.timestamp").exists())
			.andExpect(jsonPath("$.code").value(GlobalErrorCode.NOT_VALID.getCode()))
			.andExpect(jsonPath("$.data[0].field").value("length"))
			.andExpect(jsonPath("$.data[0].reason").value("물고기 길이는 1cm 이상이어야 합니다."))
			.andExpect(jsonPath("$.message").value(GlobalErrorCode.NOT_VALID.getMessage()))
			.andExpect(jsonPath("$.success").value(false));
	}

	@Test
	@DisplayName("물고기 도감 저장 [fishPointId Null] [Controller] - Fail")
	@WithMockCustomUser
	void t05() throws Exception {
		// Given
		FishEncyclopediaRequest.Create givenCreate = arbitraryBuilder.set("fishPointId", null).sample();

		// When
		ResultActions resultActions = mockMvc.perform(post("/api/v1/fishes/encyclopedias")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(givenCreate)));

		// Then
		resultActions
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.timestamp").exists())
			.andExpect(jsonPath("$.code").value(GlobalErrorCode.NOT_VALID.getCode()))
			.andExpect(jsonPath("$.data[0].field").value("fishPointId"))
			.andExpect(jsonPath("$.data[0].reason").value("낚시 포인트 ID는 필수 항목입니다."))
			.andExpect(jsonPath("$.message").value(GlobalErrorCode.NOT_VALID.getMessage()))
			.andExpect(jsonPath("$.success").value(false));
	}

	@Test
	@DisplayName("물고기 도감 상세 조회 [Controller] - Success")
	@WithMockCustomUser
	void t06() throws Exception {
		// Given
		GlobalRequest.PageRequest givenPageRequest = fixtureMonkeyRecord
			.giveMeBuilder(GlobalRequest.PageRequest.class)
			.set("size", 10)
			.set("page", 0)
			.sample();

		Fish givenFish = fixtureMonkeyBuilder.giveMeOne(Fish.class);

		List<FishEncyclopediaResponse.Detail> givenDetailList = fixtureMonkeyRecord
			.giveMeBuilder(FishEncyclopediaResponse.Detail.class)
			.sampleList(7);

		Pageable givenPageable = PageRequest.of(givenPageRequest.page(), givenPageRequest.size());

		boolean givenHasNext = false;

		SliceImpl<FishEncyclopediaResponse.Detail> givenSlice = new SliceImpl<>(
			givenDetailList,
			givenPageable,
			givenHasNext
		);

		when(fishEncyclopediaService.getDetailList(givenPageRequest, givenFish.getFishId(), 1L))
			.thenReturn(givenSlice);

		// When
		ResultActions resultActions = mockMvc.perform(get("/api/v1/fishes/{fishId}/encyclopedias", givenFish.getFishId())
			.param("page", givenPageRequest.page().toString())
			.param("size", givenPageRequest.size().toString())
			.param("sort", givenPageRequest.sort())
			.param("order", givenPageRequest.order()));

		// Then
		resultActions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.data.content.size()").value(7))
			.andExpect(jsonPath("$.data.numberOfElements").value(7))
			.andExpect(jsonPath("$.data.pageable.pageSize").value(givenPageRequest.size()))
			.andExpect(jsonPath("$.data.pageable.pageNumber").value(givenPageRequest.page()));
	}

	@Test
	@DisplayName("물고기 도감 상세 조회 [Size Min] [Controller] - Fail")
	@WithMockCustomUser
	void t07() throws Exception {
		// Given
		GlobalRequest.PageRequest givenPageRequest = fixtureMonkeyRecord
			.giveMeBuilder(GlobalRequest.PageRequest.class)
			.set("size", -1)
			.set("page", 0)
			.sample();

		Fish givenFish = fixtureMonkeyBuilder.giveMeOne(Fish.class);

		// When
		ResultActions resultActions = mockMvc.perform(get("/api/v1/fishes/{fishId}/encyclopedias", givenFish.getFishId())
			.param("page", givenPageRequest.page().toString())
			.param("size", givenPageRequest.size().toString())
			.param("sort", givenPageRequest.sort())
			.param("order", givenPageRequest.order()));

		// Then
		resultActions
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.timestamp").exists())
			.andExpect(jsonPath("$.code").value(GlobalErrorCode.NOT_VALID.getCode()))
			.andExpect(jsonPath("$.data[0].field").value("size"))
			.andExpect(jsonPath("$.data[0].reason").value("페이지 사이즈는 0 이상이어야 합니다."))
			.andExpect(jsonPath("$.message").value(GlobalErrorCode.NOT_VALID.getMessage()))
			.andExpect(jsonPath("$.success").value(false));
	}

	@Test
	@DisplayName("물고기 도감 상세 조회 [Page Min] [Controller] - Fail")
	@WithMockCustomUser
	void t08() throws Exception {
		// Given
		GlobalRequest.PageRequest givenPageRequest = fixtureMonkeyRecord
			.giveMeBuilder(GlobalRequest.PageRequest.class)
			.set("size", 1)
			.set("page", -1)
			.sample();

		Fish givenFish = fixtureMonkeyBuilder.giveMeOne(Fish.class);

		// When
		ResultActions resultActions = mockMvc.perform(get("/api/v1/fishes/{fishId}/encyclopedias", givenFish.getFishId())
			.param("page", givenPageRequest.page().toString())
			.param("size", givenPageRequest.size().toString())
			.param("sort", givenPageRequest.sort())
			.param("order", givenPageRequest.order()));

		// Then
		resultActions
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.timestamp").exists())
			.andExpect(jsonPath("$.code").value(GlobalErrorCode.NOT_VALID.getCode()))
			.andExpect(jsonPath("$.data[0].field").value("page"))
			.andExpect(jsonPath("$.data[0].reason").value("페이지 번호는 0 이상이어야 합니다."))
			.andExpect(jsonPath("$.message").value(GlobalErrorCode.NOT_VALID.getMessage()))
			.andExpect(jsonPath("$.success").value(false));
	}
}
