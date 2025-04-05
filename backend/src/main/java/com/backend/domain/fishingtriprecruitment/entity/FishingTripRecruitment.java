package com.backend.domain.fishingtriprecruitment.entity;

import com.backend.domain.fishingtriprecruitment.domain.FishingLevel;
import com.backend.domain.fishingtriprecruitment.domain.RecruitmentStatus;
import com.backend.global.baseentity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Table(name = "fishing_trip_recruitments")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@SuperBuilder
@ToString
public class FishingTripRecruitment extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long fishingTripRecruitmentId;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private FishingLevel fishingLevel;

	@Column(nullable = false, length = 50)
	private String introduction;

	@Column(nullable = false, length = 10)
	@Enumerated(EnumType.STRING)
	private RecruitmentStatus recruitmentStatus;

	@Column(nullable = false)
	private Long fishingTripPostId;

	@Column(nullable = false)
	private Long memberId;
}
