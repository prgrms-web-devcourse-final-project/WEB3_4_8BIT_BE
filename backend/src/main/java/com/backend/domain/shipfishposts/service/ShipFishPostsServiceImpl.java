package com.backend.domain.shipfishposts.service;

import java.time.Duration;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.domain.shipfishposts.dto.request.ShipFishPostsRequest;
import com.backend.domain.shipfishposts.entity.ShipFishPosts;
import com.backend.domain.shipfishposts.repository.ShipFishPostsRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ShipFishPostsServiceImpl implements ShipFishPostsService {

	private final ShipFishPostsRepository shipFishPostsRepository;

	@Override
	@Transactional
	public Long save(ShipFishPostsRequest.Create requestDto) {

		long durationMinutes = Duration.between(requestDto.startDate(), requestDto.endDate()).toMinutes();

		ShipFishPosts entity = ShipFishPosts.from(requestDto, durationMinutes);

		return shipFishPostsRepository.save(entity).getShipFishPostId();
	}
}