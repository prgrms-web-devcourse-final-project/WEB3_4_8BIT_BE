package com.backend.domain.fishencyclopedia.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.backend.domain.fishencyclopedia.dto.request.FishEncyclopediaRequest;
import com.backend.domain.fishencyclopedia.service.FishEncyclopediaService;
import com.backend.global.Util.BaseTest;
import com.backend.global.auth.WithMockCustomUser;
import com.backend.global.config.TestSecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.navercorp.fixturemonkey.ArbitraryBuilder;

@WebMvcTest(FishEncyclopediaController.class)
@ExtendWith(MockitoExtension.class)
@Import({TestSecurityConfig.class})
public class FishEncyclopediaControllerTest extends BaseTest {

	@MockitoBean
	private FishEncyclopediaService fishEncyclopediaService;

	@InjectMocks
	private FishEncyclopediaController fishEncyclopediaController;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	final ArbitraryBuilder<FishEncyclopediaRequest.Create> arbitraryBuilder = fixtureMonkeyRecord
		.giveMeBuilder(FishEncyclopediaRequest.Create.class)
		.set("fishId", 1L)
		.set("length", 10)
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
		when(fishEncyclopediaService.save(givenCreate, 1L)).thenReturn(givenSaveFishEncyclopediaId);

		// When
		ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/fish/encyclopedias")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(givenCreate)));

		// Then
		resultActions
			.andExpect(status().isCreated())
			.andExpect(header().exists("Location"))
			.andExpect(header().string("Location", givenSaveFishEncyclopediaId.toString()))
			.andExpect(jsonPath("$.success").value(true));
	}


}
