package com.backend.domain.ship.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;

import com.backend.domain.ship.dto.request.ShipRequest;
import com.backend.domain.ship.service.ShipService;
import com.backend.global.auth.WithMockCustomUser;
import com.backend.global.config.TestSecurityConfig;
import com.backend.global.util.BaseTest;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.navercorp.fixturemonkey.ArbitraryBuilder;

@WebMvcTest(ShipController.class)
@ExtendWith(MockitoExtension.class)
@Import({TestSecurityConfig.class})
class ShipControllerTest extends BaseTest {

	@MockitoBean
	private ShipService shipService;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	private final Arbitrary<String> englishStringLength = Arbitraries.strings()
		.withCharRange('a', 'z')
		.withCharRange('A', 'Z')
		.ofMinLength(1).ofMaxLength(10);

	private final ArbitraryBuilder<ShipRequest.Create> createArbitraryBuilder = fixtureMonkeyRecord
		.giveMeBuilder(ShipRequest.Create.class)
		.set("shipName", englishStringLength)
		.set("shipNumber", englishStringLength)
		.set("departurePort", englishStringLength)
		.set("passengerCapacity", 50);

	@Test
	@DisplayName("선박 저장 [Controller] - Success")
	@WithMockCustomUser
	void t01() throws Exception {
		// Given
		Long shipId = 1L;
		ShipRequest.Create givenRequestDto = createArbitraryBuilder.sample();

		when(shipService.createShip(1L, givenRequestDto)).thenReturn(shipId);
		// When

		ResultActions resultActions = mockMvc.perform(post("/api/v1/ship")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(givenRequestDto)));

		// Then
		resultActions
			.andExpect(status().isCreated())
			.andExpect(header().exists("Location"))
			.andExpect(header().string("Location", shipId.toString()));
	}
}