package com.backend.domain.shipfishposts.util;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import com.backend.domain.shipfishposts.dto.request.ShipFishPostsRequest;
import com.backend.domain.shipfishposts.entity.ShipFishPosts;

public class ShipFishPostsUtil {

	public static ShipFishPosts createShipFishPostsWithId() {
		return ShipFishPosts.builder()
			.shipFishPostId(1L)
			.subject("test subject")
			.content("test content")
			.price(BigDecimal.valueOf(10000L))
			.startDate(ZonedDateTime.now())
			.endDate(ZonedDateTime.now())
			.durationMinute(600L)
			.shipId(1L)
			.images(new ArrayList<>())
			.fishId(new ArrayList<>())
			.createdAt(ZonedDateTime.now())
			.modifiedAt(ZonedDateTime.now())
			.build();
	}

	public static ShipFishPosts createShipFishPostsWithNullableId() {
		return ShipFishPosts.builder()
			.subject("test subject")
			.content("test content")
			.price(BigDecimal.valueOf(10000L))
			.startDate(ZonedDateTime.now())
			.endDate(ZonedDateTime.now())
			.durationMinute(600L)
			.shipId(1L)
			.images(new ArrayList<>())
			.fishId(new ArrayList<>())
			.createdAt(ZonedDateTime.now())
			.modifiedAt(ZonedDateTime.now())
			.build();
	}

	public static List<ShipFishPosts> createShipFishPostsList(int range) {
		List<ShipFishPosts> shipFishPostsList = new ArrayList<>();
		IntStream.range(0, range).forEach(i -> {
			ShipFishPosts test = ShipFishPosts.builder()
				.shipFishPostId((long)range + 1)
				.subject("test subject" + range)
				.content("test content" + range)
				.price(BigDecimal.valueOf(10000L))
				.startDate(ZonedDateTime.now())
				.endDate(ZonedDateTime.now())
				.durationMinute(600L)
				.shipId(1L)
				.images(new ArrayList<>())
				.fishId(new ArrayList<>())
				.createdAt(ZonedDateTime.now())
				.modifiedAt(ZonedDateTime.now())
				.build();
		});

		return shipFishPostsList;
	}

	public static ShipFishPostsRequest.Create createShipFishPostsRequestCreate() {
		return new ShipFishPostsRequest.Create(
			"test subject",
			"test content",
			BigDecimal.valueOf(10000L),
			ZonedDateTime.now(),
			ZonedDateTime.now().plusMinutes(60),
			1L,
			List.of("http://example.com/image1.jpg", "http://example.com/image2.jpg"),
			List.of(1L, 2L, 3L)
		);
	}

}