package com.backend.domain.shipfishingpost.asyncservice;

import com.backend.domain.shipfishingpost.event.ShipFishingPostDeleteEvent;

public interface ShipFishingPostAsyncService {

	/**
	 * 선상낚시 게시글을 삭제할때 연관된 도메인들의 데이터를 비동기로 지우는 메서드
	 *
	 * @param event 게시글 삭제 이벤트 데이터
	 * @implSpec 삭제하려는 선상낚시 게시글과 연관 되어있는 데이터들을 비동기롤 전부 지우는 메서드입니다.
	 */
	void deleteRelatedDomainWithShipFishingPost(final ShipFishingPostDeleteEvent event);

}
