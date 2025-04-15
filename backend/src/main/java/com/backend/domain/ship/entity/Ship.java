package com.backend.domain.ship.entity;

import com.backend.domain.ship.domain.RestroomType;
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
import lombok.Builder;
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

	@Column(nullable = false, length = 40)
	private String departurePort;

	@Column(nullable = false, length = 30)
	private String portName;

	@Column(nullable = false)
	private Integer passengerCapacity;

	@Column(nullable = false, length = 15)
	@Enumerated(EnumType.STRING)
	@Builder.Default
	private RestroomType restroomType = RestroomType.NONE;

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

	public void updateShip(
		final String shipName,
		final String shipNumber,
		final String departurePort,
		final String portName,
		final Integer passengerCapacity,
		final RestroomType restroomType,
		final Boolean loungeArea,
		final Boolean kitchenFacility,
		final Boolean fishingChair,
		final Boolean passengerInsurance,
		final Boolean fishingGearRental,
		final Boolean mealProvided,
		final Boolean parkingAvailable) {

		this.shipName = shipName;
		this.shipNumber = shipNumber;
		this.departurePort = departurePort;
		this.portName = portName;
		this.passengerCapacity = passengerCapacity;
		this.restroomType = restroomType;
		this.loungeArea = loungeArea;
		this.kitchenFacility = kitchenFacility;
		this.fishingChair = fishingChair;
		this.passengerInsurance = passengerInsurance;
		this.fishingGearRental = fishingGearRental;
		this.mealProvided = mealProvided;
		this.parkingAvailable = parkingAvailable;

	}
}