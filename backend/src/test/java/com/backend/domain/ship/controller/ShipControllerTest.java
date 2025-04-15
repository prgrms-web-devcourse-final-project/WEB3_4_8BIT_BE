package com.backend.domain.ship.controller;

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
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;

import com.backend.domain.ship.domain.RestroomType;
import com.backend.domain.ship.dto.request.ShipRequest;
import com.backend.domain.ship.dto.response.ShipResponse;
import com.backend.domain.ship.exception.ShipErrorCode;
import com.backend.domain.ship.exception.ShipException;
import com.backend.domain.ship.service.ShipService;
import com.backend.global.auth.WithMockCustomUser;
import com.backend.global.config.TestSecurityConfig;
import com.backend.global.exception.GlobalErrorCode;
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

	private final ArbitraryBuilder<ShipRequest.Form> createArbitraryBuilder = fixtureMonkeyRecord
		.giveMeBuilder(ShipRequest.Form.class)
		.set("shipName", englishStringLength)
		.set("shipNumber", englishStringLength)
		.set("departurePort", "제주특별자치도 제주시 애월읍 애월리 407-4")
		.set("portName","제주 애월항")
		.set("passengerCapacity", 50);

	@Test
	@DisplayName("선박 저장 [Controller] - Success")
	@WithMockCustomUser
	void t01() throws Exception {
		// Given
		Long shipId = 1L;
		ShipRequest.Form givenRequestDto = createArbitraryBuilder.sample();

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

	@Test
	@DisplayName("선박 저장 [ShipName - Size] [Controller] - Fail")
	@WithMockCustomUser
	void t02() throws Exception {
		// Given
		Long shipId = 1L;
		ShipRequest.Form givenRequestDto = createArbitraryBuilder
			.set("shipName", "oiesfajoiejfioewafjoijwaoiefjwaoiefjiowjafiowjaofijwoaiefowaiefjoiwafjowaif")
			.sample();

		// When
		ResultActions resultActions = mockMvc.perform(post("/api/v1/ship")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(givenRequestDto)));

		// Then
		resultActions
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.timestamp").exists())
			.andExpect(jsonPath("$.code").value(GlobalErrorCode.NOT_VALID.getCode()))
			.andExpect(jsonPath("$.data[0].field").value("shipName"))
			.andExpect(jsonPath("$.data[0].reason").value("선박 이름은 30자 이하여야 합니다."))
			.andExpect(jsonPath("$.message").value("요청하신 유효성 검증에 실패하였습니다."))
			.andExpect(jsonPath("$.success").value(false));
	}

	@Test
	@DisplayName("선박 저장 [ShipName - NotBlank] [Controller] - Fail")
	@WithMockCustomUser
	void t03() throws Exception {
		// Given
		Long shipId = 1L;
		ShipRequest.Form givenRequestDto = createArbitraryBuilder
			.set("shipName", "")
			.sample();

		// When
		ResultActions resultActions = mockMvc.perform(post("/api/v1/ship")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(givenRequestDto)));

		// Then
		resultActions
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.timestamp").exists())
			.andExpect(jsonPath("$.code").value(GlobalErrorCode.NOT_VALID.getCode()))
			.andExpect(jsonPath("$.data[0].field").value("shipName"))
			.andExpect(jsonPath("$.data[0].reason").value("선박 이름은 필수 항목입니다."))
			.andExpect(jsonPath("$.message").value("요청하신 유효성 검증에 실패하였습니다."))
			.andExpect(jsonPath("$.success").value(false));
	}

	@Test
	@DisplayName("선박 저장 [ShipNumber - Size] [Controller] - Fail")
	@WithMockCustomUser
	void t04() throws Exception {
		// Given
		ShipRequest.Form givenRequestDto = createArbitraryBuilder
			.set("shipNumber", "oiesfajoiejfioewafjoijwaoiefjwaoiefjiowjafiowjaofijwoaiefowaiefjoiwafjowaif")
			.sample();
		// When
		ResultActions resultActions = mockMvc.perform(post("/api/v1/ship")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(givenRequestDto)));
		// Then
		resultActions
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.timestamp").exists())
			.andExpect(jsonPath("$.code").value(GlobalErrorCode.NOT_VALID.getCode()))
			.andExpect(jsonPath("$.data[0].field").value("shipNumber"))
			.andExpect(jsonPath("$.data[0].reason").value("선박 번호는 30자 이하여야 합니다."))
			.andExpect(jsonPath("$.message").value("요청하신 유효성 검증에 실패하였습니다."))
			.andExpect(jsonPath("$.success").value(false));
	}

	@Test
	@DisplayName("선박 저장 [ShipNumber - NotBlank] [Controller] - Fail")
	@WithMockCustomUser
	void t05() throws Exception {
		// Given
		ShipRequest.Form givenRequestDto = createArbitraryBuilder
			.set("shipNumber", "")
			.sample();
		// When
		ResultActions resultActions = mockMvc.perform(post("/api/v1/ship")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(givenRequestDto)));
		// Then
		resultActions
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.timestamp").exists())
			.andExpect(jsonPath("$.code").value(GlobalErrorCode.NOT_VALID.getCode()))
			.andExpect(jsonPath("$.data[0].field").value("shipNumber"))
			.andExpect(jsonPath("$.data[0].reason").value("선박 번호는 필수 항목입니다."))
			.andExpect(jsonPath("$.message").value("요청하신 유효성 검증에 실패하였습니다."))
			.andExpect(jsonPath("$.success").value(false));
	}

	@Test
	@DisplayName("선박 저장 [DeparturePort - Size] [Controller] - Fail")
	@WithMockCustomUser
	void t06() throws Exception {
		// Given
		ShipRequest.Form givenRequestDto = createArbitraryBuilder
			.set("departurePort", "oiesfajoiejfioewafjoijwaoiefjwaoiefjiowjafiowjaofijwoaiefowaiefjoiwafjowaif")
			.sample();
		// When
		ResultActions resultActions = mockMvc.perform(post("/api/v1/ship")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(givenRequestDto)));
		// Then
		resultActions
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.timestamp").exists())
			.andExpect(jsonPath("$.code").value(GlobalErrorCode.NOT_VALID.getCode()))
			.andExpect(jsonPath("$.data[0].field").value("departurePort"))
			.andExpect(jsonPath("$.data[0].reason").value("선박 이름은 30자 이하여야 합니다."))
			.andExpect(jsonPath("$.message").value("요청하신 유효성 검증에 실패하였습니다."))
			.andExpect(jsonPath("$.success").value(false));
	}

	@Test
	@DisplayName("선박 저장 [DeparturePort - NotBlank] [Controller] - Fail")
	@WithMockCustomUser
	void t07() throws Exception {
		// Given
		ShipRequest.Form givenRequestDto = createArbitraryBuilder
			.set("departurePort", "")
			.sample();
		// When
		ResultActions resultActions = mockMvc.perform(post("/api/v1/ship")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(givenRequestDto)));
		// Then
		resultActions
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.timestamp").exists())
			.andExpect(jsonPath("$.code").value(GlobalErrorCode.NOT_VALID.getCode()))
			.andExpect(jsonPath("$.data[0].field").value("departurePort"))
			.andExpect(jsonPath("$.data[0].reason").value("출항장소는 필수 항목입니다."))
			.andExpect(jsonPath("$.message").value("요청하신 유효성 검증에 실패하였습니다."))
			.andExpect(jsonPath("$.success").value(false));
	}

	@Test
	@DisplayName("선박 저장 [PassengerCapacity - Min] [Controller] - Fail")
	@WithMockCustomUser
	void t08() throws Exception {
		// Given
		ShipRequest.Form givenRequestDto = createArbitraryBuilder
			.set("passengerCapacity", 0)
			.sample();
		// When
		ResultActions resultActions = mockMvc.perform(post("/api/v1/ship")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(givenRequestDto)));
		// Then
		resultActions
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.timestamp").exists())
			.andExpect(jsonPath("$.code").value(GlobalErrorCode.NOT_VALID.getCode()))
			.andExpect(jsonPath("$.data[0].field").value("passengerCapacity"))
			.andExpect(jsonPath("$.data[0].reason").value("선박 정원은 1명 이상이어야 합니다."))
			.andExpect(jsonPath("$.message").value("요청하신 유효성 검증에 실패하였습니다."))
			.andExpect(jsonPath("$.success").value(false));
	}

	@Test
	@DisplayName("선박 저장 [PassengerCapacity - Max] [Controller] - Fail")
	@WithMockCustomUser
	void t09() throws Exception {
		// Given
		ShipRequest.Form givenRequestDto = createArbitraryBuilder
			.set("passengerCapacity", 101)
			.sample();
		// When
		ResultActions resultActions = mockMvc.perform(post("/api/v1/ship")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(givenRequestDto)));
		// Then
		resultActions
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.timestamp").exists())
			.andExpect(jsonPath("$.code").value(GlobalErrorCode.NOT_VALID.getCode()))
			.andExpect(jsonPath("$.data[0].field").value("passengerCapacity"))
			.andExpect(jsonPath("$.data[0].reason").value("선박 정원은 100명 이하여야 합니다."))
			.andExpect(jsonPath("$.message").value("요청하신 유효성 검증에 실패하였습니다."))
			.andExpect(jsonPath("$.success").value(false));
	}

	@Test
	@DisplayName("선박 저장 [PassengerCapacity - NotNull] [Controller] - Fail")
	@WithMockCustomUser
	void t10() throws Exception {
		// Given
		ShipRequest.Form givenRequestDto = createArbitraryBuilder
			.set("passengerCapacity", null)
			.sample();
		// When
		ResultActions resultActions = mockMvc.perform(post("/api/v1/ship")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(givenRequestDto)));
		// Then
		resultActions
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.timestamp").exists())
			.andExpect(jsonPath("$.code").value(GlobalErrorCode.NOT_VALID.getCode()))
			.andExpect(jsonPath("$.data[0].field").value("passengerCapacity"))
			.andExpect(jsonPath("$.data[0].reason").value("선박 정원은 필수 항목입니다."))
			.andExpect(jsonPath("$.message").value("요청하신 유효성 검증에 실패하였습니다."))
			.andExpect(jsonPath("$.success").value(false));
	}

	@Test
	@DisplayName("선박 저장 [RestroomType - ValidEnum] [Controller] - Fail")
	@WithMockCustomUser
	void t11() throws Exception {
		// Given
		String invalidJson = objectMapper
			.writeValueAsString(createArbitraryBuilder.set("restroomType", RestroomType.PUBLIC).sample())
			.replace("\"공용 화장실\"", "\"TEST\"");

		// When
		ResultActions resultActions = mockMvc.perform(post("/api/v1/ship")
			.contentType(MediaType.APPLICATION_JSON)
			.content(invalidJson));

		// Then
		resultActions
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.timestamp").exists())
			.andExpect(jsonPath("$.code").value(GlobalErrorCode.NOT_VALID.getCode()))
			.andExpect(jsonPath("$.data[0].field").value("restroomType"))
			.andExpect(jsonPath("$.message").value("요청하신 유효성 검증에 실패하였습니다."))
			.andExpect(jsonPath("$.success").value(false));
	}

	@Test
	@DisplayName("선박 저장 [LoungeArea - NotNull] [Controller] - Fail")
	@WithMockCustomUser
	void t12() throws Exception {
		// Given
		ShipRequest.Form givenRequestDto = createArbitraryBuilder
			.set("loungeArea", null)
			.sample();
		// When
		ResultActions resultActions = mockMvc.perform(post("/api/v1/ship")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(givenRequestDto)));
		// Then
		resultActions
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.timestamp").exists())
			.andExpect(jsonPath("$.code").value(GlobalErrorCode.NOT_VALID.getCode()))
			.andExpect(jsonPath("$.data[0].field").value("loungeArea"))
			.andExpect(jsonPath("$.data[0].reason").value("휴게 공간 여부는 필수 항목입니다."))
			.andExpect(jsonPath("$.message").value("요청하신 유효성 검증에 실패하였습니다."))
			.andExpect(jsonPath("$.success").value(false));
	}

	@Test
	@DisplayName("선박 저장 [KitchenFacility - NotNull] [Controller] - Fail")
	@WithMockCustomUser
	void t13() throws Exception {
		// Given
		ShipRequest.Form givenRequestDto = createArbitraryBuilder
			.set("kitchenFacility", null)
			.sample();
		// When
		ResultActions resultActions = mockMvc.perform(post("/api/v1/ship")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(givenRequestDto)));
		// Then
		resultActions
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.timestamp").exists())
			.andExpect(jsonPath("$.code").value(GlobalErrorCode.NOT_VALID.getCode()))
			.andExpect(jsonPath("$.data[0].field").value("kitchenFacility"))
			.andExpect(jsonPath("$.data[0].reason").value("조리 시설 여부는 필수 항목입니다."))
			.andExpect(jsonPath("$.message").value("요청하신 유효성 검증에 실패하였습니다."))
			.andExpect(jsonPath("$.success").value(false));
	}

	@Test
	@DisplayName("선박 저장 [FishingChair - NotNull] [Controller] - Fail")
	@WithMockCustomUser
	void t14() throws Exception {
		// Given
		ShipRequest.Form givenRequestDto = createArbitraryBuilder
			.set("fishingChair", null)
			.sample();
		// When
		ResultActions resultActions = mockMvc.perform(post("/api/v1/ship")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(givenRequestDto)));
		// Then
		resultActions
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.timestamp").exists())
			.andExpect(jsonPath("$.code").value(GlobalErrorCode.NOT_VALID.getCode()))
			.andExpect(jsonPath("$.data[0].field").value("fishingChair"))
			.andExpect(jsonPath("$.data[0].reason").value("낚시 의자 여부는 필수 항목입니다."))
			.andExpect(jsonPath("$.message").value("요청하신 유효성 검증에 실패하였습니다."))
			.andExpect(jsonPath("$.success").value(false));
	}

	@Test
	@DisplayName("선박 저장 [PassengerInsurance - NotNull] [Controller] - Fail")
	@WithMockCustomUser
	void t15() throws Exception {
		// Given
		ShipRequest.Form givenRequestDto = createArbitraryBuilder
			.set("passengerInsurance", null)
			.sample();
		// When
		ResultActions resultActions = mockMvc.perform(post("/api/v1/ship")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(givenRequestDto)));
		// Then
		resultActions
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.timestamp").exists())
			.andExpect(jsonPath("$.code").value(GlobalErrorCode.NOT_VALID.getCode()))
			.andExpect(jsonPath("$.data[0].field").value("passengerInsurance"))
			.andExpect(jsonPath("$.data[0].reason").value("승객 보험 여부는 필수 항목입니다."))
			.andExpect(jsonPath("$.message").value("요청하신 유효성 검증에 실패하였습니다."))
			.andExpect(jsonPath("$.success").value(false));
	}

	@Test
	@DisplayName("선박 저장 [FishingGearRental - NotNull] [Controller] - Fail")
	@WithMockCustomUser
	void t16() throws Exception {
		// Given
		ShipRequest.Form givenRequestDto = createArbitraryBuilder
			.set("fishingGearRental", null)
			.sample();
		// When
		ResultActions resultActions = mockMvc.perform(post("/api/v1/ship")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(givenRequestDto)));
		// Then
		resultActions
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.timestamp").exists())
			.andExpect(jsonPath("$.code").value(GlobalErrorCode.NOT_VALID.getCode()))
			.andExpect(jsonPath("$.data[0].field").value("fishingGearRental"))
			.andExpect(jsonPath("$.data[0].reason").value("장비 대여 여부는 필수 항목입니다."))
			.andExpect(jsonPath("$.message").value("요청하신 유효성 검증에 실패하였습니다."))
			.andExpect(jsonPath("$.success").value(false));
	}

	@Test
	@DisplayName("선박 저장 [MealProvided - NotNull] [Controller] - Fail")
	@WithMockCustomUser
	void t17() throws Exception {
		// Given
		ShipRequest.Form givenRequestDto = createArbitraryBuilder
			.set("mealProvided", null)
			.sample();
		// When
		ResultActions resultActions = mockMvc.perform(post("/api/v1/ship")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(givenRequestDto)));
		// Then
		resultActions
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.timestamp").exists())
			.andExpect(jsonPath("$.code").value(GlobalErrorCode.NOT_VALID.getCode()))
			.andExpect(jsonPath("$.data[0].field").value("mealProvided"))
			.andExpect(jsonPath("$.data[0].reason").value("식사 제공 여부는 필수 항목입니다."))
			.andExpect(jsonPath("$.message").value("요청하신 유효성 검증에 실패하였습니다."))
			.andExpect(jsonPath("$.success").value(false));
	}

	@Test
	@DisplayName("선박 저장 [ParkingAvailable - NotNull] [Controller] - Fail")
	@WithMockCustomUser
	void t18() throws Exception {
		// Given
		ShipRequest.Form givenRequestDto = createArbitraryBuilder
			.set("parkingAvailable", null)
			.sample();
		// When
		ResultActions resultActions = mockMvc.perform(post("/api/v1/ship")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(givenRequestDto)));
		// Then
		resultActions
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.timestamp").exists())
			.andExpect(jsonPath("$.code").value(GlobalErrorCode.NOT_VALID.getCode()))
			.andExpect(jsonPath("$.data[0].field").value("parkingAvailable"))
			.andExpect(jsonPath("$.data[0].reason").value("주차 여부는 필수 항목입니다."))
			.andExpect(jsonPath("$.message").value("요청하신 유효성 검증에 실패하였습니다."))
			.andExpect(jsonPath("$.success").value(false));
	}

	@Test
	@DisplayName("선박 전체 조회 [Controller] - Success")
	@WithMockCustomUser
	void t19() throws Exception {
		// Given
		List<ShipResponse.Detail> givenDetailList = fixtureMonkeyRecord
			.giveMeBuilder(ShipResponse.Detail.class)
			.sampleList(10);

		when(shipService.getDetailAll(1L)).thenReturn(givenDetailList);

		// When
		ResultActions resultActions = mockMvc.perform(get("/api/v1/ship"));

		// Then
		resultActions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.data.size()").value(givenDetailList.size()));
	}

	@Test
	@DisplayName("선박 수정 [Controller] - Success")
	@WithMockCustomUser
	void t20() throws Exception {
		// Given
		Long shipId = 1L;
		ShipRequest.Form givenRequestDto = createArbitraryBuilder.sample();

		when(shipService.updateShip(shipId, 1L, givenRequestDto)).thenReturn(shipId);

		// When
		ResultActions resultActions = mockMvc.perform(patch("/api/v1/ship/{shipId}", shipId)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(givenRequestDto)));

		// Then
		resultActions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true));
	}

	@Test
	@DisplayName("선박 삭제 [Controller] - Success")
	@WithMockCustomUser
	void t21() throws Exception {
		// Given
		Long shipId = 1L;

		doNothing().when(shipService).deleteById(shipId, 1L);

		// When
		ResultActions resultActions = mockMvc.perform(delete("/api/v1/ship/{shipId}", shipId)
			.contentType(MediaType.APPLICATION_JSON));

		// Then
		resultActions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true));
	}

	@Test
	@DisplayName("선박 삭제 [Not Author] [Controller] - Fail")
	@WithMockCustomUser
	void t22() throws Exception {
		// Given
		Long shipId = 1L;

		// 선박 삭제 시 권한 오류를 발생시키는 경우
		doThrow(new ShipException(ShipErrorCode.SHIP_UNAUTHORIZED_AUTHOR))
			.when(shipService).deleteById(shipId, 1L);

		// When
		ResultActions resultActions = mockMvc.perform(delete("/api/v1/ship/{shipId}", shipId)
			.contentType(MediaType.APPLICATION_JSON));

		// Then
		resultActions
			.andExpect(status().isForbidden())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.code").value(ShipErrorCode.SHIP_UNAUTHORIZED_AUTHOR.getCode()))
			.andExpect(jsonPath("$.message").value(ShipErrorCode.SHIP_UNAUTHORIZED_AUTHOR.getMessage()));
	}

	@Test
	@DisplayName("선박 삭제 [In Use By Fishing Post] [Controller] - Fail")
	@WithMockCustomUser
	void t23() throws Exception {
		// Given
		Long shipId = 1L;

		// 선박 삭제 시 권한 오류를 발생시키는 경우
		doThrow(new ShipException(ShipErrorCode.SHIP_IN_USE_BY_FISHING_POST))
			.when(shipService).deleteById(shipId, 1L);

		// When
		ResultActions resultActions = mockMvc.perform(delete("/api/v1/ship/{shipId}", shipId)
			.contentType(MediaType.APPLICATION_JSON));

		// Then
		resultActions
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.code").value(ShipErrorCode.SHIP_IN_USE_BY_FISHING_POST.getCode()))
			.andExpect(jsonPath("$.message").value(ShipErrorCode.SHIP_IN_USE_BY_FISHING_POST.getMessage()));
	}
}