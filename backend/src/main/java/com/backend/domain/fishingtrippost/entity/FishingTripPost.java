package com.backend.domain.fishingtrippost.entity;

import java.time.ZonedDateTime;
import java.util.List;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.backend.domain.fishingtrippost.domain.PostStatus;
import com.backend.domain.fishingtrippost.exception.FishingTripPostErrorCode;
import com.backend.domain.fishingtrippost.exception.FishingTripPostException;
import com.backend.global.baseentity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Table(name = "fishing_trip_posts",
	indexes = {
		@Index(name = "idx_fishing_trip_post_01", columnList = "memberId"),
		@Index(name = "idx_fishing_trip_post_02", columnList = "fishingPointId"),
		@Index(name = "idx_fishing_trip_post_createdAt_id", columnList = "createdAt, fishingTripPostId"),
		@Index(name = "idx_fishing_trip_post_region", columnList = "regionId")
	}
)
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
	@Enumerated(EnumType.STRING)
	private PostStatus postStatus;

	@Column(nullable = false)
	private ZonedDateTime fishingDate;

	@Column(nullable = false)
	private Long fishingPointId;

	@Column(nullable = false)
	private Long memberId;

	@Column(nullable = false)
	private Long regionId;

	@JdbcTypeCode(SqlTypes.JSON)
	private List<Long> fileIdList;

	@Column(nullable = false)
	@ColumnDefault("0")
	@Builder.Default
	private Long likeCount = 0L;

	@Column(nullable = false)
	@ColumnDefault("0")
	@Builder.Default
	private Long commentCount = 0L;

	public void plusCommentCount() {
		this.commentCount++;
	}

	public void minusCommentCount(final Long deleteCount) {
		if (this.commentCount > 0) {
			this.commentCount -= deleteCount;
		}
	}

	public void updateLikeCount(final Long likeCount) {
		this.likeCount = likeCount;
	}

	// 현재 인원 증가 로직
	public void increaseCurrentCount(final int count) {
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
		final List<Long> fileIdList
	) {
		this.subject = subject;
		this.content = content;
		this.recruitmentCount = recruitmentCount;
		this.isShipFish = isShipFish;
		this.fishingDate = fishingDate;
		this.fileIdList = fileIdList;
	}

	public void setPostStatus(final PostStatus postStatus) {
		this.postStatus = postStatus;
	}
}
