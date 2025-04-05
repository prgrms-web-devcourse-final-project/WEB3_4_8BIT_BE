package com.backend.domain.reservationdate.converter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.backend.domain.reservationdate.dto.response.ReservationDateResponse;
import com.backend.domain.reservationdate.entity.ReservationDate;
import com.backend.domain.reservationdate.entity.ReservationDateId;

public class ReservationDateConverter {

	/**
	 * 선상 낚시 게시글 Id 와 날짜, 예약 불가 여부 를 조합하여 복합 키를 생성 후 반환한다.
	 *
	 * @param reservationDate {@link LocalDate}
	 * @param shipFishingPostId {@link Long}
	 * @return {@link ReservationDateId}
	 */
	public static ReservationDateId fromReservationDateIdRequest(
		final LocalDate reservationDate,
		final Long shipFishingPostId) {

		return ReservationDateId.builder()
			.shipFishingPostId(shipFishingPostId)
			.reservationDate(reservationDate)
			.build();
	}

	/**
	 * 선상 낚시 게시글 Id 와 날짜, 예약 잔여 인원 를 조합하여 Entity 로 반환한다.
	 *
	 * @param reservationDate {@link LocalDate}
	 * @param shipFishingPostId {@link Long}
	 * @param remainCount {@link Integer}
	 * @return {@link ReservationDate}
	 */
	public static ReservationDate fromReservationDateRequestWithRemainCount(
		final LocalDate reservationDate,
		final Long shipFishingPostId,
		final Integer remainCount
	) {

		return ReservationDate.builder()
			.shipFishingPostId(shipFishingPostId)
			.reservationDate(reservationDate)
			.remainCount(remainCount)
			.build();
	}

	/**
	 * 선상 낚시 게시글 Id 와 날짜, 예약 불가 여부 를 조합하여 Entity 로 반환한다.
	 *
	 * @param reservationDate {@link LocalDate}
	 * @param shipFishingPostId {@link Long}
	 * @param isBan {@link Boolean}
	 * @return {@link ReservationDate}
	 */
	public static ReservationDate fromReservationDateRequest(
		final LocalDate reservationDate,
		final Long shipFishingPostId,
		final Boolean isBan) {

		return ReservationDate.builder()
			.shipFishingPostId(shipFishingPostId)
			.reservationDate(reservationDate)
			.isBan(isBan)
			.build();
	}

	/**
	 * 선상 낚시 게시글 Id 와 날짜, 예약 불가 여부 를 조합하여 List 로 반환한다.
	 *
	 * @param reservationDateList {@link List<LocalDate>}
	 * @param shipFishingPostId {@link Long}
	 * @param isBan {@link Boolean}
	 * @return {@link List<ReservationDate>}
	 */
	public static List<ReservationDate> fromReservationDateList(
		final List<LocalDate> reservationDateList,
		final Long shipFishingPostId,
		final Boolean isBan
	) {

		List<ReservationDate> reservationDate = new ArrayList<>();

		for (LocalDate date : reservationDateList) {
			reservationDate.add(fromReservationDateRequest(date, shipFishingPostId, isBan));
		}

		return reservationDate;
	}

	/**
	 * 지정한 날의 예약 가능 여부와 남은 인원 수를 반환하는 메서드입니다.
	 *
	 * @param reservationDate {@link ReservationDate}
	 * @return {@link ReservationDateResponse.Detail}
	 */
	public static ReservationDateResponse.Detail fromReservationDateResponseDetail(
		final ReservationDate reservationDate) {

		return ReservationDateResponse.Detail.builder()
			.remainCount(reservationDate.getRemainCount())
			.isBan(reservationDate.getIsBan())
			.build();
	}

	/**
	 * 지정한 달의 예약 불가능 날짜를 리스트로 반환하는 메서드입니다.
	 *
	 * @param unAvailableDateList {@link List<LocalDate>}
	 * @return {@link ReservationDateResponse.UnAvailableDateList}
	 */
	public static ReservationDateResponse.UnAvailableDateList fromUnAvailableDateList(
		final List<LocalDate> unAvailableDateList) {

		return ReservationDateResponse.UnAvailableDateList.builder()
			.unAvailableDateList(unAvailableDateList)
			.build();
	}
}
