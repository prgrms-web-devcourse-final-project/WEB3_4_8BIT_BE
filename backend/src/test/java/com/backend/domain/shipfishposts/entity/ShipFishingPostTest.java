package com.backend.domain.shipfishposts.entity;

import java.time.LocalTime;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.backend.domain.shipfishingpost.entity.ShipFishingPost;

public class ShipFishingPostTest {

	@Test
	@DisplayName("평균 시간 설정 [Entity] - Success")
	void t01() {
		ShipFishingPost shipFishingPost = ShipFishingPost.builder()
			.startTime(LocalTime.of(15, 00))
			.endTime(LocalTime.of(16, 30))
			.build();

		shipFishingPost.setDurationTime();

		Assertions.assertThat(shipFishingPost.getDurationTime()).isEqualTo(LocalTime.of(1, 30));
	}
}
