package com.backend.domain.member.repository;

import java.util.Optional;

import com.backend.domain.member.entity.Members;

public interface MembersRepository {

	/**
	 * 회원 저장 메소드
	 *
	 * @param members {@link Members}
	 * @return {@link Members}
	 * @implSpec 회원 정보를 저장한다.
	 */
	Members save(Members members);

	/**
	 * Phone로 회원 조회
	 *
	 * @param phone 회원 핸드폰 번호
	 * @return {@link Optional<Members>}
	 * @implSpec 소셜 로그인 정보를 기반으로 회원을 조회한다.
	 */
	Optional<Members> findByPhone(String phone);

	/**
	 * ID로 회원 조회
	 *
	 * @param id 회원 고유 ID
	 * @return {@link Optional<Members>} 회원 정보 조회
	 * @implSpec 인증된 사용자 ID 기반으로 회원 정보를 조회할 때 사용한다.
	 */
	Optional<Members> findById(Long id);
}
