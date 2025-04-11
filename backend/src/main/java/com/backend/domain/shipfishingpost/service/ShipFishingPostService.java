package com.backend.domain.shipfishingpost.service;

import java.util.List;

import com.backend.domain.shipfishingpost.dto.request.ShipFishingPostRequest;
import com.backend.domain.shipfishingpost.dto.response.ShipFishingPostResponse;
import com.backend.global.dto.request.GlobalRequest;
import com.backend.global.dto.response.ScrollResponse;

public interface ShipFishingPostService {

	/**
	 * 선상 낚시 게시글 저장 메서드
	 *
	 * @param requestDto {@link ShipFishingPostRequest.Create}
	 * @return {@link Long ShipFishPostId} Long: 게시글 id
	 * @implSpec 선상 낚시 게시글 정보를 파라미터로 받고 저장한다.
	 * @author swjoon
	 */
	Long createShipFishingPost(final ShipFishingPostRequest.Create requestDto, final Long memberId);

	/**
	 * 내가 작성한 게시글 목록 조회 메서드
	 *
	 * @param memberId 유저 id
	 * @return {@link List<ShipFishingPostResponse.MyPagePostList>}
	 * @implSpec 본인이 작성한 선상 낚시 게시글 목록을 조회한다.
	 * @author swjoon
	 */
	List<ShipFishingPostResponse.MyPagePostList> getMyPageShipFishingPostList(final Long memberId);

	/**
	 * 선상 낚시 게시글 상세 조회 메서드 (게시글, 멤버, 선박 정보 포함)
	 *
	 * @param shipFishingPostId {@link Long}
	 * @return {@link ShipFishingPostResponse.DetailAll}
	 * @implSpec 선상 낚시 게시글 번호를 파라미터로 받고 조회한다.
	 * @author swjoon
	 */
	ShipFishingPostResponse.DetailWithFileUrlAndFishName getShipFishingPostAll(final Long shipFishingPostId);

	/**
	 * 선상 낚시 게시글 조회 메서드
	 *
	 * @param searchDto {@link ShipFishingPostRequest.Search}
	 * @param cursorRequestDto {@link GlobalRequest.CursorRequest}
	 * @return {@link ScrollResponse<ShipFishingPostResponse.DetailScroll>}
	 * @implSpec 선상 낚시 게시글을 검색합니다.
	 * @author swjoon
	 */
	ScrollResponse<ShipFishingPostResponse.DetailScroll> getShipFishingPostScroll(
		final ShipFishingPostRequest.Search searchDto,
		final GlobalRequest.CursorRequest cursorRequestDto);

	/**
	 * 메인페이지에 인기 게시글 목록 조회 메서드
	 *
	 * @param size 조회할 데이터 사이즈
	 * @return {@link List<ShipFishingPostResponse.MainPageHotPost>}
	 * @implSpec 메인페이지에 보여줄 인기 게시글 목록을 사이즈에 맞게 제공하는 메서드입니다.
	 */
	List<ShipFishingPostResponse.MainPageHotPost> getMainPageHotShipFishingPostList(final Integer size);

	/**
	 * 선상 낚시 게시글 업데이트 메서드
	 *
	 * @param shipFishingPostId 선상 낚시 게시글
	 * @param requestDto 업데이트 내용
	 * @param memberId 로그인 유저 정보
	 * @return 업데이트한 선상낚시 게시글 Id
	 * @implSpec 선상낚시 게시글 id 와 업데이트 내용을 받아 검증 후 업데이트를 진행합니다.
	 * @author swjoon
	 */
	Long updateShipFishingPost(
		final Long shipFishingPostId,
		final ShipFishingPostRequest.Update requestDto,
		final Long memberId);

	/**
	 * 선상 낚시 게시글 삭제 메서드
	 *
	 * @param shipFishingPostId {@link Long}
	 * @param memberId {@link Long}
	 * @implSpec 입력된 선상 낚시 게시글의 예약 내역을 검증하고 삭제하는 메서드 입니다.
	 * @author swjoon
	 */
	void deleteShipFishingPost(final Long shipFishingPostId, final Long memberId);
}