package com.backend.domain.fishingtrippost.service;

import com.backend.domain.fishingtrippost.dto.request.FishingTripPostRequest;
import com.backend.domain.fishingtrippost.dto.response.FishingTripPostResponse;
import com.backend.domain.fishingtrippost.exception.FishingTripPostException;
import com.backend.domain.fishingtrippost.repository.FishingTripPostRepository;

public interface FishingTripPostService {

	/**
	 * 동출 게시글 저장 메소드
	 *
	 * @param memberId   동출 게시글 작성하는 멤버
	 * @param requestDto {@link FishingTripPostRequest.Form}
	 * @return {@link Long fishingTripPostId} Long: 동출 게시글 Id fishingTripPostId
	 * @implSpec 로그인한 멤버 Id와 동출 게시글 작성에 필요한 정보를 받아 게시글 작성
	 */
	Long createFishingTripPost(
		final Long memberId,
		final FishingTripPostRequest.Form requestDto
	);

	/**
	 * 동출 게시글 수정 메소드
	 *
	 * @param memberId   동출 게시글 작성하는 멤버
	 * @param requestDto {@link FishingTripPostRequest.Form}
	 * @return {@link Long fishingTripPostId} Long: 동출 게시글 Id fishingTripPostId
	 * @implSpec 로그인한 멤버 Id와 동출 게시글 수정에 필요한 정보를 받아 게시글 수정
	 */
	Long updateFishingTripPost(
		final Long memberId,
		final Long fishingTripPostId,
		final FishingTripPostRequest.Form requestDto
	);

	/**
	 * 주어진 게시글 ID를 기반으로 동출 모집 게시글의 상세 정보를 조회합니다.
	 *
	 * <p>이 메서드는 게시글의 기본 정보, 작성자 이름, 낚시 포인트 정보, 이미지 URL 목록을 포함한
	 * {@link FishingTripPostResponse.Detail} DTO를 반환합니다.</p>
	 *
	 * <p>내부적으로는 먼저 {@link FishingTripPostResponse.DetailQueryDto}를 조회한 뒤,
	 * 해당 DTO의 파일 ID 목록을 통해 이미지 URL 리스트를 조회하고, 이를 기반으로 최종 응답 DTO를 생성합니다.</p>
	 *
	 * @implSpec
	 * 이 구현은 다음 순서로 동작합니다:
	 * <ol>
	 *   <li>{@link FishingTripPostRepository}를 통해 게시글 ID에 해당하는 {@link FishingTripPostResponse.DetailQueryDto}를 조회합니다.</li>
	 *   <li>조회된 DTO가 존재하지 않으면 {@link FishingTripPostException}을 발생시킵니다.</li>
	 *   <li>DTO의 fileIdList를 기반으로 {@link com.backend.global.storage.repository.StorageRepository}에서 이미지 URL들을 조회합니다.</li>
	 *   <li>이 정보들을 바탕으로 최종 {@link FishingTripPostResponse.Detail} 객체를 생성하여 반환합니다.</li>
	 * </ol>
	 *
	 * @param fishingTripPostId 조회할 게시글의 고유 ID
	 * @return 게시글 상세 정보가 담긴 {@link FishingTripPostResponse.Detail}
	 * @throws FishingTripPostException 게시글이 존재하지 않는 경우 발생
	 */
	FishingTripPostResponse.Detail getFishingTripPostDetail(final Long fishingTripPostId);
}
