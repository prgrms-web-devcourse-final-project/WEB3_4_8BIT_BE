package com.backend.domain.fishpoint.entity;

import org.hibernate.annotations.ColumnDefault;
import org.locationtech.jts.geom.Point;

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
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Table(name = "fish_points")
@Entity
@Getter
@SuperBuilder
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

	// @Column(nullable = false, columnDefinition = "POINT SRID 4326") -> h2에서 지원 안함, DB 단에서 직접 설정
	@Column(nullable = false)
	private Point location;

	@Column(nullable = false)
	private Long regionId;
}
