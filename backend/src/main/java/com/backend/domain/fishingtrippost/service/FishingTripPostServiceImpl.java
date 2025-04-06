package com.backend.domain.fishingtrippost.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.backend.domain.fishingtrippost.converter.FishingTripPostConverter;
import com.backend.domain.fishingtrippost.dto.request.FishingTripPostRequest;
import com.backend.domain.fishingtrippost.dto.response.FishingTripPostResponse;
import com.backend.domain.fishingtrippost.entity.FishingTripPost;
import com.backend.domain.fishingtrippost.exception.FishingTripPostErrorCode;
import com.backend.domain.fishingtrippost.exception.FishingTripPostException;
import com.backend.domain.fishingtrippost.repository.FishingTripPostRepository;
import com.backend.domain.fishpoint.exception.FishPointErrorCode;
import com.backend.domain.fishpoint.exception.FishPointException;
import com.backend.domain.fishpoint.repository.FishPointRepository;
import com.backend.domain.member.exception.MemberErrorCode;
import com.backend.domain.member.exception.MemberException;
import com.backend.domain.member.repository.MemberRepository;
import com.backend.global.exception.GlobalException;
import com.backend.global.storage.entity.File;
import com.backend.global.storage.repository.StorageRepository;
import com.backend.global.storage.service.StorageService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class FishingTripPostServiceImpl implements FishingTripPostService {

	private final FishingTripPostRepository fishingTripPostRepository;
	private final MemberRepository memberRepository;
	private final FishPointRepository fishPointRepository;
	private final StorageService storageService;
	private final StorageRepository storageRepository;

	@Override
	@Transactional
	public Long createFishingTripPost(final Long memberId, final FishingTripPostRequest.Form requestDto) {
		// 멤버, 낚시 포인트 존재 검증
		validMemberAndFishPoint(memberId, requestDto);

		FishingTripPost fishingTripPost = FishingTripPostConverter.fromFishingTripPostCreate(memberId, requestDto);

		return fishingTripPostRepository.save(fishingTripPost).getFishingTripPostId();
	}

	@Override
	@Transactional
	public Long updateFishingTripPost(
		final Long memberId,
		final Long fishTripPostId,
		final FishingTripPostRequest.Form requestDto
	) {

		FishingTripPost fishingTripPost = getFishingTripPostById(fishTripPostId);

		// 동출 모집 게시글 작성자인지 검증
		validAuthor(fishingTripPost, memberId);

		// 기존 동출 게시글 이미지 ID 리스트
		List<Long> originalFileIdList = fishingTripPost.getFileIdList();

		// 새로 요청된 이미지 ID 리스트
		List<Long> newFileIdList = requestDto.fileIdList();

		// 삭제 대상 이미지 ID 추출
		List<Long> unusedFileIdList = originalFileIdList.stream()
			.filter(id -> !newFileIdList.contains(id))
			.toList();

		// 사용하지 않는 이미지 삭제
		if (!unusedFileIdList.isEmpty()) {
			storageService.deleteFilesByIdList(memberId, unusedFileIdList);
		}

		fishingTripPost.updateFishingTripPost(
			requestDto.subject(),
			requestDto.content(),
			requestDto.recruitmentCount(),
			requestDto.isShipFish(),
			requestDto.fishingDate(),
			requestDto.fishingPointId(),
			requestDto.fileIdList()
		);

		log.debug("[동출 모집 게시글 정보 수정] : {}", fishingTripPost);

		return fishingTripPost.getFishingPointId();
	}

	@Override
	public FishingTripPostResponse.Detail getFishingTripPostDetail(final Long fishingTripPostId) {
		//
		FishingTripPostResponse.DetailQueryDto detailQueryDto = getDetailDtoById(fishingTripPostId);

		List<String> fileUrlList = getFileUrlList(detailQueryDto);

		return FishingTripPostResponse.Detail.fromDetailQueryDtoAndFileUrlList(detailQueryDto, fileUrlList);
	}

	/**
	 * 상세 조회용 DTO에서 이미지 파일 ID 리스트를 기반으로 실제 이미지 URL 목록을 조회합니다.
	 *
	 * <p>저장소에서 파일 엔티티를 조회하고, 각 파일의 URL만 추출하여 리스트로 반환합니다.</p>
	 *
	 * @param detailQueryDto 동출 게시글 상세 정보가 담긴 DTO
	 * @return 이미지 URL 문자열 리스트
	 */
	private List<String> getFileUrlList(final FishingTripPostResponse.DetailQueryDto detailQueryDto) {
		return storageRepository.findAllById(detailQueryDto.fileIdList()).stream()
			.map(File::getUrl)
			.toList();
	}

	/**
	 * 주어진 게시글 ID를 기반으로 동출 게시글 상세 정보를 조회합니다.
	 *
	 * <p>해당 ID의 게시글이 존재하지 않을 경우 {@link FishingTripPostException} 예외를 발생시키며,
	 * 조회된 결과는 중간 응답 DTO {@link FishingTripPostResponse.DetailQueryDto} 형태로 반환됩니다.</p>
	 *
	 * @param fishingTripPostId 조회할 게시글의 고유 ID
	 * @return 조회된 상세 정보 DTO
	 * @throws FishingTripPostException 게시글이 존재하지 않는 경우
	 */
	private FishingTripPostResponse.DetailQueryDto getDetailDtoById(final Long fishingTripPostId) {
		FishingTripPostResponse.DetailQueryDto detailQueryDto =
			fishingTripPostRepository.findDetailQueryDtoById(fishingTripPostId)
				.orElseThrow(() -> new FishingTripPostException(FishingTripPostErrorCode.FISHING_TRIP_POST_NOT_FOUND));
		log.debug("[동출 모집 상세 조회 Dto 조회] : {}", detailQueryDto);
		return detailQueryDto;
	}

	/**
	 * 동출 모집 게시글 ID로 동출 게시글 엔티티를 조회하는 메소드
	 *
	 * @param fishingTripPostId 동출 모집 게시글의 ID
	 * @return {@link FishingTripPost} 조회된 동출 모집 게시글 엔티티
	 * @throws FishingTripPostException 동출 모집 게시글이 존재하지 않는 경우 예외 발생
	 */

	private FishingTripPost getFishingTripPostById(final Long fishingTripPostId) {

		FishingTripPost fishingTripPost = fishingTripPostRepository.findById(fishingTripPostId)
			.orElseThrow(() -> new FishingTripPostException(FishingTripPostErrorCode.FISHING_TRIP_POST_NOT_FOUND));
		log.debug("[동출 모집 게시글 조회] : {}", fishingTripPost);

		return fishingTripPost;
	}

	/**
	 * 로그인한 멤버와 낚시포인트 존재 검증 메서드
	 *
	 * @param memberId 검증할 회원 Id
	 * @throws MemberException    존재하지 않는 회원이면 예외 발생
	 * @throws FishPointException 존재하지 않는 낚시 포인트면 예외 발생
	 */

	private void validMemberAndFishPoint(final Long memberId, final FishingTripPostRequest.Form requestDto)
		throws GlobalException {

		if (!memberRepository.existsById(memberId)) {
			throw new MemberException(MemberErrorCode.MEMBER_NOT_FOUND);
		}

		if (!fishPointRepository.existsById(requestDto.fishingPointId())) {
			throw new FishPointException(FishPointErrorCode.FISH_POINT_NOT_FOUND);
		}
	}

	/**
	 * 로그인한 멤버와 동출 모집 게시글 작성자 동일 검증 메서드
	 *
	 * @param memberId 검증할 회원 Id
	 * @throws FishingTripPostException 동출 모집 게시글을 작성한 멤버가 아니면 예외 발생
	 */

	private void validAuthor(final FishingTripPost post, final Long memberId) {
		if (!post.getMemberId().equals(memberId)) {
			throw new FishingTripPostException(FishingTripPostErrorCode.FISHING_TRIP_POST_UNAUTHORIZED_AUTHOR);
		}
	}
}