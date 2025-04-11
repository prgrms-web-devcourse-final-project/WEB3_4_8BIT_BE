package com.backend.domain.fishingtrippost.service;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.domain.chat.room.entity.TargetType;
import com.backend.domain.chat.room.service.RoomService;
import com.backend.domain.comment.repository.CommentRepository;
import com.backend.domain.fishingtrippost.converter.FishingTripPostConverter;
import com.backend.domain.fishingtrippost.domain.PostStatus;
import com.backend.domain.fishingtrippost.dto.request.FishingTripPostRequest;
import com.backend.domain.fishingtrippost.dto.response.FishingTripPostResponse;
import com.backend.domain.fishingtrippost.entity.FishingTripPost;
import com.backend.domain.fishingtrippost.exception.FishingTripPostErrorCode;
import com.backend.domain.fishingtrippost.exception.FishingTripPostException;
import com.backend.domain.fishingtrippost.notifier.FishingTripPostNotifier;
import com.backend.domain.fishingtrippost.repository.FishingTripPostRepository;
import com.backend.domain.fishingtriprecruitment.repository.FishingTripRecruitmentRepository;
import com.backend.domain.fishpoint.exception.FishPointErrorCode;
import com.backend.domain.fishpoint.exception.FishPointException;
import com.backend.domain.fishpoint.repository.FishPointRepository;
import com.backend.domain.like.domain.LikeTargetType;
import com.backend.domain.like.repository.LikeRepository;
import com.backend.domain.member.exception.MemberErrorCode;
import com.backend.domain.member.exception.MemberException;
import com.backend.domain.member.repository.MemberRepository;
import com.backend.global.dto.request.GlobalRequest;
import com.backend.global.dto.response.ScrollResponse;
import com.backend.global.exception.GlobalException;
import com.backend.global.storage.entity.File;
import com.backend.global.storage.repository.StorageRepository;
import com.backend.global.storage.service.StorageService;

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
	private final FishingTripPostNotifier fishingTripPostNotifier;
	private final LikeRepository likeRepository;
	private final FishingTripRecruitmentRepository fishingTripRecruitmentRepository;
	private final RoomService roomService;
	private final CommentRepository commentRepository;
	private final RedisTemplate<String, List<FishingTripPostResponse.HotPost>> hotPostRedisTemplate;

	private static final LikeTargetType TARGET_TYPE = LikeTargetType.FISHING_TRIP_POST;
	private static final String hotPostKey = "hotFishingTripPosts";

	@Override
	@Transactional
	public Long createFishingTripPost(final Long memberId, final FishingTripPostRequest.Form requestDto) {
		// 멤버, 낚시 포인트 존재 검증
		validMemberAndFishPoint(memberId, requestDto);

		FishingTripPost fishingTripPost = FishingTripPostConverter.fromCreate(memberId, requestDto);

		Long fishingTripPostId = fishingTripPostRepository.save(fishingTripPost).getFishingTripPostId();

		roomService.createRoom(fishingTripPostId, TargetType.FISHING_TRIP_POST);

		return fishingTripPostId;
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
	@Transactional(readOnly = true)
	public FishingTripPostResponse.Detail getFishingTripPostDetail(
		final Long memberId,
		final Long fishingTripPostId) {

		FishingTripPostResponse.DetailQueryDto detailQueryDto = getDetailDtoById(fishingTripPostId);

		List<String> fileUrlList = getFileUrlList(detailQueryDto);

		boolean isLiked = getIsLiked(memberId, fishingTripPostId);

		FishingTripPostResponse.Detail responseDto = FishingTripPostConverter.toDetail(detailQueryDto, fileUrlList,
			isLiked);
		log.debug("[동출 상세보기] : 조회 성공");

		return responseDto;
	}

	@Override
	@Transactional
	public void completeFishingTripPost(final Long memberId, final Long fishingTripPostId) {

		FishingTripPost fishingTripPost = getFishingTripPostById(fishingTripPostId);

		validAuthor(fishingTripPost, memberId);

		fishingTripPost.setPostStatus(PostStatus.COMPLETED);
		fishingTripPostNotifier.notifyMailIfCompleted(fishingTripPost);
	}

	@Override
	@Transactional(readOnly = true)
	public ScrollResponse<FishingTripPostResponse.DetailPage> getDetailPage(
		final GlobalRequest.CursorRequest cursorRequestDto,
		final PostStatus status,
		final Long regionId,
		final String keyword) {
		List<FishingTripPostResponse.DetailPageQueryDto> detailPageDto = fishingTripPostRepository.findScrollDetailPageDto(
			cursorRequestDto, status, regionId, keyword);

		boolean isLast = detailPageDto.size() <= cursorRequestDto.size();
		if (!isLast)
			detailPageDto.remove(detailPageDto.size() - 1);

		List<FishingTripPostResponse.DetailPage> result = detailPageDto.stream()
			.map(dto -> FishingTripPostConverter.toDetailPage(dto, this::getImageUrlById))
			.toList();

		log.debug("[동출 전체보기] : 조회 성공");

		return ScrollResponse.from(
			result,
			cursorRequestDto.size(),
			result.size(),
			cursorRequestDto.fieldValue() == null,
			isLast
		);
	}

	@Override
	@Transactional(readOnly = true)
	public FishingTripPostResponse.FishingTripPostParticipationDetail getFishingTripPostParticipationDetail(
		final Long memberId, final Long fishingTripPostId) {

		FishingTripPostResponse.ParticipantDetailDto participantDetailDto = fishingTripPostRepository.findParticipantDetailDto(
			fishingTripPostId, memberId);
		List<FishingTripPostResponse.ParticipantDetail> participants = fishingTripPostRepository.findApprovedParticipants(
			fishingTripPostId);
		FishingTripPostResponse.FishingTripPostParticipationDetail responseDto = FishingTripPostConverter.toParticipationDetail(
			participantDetailDto, participants);
		log.debug("[동출 인원 상세정보 조회] : 조회 성공");

		return responseDto;
	}

	@Override
	@Transactional
	public void delete(final Long memberId, final Long fishingTripPostId) {

		FishingTripPost fishingTripPost = getFishingTripPostById(fishingTripPostId);
		validAuthor(fishingTripPost, memberId);

		fishingTripRecruitmentRepository.deleteAllByPostId(fishingTripPostId);
		fishingTripPostRepository.delete(fishingTripPost);
		commentRepository.deleteByFishingTripPostId(fishingTripPostId);

		log.debug("[동출 게시글 삭제] : 삭제 성공");
	}

	@Override
	@Transactional(readOnly = true)
	public ScrollResponse<FishingTripPostResponse.MyFishingTripPostDetailPage> getMyFishingTripPostDetailPage(
		final GlobalRequest.CursorRequest cursorRequestDto,
		final Long memberId,
		final PostStatus postStatus) {
		ScrollResponse<FishingTripPostResponse.MyFishingTripPostDetailPage> responseDto = fishingTripPostRepository.findMyFishingTripRecruitmentDetailPage(
			cursorRequestDto, postStatus, memberId);
		log.debug("[마이페이지 내가 신청한 동출 조회] : 조회 성공");
		return responseDto;
	}

	@Override
	@Transactional(readOnly = true)
	public ScrollResponse<FishingTripPostResponse.MyFishingTripPostDetailPage> getMyPostFishingTripPostDetailPage(
		final GlobalRequest.CursorRequest cursorRequestDto,
		final Long memberId,
		final PostStatus postStatus) {
		ScrollResponse<FishingTripPostResponse.MyFishingTripPostDetailPage> responseDto = fishingTripPostRepository.findMyPostFishingTripPostDetailPage(
			cursorRequestDto, postStatus, memberId);
		log.debug("[마이페이지 내 동출게시글 조회] : 조회 성공");
		return responseDto;
	}

	@Override
	@Transactional(readOnly = true)
	public List<FishingTripPostResponse.HotPost> getHotPost() {

		List<FishingTripPostResponse.HotPost> cached = hotPostRedisTemplate.opsForValue().get(hotPostKey);
		if (cached != null) {
			log.debug("[인기 동출글 조회] : 캐시에서 가져옴");
			return cached;
		}

		ZonedDateTime baseTime = ZonedDateTime.now(ZoneId.of("Asia/Seoul"))
			.minusDays(5)
			.withHour(0).withMinute(0).withSecond(0).withNano(0);

		List<FishingTripPostResponse.HotPostDto> hotPostDtoList = fishingTripPostRepository.findHotPostDto(baseTime);

		List<FishingTripPostResponse.HotPost> responseDto = getHotPostList(hotPostDtoList);

		log.debug("[인기 동출글 조회] : 직접 조회");

		hotPostRedisTemplate.opsForValue().set(hotPostKey, responseDto, Duration.ofMinutes(30));
		log.debug("[인기 동출글 조회] : 캐사에 추가");

		return responseDto;
	}

	/**
	 * 주어진 리스트의 HotPostDto 객체들을 HotPost 객체로 변환하여 반환하는 메서드입니다.
	 *
	 * @param hotPostDtoList HotPostDto 객체들을 담고 있는 리스트. 각 HotPostDto에는 낚시 게시글의 ID, 제목, 지역 정보,
	 *                       이미지 파일 ID 리스트, 인기 점수 등의 정보가 포함됩니다.
	 * @return HotPost 객체로 변환된 리스트. 각 HotPost는 주어진 HotPostDto에서 변환된 데이터로 구성됩니다.
	 *         이미지 URL은 파일 ID 리스트가 비어 있지 않으면 첫 번째 파일 ID를 기준으로 가져옵니다.
	 */
	private List<FishingTripPostResponse.HotPost> getHotPostList(List<FishingTripPostResponse.HotPostDto> hotPostDtoList) {
		return hotPostDtoList.stream()
			.map(dto -> {
				String imageUrl = (dto.fileIdList() != null && !dto.fileIdList().isEmpty())
					? getImageUrlById(dto.fileIdList().get(0))
					: null;

				return new FishingTripPostResponse.HotPost(
					dto.fishingTripPostId(),
					dto.subject(),
					dto.regionId(),
					dto.regionType(),
					imageUrl,
					dto.hotScore()
				);
			})
			.toList();
	}

	/**
	 * 현재 로그인한 사용자가 해당 게시글에 '좋아요'를 눌렀는지 여부를 반환합니다.
	 *
	 * <p>사용자가 로그인된 상태(memberId != null)일 때만 {@link LikeRepository}를 통해
	 * 게시글 ID와 사용자 ID 기반으로 좋아요 여부를 조회합니다.</p>
	 *
	 * @param memberId 현재 로그인한 사용자의 ID (비로그인 시 null)
	 * @param fishingTripPostId 대상 게시글의 ID
	 * @return 사용자가 해당 게시글을 좋아요 했으면 true, 아니면 false
	 */
	private boolean getIsLiked(final Long memberId, final Long fishingTripPostId) {
		return (memberId != null) &&
			likeRepository.existsByMemberIdAndTargetTypeAndTargetId(memberId, TARGET_TYPE, fishingTripPostId);
	}

	/**
	 * 파일 ID를 통해 해당 파일의 이미지 URL을 조회합니다.
	 *
	 * <p> 파일을 조회하고, 존재할 경우 해당 파일의 URL을 반환합니다.
	 * 파일이 존재하지 않으면 {@code null}을 반환합니다.</p>
	 *
	 * @param fileId 조회할 파일의 ID
	 * @return 파일이 존재하면 해당 파일의 URL, 존재하지 않으면 {@code null}
	 */
	private String getImageUrlById(final Long fileId) {
		return storageRepository.findById(fileId)
			.map(File::getUrl)
			.orElse(null);
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