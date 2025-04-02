package com.backend.domain.reservationdate.entity;

import java.time.ZonedDateTime;

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

@Table(name = "reservation_dates")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@SuperBuilder
@ToString
public class ReservationDate extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long reservationDateId;

	@Column(nullable = false)
	private Long shipFishingPostId;

	@Column(nullable = false)
	private Long memberId;

	@Builder.Default
	@Column(nullable = false)
	private Integer reservationCount = 0;

	@Column(nullable = false)
	private ZonedDateTime reservationDate;

	@Column(nullable = false)
	private Boolean isEnd;

	@Column(nullable = false)
	private Boolean isBan;
}
