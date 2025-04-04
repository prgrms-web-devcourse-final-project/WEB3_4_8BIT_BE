package com.backend.domain.fishingtrippost.entity;

import java.time.ZonedDateTime;
import java.util.List;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.backend.domain.fishingtrippost.exception.FishingTripPostErrorCode;
import com.backend.domain.fishingtrippost.exception.FishingTripPostException;
import com.backend.global.baseentity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@SuperBuilder
@ToString
public class FishingTripPost extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long fishingTripPostId;

	@Column(nullable = false, length = 50)
	private String subject;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String content;

	@Column(nullable = false)
	private Integer recruitmentCount;

	@Column(nullable = false)
	private Integer currentCount;

	@Column(nullable = false)
	private Boolean isShipFish;

	@Column(nullable = false)
	private ZonedDateTime fishingDate;

	@Column(nullable = false)
	private Long fishingPointId;

	@Column(nullable = false)
	private Long memberId;

	@JdbcTypeCode(SqlTypes.JSON)
	private List<Long> fileIdList;

	// 현재 인원 증가 로직
	public void increaseCurrentCount(int count) {
		if (this.currentCount + count > this.recruitmentCount) {
			throw new FishingTripPostException(FishingTripPostErrorCode.FISHING_TRIP_POST_OVER_RECRUITMENT);
		}
		this.currentCount += count;
	}

	// 동출 게시글 수정 메서드
	public void updateFishingTripPost(
		final String subject,
		final String content,
		final Integer recruitmentCount,
		final Boolean isShipFish,
		final ZonedDateTime fishingDate,
		final Long fishingPointId,
		final List<Long> fileIdList
	) {
		this.subject = subject;
		this.content = content;
		this.recruitmentCount = recruitmentCount;
		this.isShipFish = isShipFish;
		this.fishingDate = fishingDate;
		this.fishingPointId = fishingPointId;
		this.fileIdList = fileIdList;
	}
}
