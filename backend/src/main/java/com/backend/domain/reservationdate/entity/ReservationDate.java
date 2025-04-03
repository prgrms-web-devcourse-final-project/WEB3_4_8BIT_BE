package com.backend.domain.reservationdate.entity;

import java.time.LocalDate;

import com.backend.global.baseentity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Table(name = "reservation_dates")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@SuperBuilder
@ToString
@IdClass(ReservationDateId.class)
public class ReservationDate extends BaseEntity {

	@Id
	@Column(insertable = false, updatable = false)
	private Long shipFishingPostId;

	@Id
	@Column(insertable = false, updatable = false)
	private LocalDate reservationDate;

	@Min(0)
	@Builder.Default
	@Column(nullable = false)
	private Integer remainCount = 0;

	@Builder.Default
	@Column(nullable = false)
	private Boolean isBan = false;

	public void remainMinus(final Integer guestCount) {
		this.remainCount -= guestCount;
	}

	public void remainPlus(final Integer guestCount) {
		this.remainCount += guestCount;
	}
}
