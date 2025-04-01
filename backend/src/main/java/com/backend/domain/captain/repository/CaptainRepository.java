package com.backend.domain.captain.repository;

import java.util.Optional;

import com.backend.domain.captain.dto.Response.CaptainResponse;
import com.backend.domain.captain.entity.Captain;

public interface CaptainRepository {

	/**
	 * 선장 저장 메소드
	 *
	 * @param captain {@link Captain}
	 * @return {@link Captain}
	 * @implSpec 선장 저장 메서드 입니다.
	 */
	Captain save(final Captain captain);

	/**
	 * 선장 조회 메소드
	 *
	 * @param captainId {@link Long}
	 * @return {@link Optional<Captain>}
	 * @implSpec Id로 선장 조회 메서드 입니다.
	 */
	Optional<Captain> findById(final Long captainId);

	/**
	 * 선장 상세 조회 메소드
	 *
	 * @param captainId {@link Long}
	 * @return {@link Optional<CaptainResponse.Detail>}
	 * @implSpec Id로 선장 상세 조회 메서드 입니다.
	 */
	Optional<CaptainResponse.Detail> findDetailById(final Long captainId);
}
