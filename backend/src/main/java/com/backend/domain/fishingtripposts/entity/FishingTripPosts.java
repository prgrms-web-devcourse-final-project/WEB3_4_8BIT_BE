package com.backend.domain.fishingtripposts.entity;

import java.time.ZonedDateTime;

import com.backend.global.baseentity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@SuperBuilder
@ToString
@EqualsAndHashCode(callSuper = false)
public class FishingTripPosts extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long fishingPostId;

	@Column(nullable = false, length = 50)
	private String subject;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String content;

	@Column(nullable = false)
	private Integer recruitmentCount;

	@Column(nullable = false)
	private Boolean isShipFish;

	@Column(nullable = false)
	private ZonedDateTime fishingDate;

	@Column(nullable = false, length = 30)
	private String place;

	private Double longitude;

	private Double latitude;

	@Column(nullable = false)
	private Long memberId;
}
