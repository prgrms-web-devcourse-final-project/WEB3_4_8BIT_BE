package com.backend.domain.reservation.entity;

import java.time.LocalDate;
import java.time.OffsetDateTime;

import com.backend.global.baseentity.BaseEntity;
import com.backend.global.payment.dto.response.TossPaymentResponse;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Table(name = "reservations", indexes = {
	@Index(name = "idx_reservation_01", columnList = "reservationId, reservationDate")
})
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
	private ReservationStatus status = ReservationStatus.PENDING;

	@Column(length = 100)
	private String paymentKey;

	@Column(length = 20)
	private String paymentMethod;

	@Column(length = 50)
	private String cardNumber;

	@Column(length = 20)
	private String cardApproveNo;

	@Column(length = 1024)
	private String receiptUrl;

	@Column
	private Long totalAmount;

	@Column
	private OffsetDateTime approvedAt;

	public void updatePending(final Boolean isSuccess) {
		this.status = isSuccess ? ReservationStatus.CONFIRMED : ReservationStatus.REJECTED;
	}

	public void updateCanceled() {
		this.status = this.status == ReservationStatus.CONFIRMED ? ReservationStatus.CANCELLED : this.status;
	}

	public void updateTossPaymentInfo(final TossPaymentResponse response) {
		this.paymentKey = response.getPaymentKey();
		this.approvedAt = response.getApprovedAt();
		this.paymentMethod = response.getMethod();
		this.totalAmount = response.getTotalAmount();
		if (response.getCard() != null) {
			this.cardNumber = response.getCard().getNumber();
			this.cardApproveNo = response.getCard().getApproveNo();
		}
		if (response.getReceipt() != null) {
			this.receiptUrl = response.getReceipt().getUrl();
		}
	}
}
