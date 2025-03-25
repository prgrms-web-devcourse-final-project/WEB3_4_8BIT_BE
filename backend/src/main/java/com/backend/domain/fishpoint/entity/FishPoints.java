package com.backend.domain.fishpoint.entity;

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
public class FishPoints extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long fishPointId;

	@Column(unique = true, nullable = false, length = 50)
	private String fishPointName;

	@Column(nullable = false)
	private Double longitude;

	@Column(nullable = false)
	private Double latitude;

	@Column(nullable = false)
	private Boolean isBan;
}
