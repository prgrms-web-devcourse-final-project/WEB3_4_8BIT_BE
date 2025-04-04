package com.backend.domain.fishpoint.entity;

import org.hibernate.annotations.ColumnDefault;

import com.backend.global.baseentity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Table(name = "fish_points")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@SuperBuilder
public class FishPoint extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long fishPointId;

	@Column(nullable = false, length = 50)
	private String fishPointName;

	@Column(nullable = false, length = 50)
	private String fishPointDetailName;

	@Column(nullable = false)
	private Double longitude;

	@Column(nullable = false)
	private Double latitude;

	@Column(nullable = false)
	@ColumnDefault("false")
	@Builder.Default
	private Boolean isBan = false;
}
