package com.backend.global.util;

import java.io.IOException;

import org.springframework.http.ResponseCookie;

import com.backend.global.response.GenericResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletResponse;

public class AuthResponseUtil {

	/**
	 * 로그인 성공 시: accessToken은 ResponseCookie로 내려주고 JSON 응답
	 */
	public static void success(HttpServletResponse response,
		ResponseCookie accessTokenCookie,
		int status,
		GenericResponse<?> rsData,
		ObjectMapper om) throws IOException {

		response.addHeader("Set-Cookie", accessTokenCookie.toString());
		writeJsonResponse(response, status, rsData, om);
	}

	/**
	 * 로그인 실패 시: JSON으로 에러 응답
	 */
	public static void fail(HttpServletResponse response,
		GenericResponse<?> rsData,
		int status,
		ObjectMapper om) throws IOException {

		writeJsonResponse(response, status, rsData, om);
	}

	/**
	 * 공통 JSON 작성 메서드
	 */
	private static void writeJsonResponse(HttpServletResponse response,
		int status,
		GenericResponse<?> body,
		ObjectMapper om) throws IOException {

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.setStatus(status);
		response.getWriter().write(om.writeValueAsString(body));
	}
}
