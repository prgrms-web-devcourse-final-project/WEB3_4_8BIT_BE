package com.backend.domain.shipfishingpost.asyncservice;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.backend.domain.like.domain.LikeTargetType;
import com.backend.domain.like.repository.LikeRepository;
import com.backend.domain.reservationdate.repository.ReservationDateRepository;
import com.backend.domain.review.repository.ReviewRepository;
import com.backend.domain.shipfishingpost.event.ShipFishingPostDeleteEvent;
import com.backend.global.storage.service.S3StorageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShipFishingPostAsyncServiceImpl implements ShipFishingPostAsyncService {

	private final S3StorageService s3StorageService;

	private final LikeRepository likeRepository;
	private final ReviewRepository reviewRepository;
	private final ReservationDateRepository reservationDateRepository;

	@Async("threadPoolTaskExecutor")
	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void deleteRelatedDomainWithShipFishingPost(final ShipFishingPostDeleteEvent event) {

		log.debug("이벤트 발행시간 : {} \n 게시글 ID : {}", event.getCreatedAt(), event.getShipFishingPostId());

		Long shipFishingPostId = event.getShipFishingPostId();

		reservationDateRepository.deleteByShipFishingPostId(shipFishingPostId);

		s3StorageService.deleteFilesByIdList(event.getMemberId(), event.getFileIdList());

		reviewRepository.deleteAllByShipFishingPostId(shipFishingPostId);

		likeRepository.deleteLikesByTargetTypeAndTargetId(LikeTargetType.SHIP_FISHING_POST, shipFishingPostId);
	}
}
