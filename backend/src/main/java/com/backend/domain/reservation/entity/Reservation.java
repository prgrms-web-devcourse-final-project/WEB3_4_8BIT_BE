package com.backend.domain.reservation.entity;

import java.time.LocalDate;

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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Table(name = "reservations")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@SuperBuilder
@ToString
public class Reservation extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long reservationId;

	@Column(nullable = false)
	private Long shipFishingPostId;

	@Column(nullable = false)
	private Long memberId;

	@Column(nullable = false)
	private String reservationNumber;

	@Column(nullable = false)
	private Integer guestCount;

	@Column(nullable = false)
	private Long price;

	@Column(nullable = false)
	private Long totalPrice;

	@Column(nullable = false)
	private LocalDate reservationDate;

	@Builder.Default
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ReservationStatus status = ReservationStatus.CONFIRMED;

	public void updatePending(final Boolean isSuccess) {
		this.status = isSuccess ? ReservationStatus.CONFIRMED : ReservationStatus.REJECTED;
	}

	public void updateCanceled() {
		this.status = this.status == ReservationStatus.CONFIRMED ? ReservationStatus.CANCELLED : this.status;
	}
}
