package com.backend.domain.fishpointsummary.entity;

import com.backend.global.baseentity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Table(name = "fish_point_summaries")
@Entity
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FishPointSummary extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long fishPointSummaryId;

	@Column(nullable = false)
	private Long fishPointId;

	@Column(nullable = false)
	private Long fishId;

	@Column(nullable = false)
	private Long fileId;

	@Column(nullable = false)
	private Integer totalCount;
}
