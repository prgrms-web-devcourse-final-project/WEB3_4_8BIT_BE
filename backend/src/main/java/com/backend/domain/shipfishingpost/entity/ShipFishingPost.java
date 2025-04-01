package com.backend.domain.shipfishingpost.entity;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.backend.global.baseentity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Table(name = "ship_fish_posts")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@SuperBuilder
@ToString
@EqualsAndHashCode(callSuper = false)
public class ShipFishingPost extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long shipFishingPostId;

	@Column(nullable = false)
	private Long memberId;

	@Column(nullable = false, length = 50)
	private String subject;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String content;

	@JdbcTypeCode(SqlTypes.JSON)
	private List<String> imageList;

	@Column(nullable = false)
	private Long price;

	@Column(nullable = false)
	private String location;

	@Column(nullable = false)
	private LocalTime startTime;

	@Column(nullable = false)
	private LocalTime endTime;

	@Column(nullable = false)
	private LocalTime durationTime;

	@Column(nullable = false)
	private Long maxGuestCount;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(nullable = false)
	private List<Long> fishList;

	@Column(nullable = false, unique = true)
	private Long shipId;

	@Column(nullable = false)
	@ColumnDefault("0")
	@Builder.Default
	private Long viewCount = 0L;

	@Column(nullable = false)
	@ColumnDefault("0.00")
	@Builder.Default
	private Double reviewEverRate = 0D;

	public void setDurationTime() {
		long minutes = ChronoUnit.MINUTES.between(startTime, endTime);
		durationTime = LocalTime.MIDNIGHT.plusMinutes(minutes);
	}
}