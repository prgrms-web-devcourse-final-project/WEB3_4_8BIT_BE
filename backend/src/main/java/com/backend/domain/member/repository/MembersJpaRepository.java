package com.backend.domain.member.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.domain.member.domain.Provider;
import com.backend.domain.member.entity.Members;

public interface MembersJpaRepository extends JpaRepository<Members, Long> {

	/**
	 * 소셜 회원가입 여부 확인
	 *
	 * @param provider OAuth2 서비스 구분 ID ex) kakao, naver
	 * @param providerId 소셜별 유저 고유 ID
	 * @return Members 회원
	 */
	Optional<Members> findByProviderAndProviderId(Provider provider, String providerId);
}
