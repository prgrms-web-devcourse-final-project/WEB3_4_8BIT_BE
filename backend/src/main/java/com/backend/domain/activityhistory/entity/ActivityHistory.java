package com.backend.domain.activityhistory.entity;

import com.backend.domain.activityhistory.domain.ActivityType;
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

@Table(name = "activity_histories")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@SuperBuilder
@ToString
public class ActivityHistory extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long activityHistoryId;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private ActivityType activityType;

	@Column(nullable = false)
	private Long targetId;

	@Column(nullable = false, length = 100)
	private String description;

	@Column(nullable = false)
	private Long memberId;
}
