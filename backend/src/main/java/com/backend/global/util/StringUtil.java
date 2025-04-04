package com.backend.global.util;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class StringUtil {

	private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy.MM.dd");
	private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

	/**
	 * ZonedDateTime을 "yyyy.MM.dd" 형식의 문자열로 포맷합니다.
	 *
	 * @param dateTime 변환할 {@link ZonedDateTime} 객체
	 * @return "yyyy.MM.dd" 형식의 날짜 문자열
	 */
	public static String formatDate(final ZonedDateTime dateTime) {
		return dateTime.format(DATE_FORMAT);
	}

	/**
	 * ZonedDateTime을 "HH:mm" 형식의 문자열로 포맷합니다.
	 *
	 * @param dateTime 변환할 {@link ZonedDateTime} 객체
	 * @return "HH:mm" 형식의 시간 문자열
	 */
	public static String formatTime(final ZonedDateTime dateTime) {
		return dateTime.format(TIME_FORMAT);
	}

	/**
	 * 현재 인원 수와 모집 정원을 기반으로 "현재/정원명" 형식의 문자열을 반환합니다.
	 *
	 * @param current 현재 인원 수
	 * @param recruit 모집 정원 수
	 * @return "current/total명" 형식의 문자열 (예: "3/6명")
	 */
	public static String formatParticipantCount(Integer current, Integer recruit) {
		return current + "/" + recruit + "명";
	}
}
