package com.backend.domain.ship.entity;

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
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Table(name = "ships")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@SuperBuilder
@ToString
public class Ship extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long shipId;

	@Column(nullable = false, length = 30)
	private String shipName;

	//TODO 추후 길이 수정 예정
	@Column(unique = true, nullable = false, length = 30)
	private String shipNumber;

	@Column(nullable = false)
	private Long memberId;

	@Column(nullable = false, length = 30)
	private String departurePort;

	@Column(nullable = false)
	private Boolean publicRestroom;

	@Column(nullable = false)
	private Boolean loungeArea;

	@Column(nullable = false)
	private Boolean kitchenFacility;

	@Column(nullable = false)
	private Boolean fishingChair;

	@Column(nullable = false)
	private Boolean passengerInsurance;

	@Column(nullable = false)
	private Boolean fishingGearRental;

	@Column(nullable = false)
	private Boolean mealProvided;

	@Column(nullable = false)
	private Boolean parkingAvailable;
}