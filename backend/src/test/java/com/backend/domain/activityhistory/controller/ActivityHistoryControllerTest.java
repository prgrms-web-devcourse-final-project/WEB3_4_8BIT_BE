package com.backend.domain.activityhistory.controller;

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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.backend.domain.activityhistory.dto.request.ActivityHistoryRequest;
import com.backend.domain.activityhistory.dto.response.ActivityHistoryResponse;
import com.backend.domain.activityhistory.service.ActivityHistoryService;
import com.backend.global.auth.WithMockCustomUser;
import com.backend.global.config.TestSecurityConfig;
import com.backend.global.dto.request.GlobalRequest;
import com.backend.global.dto.response.ScrollResponse;
import com.backend.global.util.BaseTest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@WebMvcTest(ActivityHistoryController.class)
@ExtendWith(MockitoExtension.class)
@Import(TestSecurityConfig.class)
public class ActivityHistoryControllerTest extends BaseTest {

	@MockitoBean
	private ActivityHistoryService activityHistoryService;

	@Autowired
	private MockMvc mockMvc;

	@Test
	@DisplayName("최근 활동 내역 전체 조회 [Controller] - Success")
	@WithMockCustomUser
	void t01() throws Exception {
		Long givenMemberId = 1L;

		GlobalRequest.CursorRequest givenCursorRequestDto = new GlobalRequest.CursorRequest(
			null,
			null,
			null,
			null,
			null,
			10
		);

		ActivityHistoryRequest.Search givenRequestDto = new ActivityHistoryRequest.Search(null);

		List<ActivityHistoryResponse.Detail> givenDetailList = fixtureMonkeyRecord
			.giveMeBuilder(ActivityHistoryResponse.Detail.class)
			.sampleList(10);

		ScrollResponse<ActivityHistoryResponse.Detail> givenScrollResponse = fixtureMonkeyRecord
			.giveMeBuilder(ScrollResponse.class)
			.set("content", givenDetailList)
			.sample();

		when(activityHistoryService.getDetailList(givenCursorRequestDto, givenRequestDto, givenMemberId))
			.thenReturn(givenScrollResponse);

		// When
		ResultActions resultActions = mockMvc.perform(get("/api/v1/activity-histories")
			.param("size", "10"));

		// Then
		resultActions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.data.content.size()").value(10));
	}

	@Test
	@DisplayName("최근 활동 내역 전체 조회 [Valid Enum] [Controller] - Fail")
	@WithMockCustomUser
	void t02() throws Exception {
		Long givenMemberId = 1L;

		GlobalRequest.CursorRequest givenCursorRequestDto = new GlobalRequest.CursorRequest(
			null,
			null,
			null,
			null,
			null,
			10
		);

		ActivityHistoryRequest.Search givenRequestDto = new ActivityHistoryRequest.Search(null);

		List<ActivityHistoryResponse.Detail> givenDetailList = fixtureMonkeyRecord
			.giveMeBuilder(ActivityHistoryResponse.Detail.class)
			.sampleList(10);

		ScrollResponse<ActivityHistoryResponse.Detail> givenScrollResponse = fixtureMonkeyRecord
			.giveMeBuilder(ScrollResponse.class)
			.set("content", givenDetailList)
			.sample();

		when(activityHistoryService.getDetailList(givenCursorRequestDto, givenRequestDto, givenMemberId))
			.thenReturn(givenScrollResponse);

		// When
		ResultActions resultActions = mockMvc.perform(get("/api/v1/activity-histories")
			.param("size", "10")
			.param("activityType", "test"));

		// Then
		resultActions
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.success").value(false));
	}
}
