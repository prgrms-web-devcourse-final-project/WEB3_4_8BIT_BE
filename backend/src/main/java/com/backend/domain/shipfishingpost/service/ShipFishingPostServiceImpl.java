package com.backend.domain.shipfishingpost.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

	private final ShipFishingPostRepository shipFishingPostRepository;

	@Override
	@Transactional
	public Long save(final ShipFishingPostRequest.Create requestDto) {

		ShipFishingPost shipFishingPost = ShipFishingPostConverter.fromShipFishPostsRequestCreate(requestDto);

		// Todo : shipId로 등록 여부 & 본인 여부 검증

		// Todo : 예약 불가날짜 관리 로직 필요
		// requestDto.unavailableDates();

		// Todo : Fish 랑 중간 테이블 생성 후 추가 로직 구현

		log.debug("Save ship fish posts: {}", shipFishingPost);

		return shipFishingPostRepository.save(shipFishingPost).getShipFishingPostId();
	}

	@Override
	public ShipFishingPostResponse.Detail getShipFishingPost(final Long shipFishingPostId) {

		return shipFishingPostRepository.findDetailById(shipFishingPostId)
			.orElseThrow(() -> new ShipFishingPostException(ShipFishingPostErrorCode.POSTS_NOT_FOUND));
	}
}