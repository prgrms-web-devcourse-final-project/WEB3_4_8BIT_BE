package com.backend.domain.member.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.domain.member.entity.Members;

public interface MembersJpaRepository extends JpaRepository<Members, Long> {

	/**
	 * 소셜 회원가입 여부 확인
	 *
	 * @param phone 회원 핸드폰 번호
	 * @return Members 회원
	 */
	Optional<Members> findByPhone(String phone);
}
