package com.backend.domain.member.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.backend.domain.member.dto.MemberResponse;
import com.backend.domain.member.entity.Member;

public interface MemberRepository {

	/**
	 * 회원 저장 메소드
	 *
	 * @param member {@link Member}
	 * @return {@link Member}
	 * @implSpec 회원 정보를 저장한다.
	 */
	Member save(final Member member);

	/**
	 * Phone로 회원 조회
	 *
	 * @param phone 회원 핸드폰 번호
	 * @return {@link Optional<Member>}
	 * @implSpec 소셜 로그인 정보를 기반으로 회원을 조회한다.
	 */
	Optional<Member> findByPhone(final String phone);

	/**
	 * ID로 회원 조회
	 *
	 * @param id 회원 고유 ID
	 * @return {@link Optional<Member>} 회원 정보 조회
	 * @implSpec 인증된 사용자 ID 기반으로 회원 정보를 조회할 때 사용한다.
	 */
	Optional<Member> findById(final Long id);

	/**
	 * 회원 상세 조회 메서드
	 *
	 * @param memberId {@link Long}
	 * @return {@link Optional<MemberResponse.Detail>}
	 * @implSpec memberId 기준으로 회원 상세 정보를 조회한다.
	 */
	Optional<MemberResponse.Detail> findDetailById(final Long memberId);

	/**
	 * 멤버 존재 여부 조회 메소드
	 *
	 * @param memberId {@link Long}
	 * @return {@link Boolean} 데이터가 있다면 true, 없으면 false
	 * @implSpec memberId 데이터가 있는지 확인 후 결과 반한
	 */
	boolean existsById(final Long memberId);

	/**
	 * 주어진 회원 ID 목록에 해당하는 회원들의 이메일 주소 리스트를 조회합니다.
	 *
	 * <p>회원 테이블에서 {@code memberIdList}에 포함된 ID를 가진 회원들을 대상으로
	 * 이메일 주소만 추출하여 리스트로 반환합니다. 회원 ID가 존재하지 않거나 비어 있을 경우,
	 * 빈 리스트를 반환합니다.</p>
	 *
	 * @param memberIdList 이메일을 조회할 대상 회원들의 ID 목록
	 * @return 해당 회원들의 이메일 주소 목록 (존재하지 않는 ID는 무시됨)
	 * @implSpec 내부적으로 {@code member.id IN (:memberIdList)} 조건으로 쿼리를 수행하며, 컬럼만 추출하여 반환합니다.
	 */
	List<String> findEmailListByIdList(final List<Long> memberIdList);

	/**
	 * 주어진 회원 ID 리스트를 기반으로 각 회원의 프로필 이미지 URL을 조회
	 *
	 * @param memberIdList 조회할 회원 ID 리스트
	 * @return 회원 ID를 키로, 프로필 이미지 URL을 값으로 갖는 Map
	 */
	Map<Long, String> getFileUrlMapByIdList(final Set<Long> memberIdList);
}
