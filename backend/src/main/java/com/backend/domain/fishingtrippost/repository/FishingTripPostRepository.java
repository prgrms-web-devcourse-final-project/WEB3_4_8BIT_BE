package com.backend.domain.fishingtrippost.repository;

import java.util.Optional;

import com.backend.domain.fishingtrippost.dto.response.FishingTripPostResponse;
import com.backend.domain.fishingtrippost.entity.FishingTripPost;

public interface FishingTripPostRepository {

	/**
	 * 동출 모집 게시글 저장 메서드
	 *
	 * @param fishingTripPost {@link FishingTripPost}
	 * @return {@link FishingTripPost}
	 * @implSpec FishingTripPost 받아서 저장 후 저장된 엔티티 반환
	 */

	FishingTripPost save(final FishingTripPost fishingTripPost);

	/**
	 * 동출 모집 게시글 조회 메서드
	 *
	 * @param fishingTripPostId {@link Long}
	 * @return {@link FishingTripPost}
	 * @implSpec FishingTripPostId 받아서 조회된 동출 모집 게시글 엔티티 반환
	 */
	Optional<FishingTripPost> findById(final Long fishingTripPostId);

	/**
	 * 주어진 게시글 ID를 기반으로 동출 게시글의 상세 정보를 조회합니다.
	 *
	 * <p>이 메서드는 게시글 작성자, 제목, 내용, 모집 정보, 출조 날짜, 위치 정보, 이미지 파일 ID 목록을 포함한
	 * {@link com.backend.domain.fishingtrippost.dto.response.FishingTripPostResponse.DetailQueryDto}를 반환합니다.</p>
	 *
	 * <p>이 DTO는 최종 응답 DTO {@link com.backend.domain.fishingtrippost.dto.response.FishingTripPostResponse.Detail}
	 * 를 생성하기 위한 중간 단계로 사용됩니다. 이미지 파일 ID를 통해 실제 URL을 조회하는 로직은 서비스 계층에서 처리됩니다.</p>
	 *
	 * @implSpec
	 * 이 메서드는 QueryDSL을 기반으로 동출 게시글, 작성자(member), 낚시 포인트(fishPoint) 정보를 조인하여
	 * 단일 쿼리로 가져옵니다. 결과가 없을 경우 {@link Optional#empty()}를 반환합니다.
	 *
	 * 구현 시 성능과 확장성을 고려하여 필요한 필드만 조회하도록 주의해야 합니다.
	 *
	 * @param fishingTripPostId 조회할 동출 게시글의 고유 ID
	 * @return 게시글 상세 정보를 담은 DTO를 {@link Optional}로 감싼 값. 존재하지 않으면 {@link Optional#empty()}
	 */
	Optional<FishingTripPostResponse.DetailQueryDto> findDetailQueryDtoById(final Long fishingTripPostId);
}