package com.backend.domain.fishencyclopedia.dto.request;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.backend.global.dto.GlobalRequest;

class FishEncyclopediaRequestTest {

	@Test
	@DisplayName("물고기 도감 상세 조회 요청 객체 [Size, Page is Null] [Dto] - Success")
	void t01() {
		// Given
		Integer givenPage = null;
		Integer givenSize = null;
		String givenSort = null;
		String givenOrder = null;

		// When
		GlobalRequest.PageRequest requestDto = new GlobalRequest.PageRequest(
			givenPage,
			givenSize,
			givenSort,
			givenOrder
		);

		// Then
		assertThat(requestDto.page()).isEqualTo(0);
		assertThat(requestDto.size()).isEqualTo(10);
	}

	@Test
	@DisplayName("물고기 도감 상세 조회 요청 객체 [Size is Null] [Dto] - Success")
	void t02() {
		// Given
		Integer givenPage = 10;
		Integer givenSize = null;
		String givenSort = null;
		String givenOrder = null;

		// When
		GlobalRequest.PageRequest requestDto = new GlobalRequest.PageRequest(
			givenPage,
			givenSize,
			givenSort,
			givenOrder
		);

		// Then
		assertThat(requestDto.page()).isEqualTo(10);
		assertThat(requestDto.size()).isEqualTo(10);
	}

	@Test
	@DisplayName("물고기 도감 상세 조회 요청 객체 [Page is Null] [Dto] - Success")
	void t03() {
		// Given
		Integer givenPage = null;
		Integer givenSize = 12;
		String givenSort = null;
		String givenOrder = null;

		// When
		GlobalRequest.PageRequest requestDto = new GlobalRequest.PageRequest(
			givenPage,
			givenSize,
			givenSort,
			givenOrder
		);

		// Then
		assertThat(requestDto.page()).isEqualTo(0);
		assertThat(requestDto.size()).isEqualTo(12);
	}

}