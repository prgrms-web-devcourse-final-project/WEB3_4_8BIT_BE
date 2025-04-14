package com.backend.domain.fishingtrippost.service;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.domain.activityhistory.service.ActivityHistoryService;
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
import com.backend.global.util.RedisUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class FishingTripPostServiceImpl implements FishingTripPostService {

	private final RoomService roomService;
	private final ActivityHistoryService activityHistoryService;

	private final FishingTripPostRepository fishingTripPostRepository;
	private final MemberRepository memberRepository;
	private final FishPointRepository fishPointRepository;
	private final StorageService storageService;
	private final StorageRepository storageRepository;
	private final FishingTripPostNotifier fishingTripPostNotifier;
	private final LikeRepository likeRepository;
	private final FishingTripRecruitmentRepository fishingTripRecruitmentRepository;
	private final CommentRepository commentRepository;
	private final RedisUtil redisUtil;
	private final RedisTemplate<String, List<FishingTripPostResponse.HotPost>> hotPostRedisTemplate;

	private static final LikeTargetType TARGET_TYPE = LikeTargetType.FISHING_TRIP_POST;
	private static final String hotPostKey = "hotFishingTripPosts";

	@Override
	@Transactional
	public Long createFishingTripPost(final Long memberId, final FishingTripPostRequest.Create requestDto) {
		// 멤버, 낚시 포인트 존재 검증
		validMemberAndFishPoint(memberId, requestDto);

		FishingTripPost fishingTripPost = FishingTripPostConverter.fromCreate(memberId, requestDto);

		FishingTripPost savedFishingTripPost = fishingTripPostRepository.save(fishingTripPost);

		Long fishingTripPostId = savedFishingTripPost.getFishingTripPostId();

		roomService.createRoom(fishingTripPostId, TargetType.FISHING_TRIP_POST);

		activityHistoryService.createActivityHistory(savedFishingTripPost);

		return fishingTripPostId;
	}

	@Override
	@Transactional
	public Long updateFishingTripPost(
		final Long memberId,
		final Long fishTripPostId,
		final FishingTripPostRequest.Update requestDto
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
			requestDto.fileIdList()
		);

		log.debug("[동출 모집 게시글 정보 수정] : {}", fishingTripPost);

		return fishingTripPost.getFishingTripPostId();
	}

	@Override
	@Transactional(readOnly = true)
	public FishingTripPostResponse.Detail getFishingTripPostDetail(
		final Long memberId,
		final Long fishingTripPostId) {

		FishingTripPostResponse.DetailQueryDto detailQueryDto = getDetailDtoById(fishingTripPostId);

		Map<Long, String> fileUrlMap = getFileUrlMap(detailQueryDto);

		boolean isLiked = getIsLiked(memberId, fishingTripPostId);
		boolean isPostOwner = getIsPostOwner(memberId, fishingTripPostId);
		FishingTripPostResponse.Detail responseDto = FishingTripPostConverter.toDetail(detailQueryDto,
			isLiked, isPostOwner, fileUrlMap);
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
		final Long memberId,
		final PostStatus status,
		final Long regionId,
		final String keyword
	) {
		List<FishingTripPostResponse.DetailPageQueryDto> detailPageDto =
			fishingTripPostRepository.findScrollDetailPageDto(cursorRequestDto, status, regionId, keyword);

		boolean isLast = detailPageDto.size() <= cursorRequestDto.size();
		if (!isLast) {
			detailPageDto.remove(detailPageDto.size() - 1);
		}

		// 좋아요 눌린 게시글 Id 리스트
		Set<Long> likedPostIds = getLikedPostIdSet(memberId, detailPageDto);

		List<FishingTripPostResponse.DetailPage> responseDto = detailPageDto.stream()
			.map(dto -> {
				boolean isLiked = likedPostIds.contains(dto.fishingTripPostId());
				return FishingTripPostConverter.toDetailPage(dto, this::getImageUrlById, isLiked);
			})
			.toList();

		log.debug("[동출 전체보기] : 조회 성공");

		return ScrollResponse.from(
			responseDto,
			cursorRequestDto.size(),
			responseDto.size(),
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

		// 동출 게시글 삭제시 게시글 좋아요 List와 캐싱 값 제거
		likeRepository.deleteLikesByTargetTypeAndTargetId(LikeTargetType.FISHING_TRIP_POST, fishingTripPostId);
		redisUtil.deleteKeyIfExists("like_count::FISHING_TRIP_POST::" + fishingTripPostId);

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
		log.debug("[인기 동출글 조회] : 캐시에 추가");

		return responseDto;
	}

	/**
	 * 주어진 리스트의 HotPostDto 객체들을 HotPost 객체로 변환하여 반환하는 메서드입니다.
	 *
	 * @param hotPostDtoList HotPostDto 객체들을 담고 있는 리스트. 각 HotPostDto에는 낚시 게시글의 ID, 제목, 지역 정보,
	 *                       이미지 파일 ID 리스트, 인기 점수 등의 정보가 포함됩니다.
	 * @return HotPost 객체로 변환된 리스트. 각 HotPost는 주어진 HotPostDto에서 변환된 데이터로 구성됩니다.
	 * 이미지 URL은 파일 ID 리스트가 비어 있지 않으면 첫 번째 파일 ID를 기준으로 가져옵니다.
	 */
	private List<FishingTripPostResponse.HotPost> getHotPostList(
		final List<FishingTripPostResponse.HotPostDto> hotPostDtoList
	) {
		return hotPostDtoList.stream()
			.map(dto -> new FishingTripPostResponse.HotPost(
				dto.fishingTripPostId(),
				dto.subject(),
				dto.regionId(),
				dto.regionType(),
				getFirstImageUrl(dto.fileIdList()),
				dto.hotScore()
			))
			.toList();
	}

	/**
	 * 현재 로그인한 사용자가 해당 게시글에 '좋아요'를 눌렀는지 여부를 반환합니다.
	 *
	 * <p>사용자가 로그인된 상태(memberId != null)일 때만 {@link LikeRepository}를 통해
	 * 게시글 ID와 사용자 ID 기반으로 좋아요 여부를 조회합니다.</p>
	 *
	 * @param memberId          현재 로그인한 사용자의 ID (비로그인 시 null)
	 * @param fishingTripPostId 대상 게시글의 ID
	 * @return 사용자가 해당 게시글을 좋아요 했으면 true, 아니면 false
	 * @implSpec soft delete 좋아요는 제외함 (isDeleted = false 조건 포함)
	 */
	private boolean getIsLiked(final Long memberId, final Long fishingTripPostId) {
		return (memberId != null) &&
			likeRepository.existsByMemberIdAndTargetTypeAndTargetIdAndIsDeletedFalse(memberId, TARGET_TYPE,
				fishingTripPostId);
	}

	/**
	 * 현재 로그인한 사용자가 좋아요를 누른 게시글 ID 목록을 조회합니다.
	 *
	 * <p>비로그인 상태(memberId == null)일 경우 빈 Set을 반환합니다.</p>
	 * <p>리스트 조회 성능 최적화를 위해 QueryDSL로 ID 목록을 한 번에 가져옵니다.</p>
	 * <p>쿼리 레벨에서 distinct 처리되며, 이 메서드에서는 contains 성능을 위해 Set 변환만 수행합니다.</p>
	 *
	 * @param memberId 현재 로그인한 사용자 ID (nullable)
	 * @param posts    게시글 리스트 (DetailPageQueryDto) - 좋아요 대상이 될 게시글들
	 * @return 좋아요를 누른 게시글의 ID 목록 (Set)
	 */
	private Set<Long> getLikedPostIdSet(
		final Long memberId,
		final List<FishingTripPostResponse.DetailPageQueryDto> posts
	) {
		if (memberId == null || posts.isEmpty()) {
			return Set.of();
		}

		List<Long> targetIds = posts.stream()
			.map(FishingTripPostResponse.DetailPageQueryDto::fishingTripPostId)
			.toList();

		return new HashSet<>(likeRepository.findLikedTargetIdsByMemberIdAndTargetType(
			memberId,
			LikeTargetType.FISHING_TRIP_POST,
			targetIds
		));
	}

	/**
	 * 파일 ID 리스트 중 첫 번째 파일의 URL을 가져옵니다.
	 * <p>비어 있거나 null일 경우 null 반환</p>
	 */
	private String getFirstImageUrl(final List<Long> fileIdList) {
		if (fileIdList == null || fileIdList.isEmpty()) {
			return null;
		}
		Long firstFileId = fileIdList.get(0);
		if (firstFileId == null) {
			return null;
		}
		return getImageUrlById(firstFileId);
	}

	/**
	 * 파일 ID를 통해 해당 파일의 이미지 URL을 조회합니다.
	 *
	 * <p> 파일을 조회하고, 존재할 경우 해당 파일의 URL을 반환합니다.
	 * 파일이 존재하지 않으면 {@code null}을 반환합니다.</p>
	 *
	 * @param fileId 조회할 파일의 ID
	 * @return 파일이 존재하면 해당 파일의 URL, 존재하지 않으면 {@code null}
	 * @implSpec 존재하지 않는 파일 ID일 경우 예외 없이 null 처리
	 */
	private String getImageUrlById(final Long fileId) {
		if (fileId == null)
			return null;
		return storageRepository.findById(fileId)
			.map(File::getUrl)
			.orElse(null);
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

	private void validMemberAndFishPoint(final Long memberId, final FishingTripPostRequest.Create requestDto)
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

	/**
	 * 주어진 사용자가 특정 동출 모집 게시글의 작성자인지 여부를 판단하는 메서드입니다.
	 *
	 * <p>memberId가 null이 아닌 경우에만 게시글 작성자 여부를 확인합니다.</p>
	 *
	 * @param memberId          확인할 회원 ID (null일 경우 false 반환)
	 * @param fishingTripPostId 확인할 동출 모집 게시글 ID
	 * @return true: 해당 게시글의 작성자인 경우<br>
	 * false: memberId가 null이거나, 작성자가 아닌 경우
	 */

	private boolean getIsPostOwner(final Long memberId, final Long fishingTripPostId) {
		return memberId != null && fishingTripPostRepository.existFishingTripPostByMemberIdAndPostId(memberId,
			fishingTripPostId);
	}

	/**
	 * 상세 조회용 DTO에서 이미지 파일 ID 리스트를 기반으로 실제 이미지 ID-URL 매핑 정보를 조회합니다.
	 *
	 * <p>저장소에서 파일 엔티티를 조회하고, 각 파일의 ID와 URL을 Map 형태로 반환합니다.</p>
	 *
	 * @param detailQueryDto 동출 게시글 상세 정보가 담긴 DTO
	 * @return 이미지 ID → URL 매핑 정보
	 */
	private Map<Long, String> getFileUrlMap(final FishingTripPostResponse.DetailQueryDto detailQueryDto) {
		List<Long> fileIdList = detailQueryDto.fileIdList();
		if (fileIdList == null || fileIdList.isEmpty()) {
			return Collections.emptyMap();
		}

		return storageRepository.findAllById(fileIdList).stream()
			.collect(Collectors.toMap(
				File::getFileId,
				File::getUrl
			));
	}
}