package com.backend.domain.shipfishposts.entity;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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
public class ShipFishPosts extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long shipFishPostId;

	// Todo : user 정보 등록 ( member Id & Name )
	// @Column(nullable = false)
	// private Long memberId;
	//
	// @Column(nullable = false)
	// private String memberName;

	@Column(nullable = false, length = 50)
	private String subject;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String content;

	@JdbcTypeCode(SqlTypes.JSON)
	private List<String> images;

	@Column(nullable = false)
	private BigDecimal price;

	@Column(nullable = false)
	private ZonedDateTime startDate;

	@Column(nullable = false)
	private ZonedDateTime endDate;

	@Column(nullable = false)
	private Long durationMinute;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(nullable = false)
	private List<Long> fishId;

	@Column(nullable = false)
	private Long shipId;

	private Double reviewEverRate;
}
