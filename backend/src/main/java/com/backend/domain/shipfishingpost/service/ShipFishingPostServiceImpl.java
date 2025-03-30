package com.backend.domain.shipfishingpost.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShipFishingPostServiceImpl implements ShipFishingPostService {

	private final ShipRepository shipRepository;
	private final ShipFishingPostRepository shipFishingPostRepository;

	@Override
	@Transactional
	public Long saveShipFishingPost(final ShipFishingPostRequest.Create requestDto, final Long memberId) {

		ShipFishingPost shipFishingPost = ShipFishingPostConverter.fromShipFishPostsRequestCreate(requestDto, memberId);

		// shipId 등록 여부 & 선박 소유자 정보 일치 검증
		verifyShipOwnership(shipFishingPost.getShipId(), memberId);

		// Todo : 예약 불가날짜 관리 로직 필요
		// requestDto.unavailableDates();

		// Todo : Fish 랑 중간 테이블 생성 후 추가 로직 구현
		// requestDto.fishList();

		log.debug("Save ship fish posts: {}", shipFishingPost);

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
}