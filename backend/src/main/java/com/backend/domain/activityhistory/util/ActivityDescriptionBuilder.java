package com.backend.domain.activityhistory.util;

public class ActivityDescriptionBuilder {

	public static String createReview(String shipName, Integer rating) {
		StringBuilder sb = getStringBuilder();

		return sb.append(shipName)
			.append("-")
			.append(rating)
			.toString();
	}

	public static String createFishEncyclopedia(
		final String fishName,
		final String fishPointDetailName,
		final Integer length
	) {
		StringBuilder sb = getStringBuilder();

		return sb.append(fishName)
			.append(" ")
			.append(length)
			.append("cm - ")
			.append(fishPointDetailName)
			.toString();
	}

	private static StringBuilder getStringBuilder() {
		return new StringBuilder();
	}

	public static String createReservation(
		final String subject,
		final String reservationDate
	) {
		StringBuilder sb = getStringBuilder();

		return sb
			.append(subject)
			.append(" - ")
			.append(reservationDate)
			.toString();
	}

}
