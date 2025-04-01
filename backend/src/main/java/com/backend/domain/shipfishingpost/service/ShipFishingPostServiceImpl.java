package com.backend.domain.shipfishingpost.service;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.domain.fish.entity.Fish;
import com.backend.domain.fish.exception.FishErrorCode;
import com.backend.domain.fish.exception.FishException;
import com.backend.domain.fish.repository.FishRepository;
import com.backend.domain.ship.entity.Ship;
import com.backend.domain.ship.exception.ShipErrorCode;
import com.backend.domain.ship.exception.ShipException;
import com.backend.domain.ship.repository.ShipRepository;
import com.backend.domain.shipfishingpost.converter.ShipFishingPostConverter;
import com.backend.domain.shipfishingpost.dto.request.ShipFishingPostRequest;
import com.backend.domain.shipfishingpost.dto.response.ShipFishingPostResponse;
import com.backend.domain.shipfishingpost.entity.ShipFishingPost;
import com.backend.domain.shipfishingpost.exception.ShipFishingPostErrorCode;
import com.backend.domain.shipfishingpost.exception.ShipFishingPostException;
import com.backend.domain.shipfishingpost.repository.ShipFishingPostRepository;
import com.backend.domain.shipfishingpostfish.repository.ShipFishingPostFishRepository;
import com.backend.global.dto.request.GlobalRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShipFishingPostServiceImpl implements ShipFishingPostService {

	private final FishRepository fishRepository;
	private final ShipRepository shipRepository;
	private final ShipFishingPostRepository shipFishingPostRepository;
	private final ShipFishingPostFishRepository shipFishingPostFishRepository;

	@Override
	@Transactional
	public Long saveShipFishingPost(final ShipFishingPostRequest.Create requestDto, final Long memberId) {

		ShipFishingPost shipFishingPost = ShipFishingPostConverter.fromShipFishPostsRequestCreate(requestDto, memberId);

		// shipId 등록 여부 & 선박 소유자 정보 일치 검증
		verifyShipOwnership(shipFishingPost.getShipId(), memberId);

		verifyFishList(shipFishingPost.getFishList());

		// Todo : 예약 불가날짜 관리 로직 필요
		// requestDto.unavailableDates();

		log.debug("Save ship fish posts: {}", shipFishingPost);

		shipFishingPostRepository.save(shipFishingPost);

		return shipFishingPostRepository.save(shipFishingPost).getShipFishingPostId();
	}

	@Override
	public ShipFishingPostResponse.Detail getShipFishingPost(final Long shipFishingPostId) {

		return shipFishingPostRepository.findDetailById(shipFishingPostId)
			.orElseThrow(() -> new ShipFishingPostException(ShipFishingPostErrorCode.POSTS_NOT_FOUND));
	}

	@Override
	public ShipFishingPostResponse.DetailAll getShipFishingPostAll(final Long shipFishingPostId) {

		return shipFishingPostRepository.findDetailAllById(shipFishingPostId)
			.orElseThrow(() -> new ShipFishingPostException(ShipFishingPostErrorCode.POSTS_NOT_FOUND));
	}

	@Override
	public Slice<ShipFishingPostResponse.DetailPage> getShipFishingPostPage(
		final ShipFishingPostRequest.Search requestDto,
		final GlobalRequest.PageRequest pageRequestDto) {
		Pageable pageable = convertPageable(pageRequestDto);
		return shipFishingPostRepository.findAllBySearchAndCondition(requestDto, pageable);
	}

	/**
	 * shipId 등록 여부 & 선박 소유자 정보 일치 검증 메서드
	 *
	 * @param shipId {@link Long}
	 * @param memberId {@link Long}
	 */
	private void verifyShipOwnership(final Long shipId, final Long memberId) {

		Ship ship = shipRepository.findById(shipId)
			.orElseThrow(() -> new ShipException(ShipErrorCode.SHIP_NOT_FOUND));

		if (!ship.getMemberId().equals(memberId)) {
			throw new ShipException(ShipErrorCode.SHIP_MISMATCH_MEMBER_ID);
		}
	}

	/**
	 * 저장하려는 물고기 정보가 있는지 검증 메서드
	 *
	 * @param fishList {@link List}
	 */
	private void verifyFishList(final List<Long> fishList) {
		List<Fish> findFishList = fishRepository.findAllById(fishList);
		if (findFishList.size() != fishList.size()) {
			throw new FishException(FishErrorCode.FISH_NOT_FOUND);
		}
	}

	/**
	 * pageRequestDTO -> pageable
	 *
	 * @param pageRequestDto {@link }
	 * @return {@link Pageable}
	 */
	private Pageable convertPageable(final GlobalRequest.PageRequest pageRequestDto) {
		return PageRequest.of(
			pageRequestDto.page(),
			pageRequestDto.size(),
			Sort.by(Sort.Direction.fromString(
					pageRequestDto.order().equals("asc") ? "asc" : "desc"),
				pageRequestDto.sort()));
	}
}