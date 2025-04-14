package com.backend.domain.shipfishingpost.event;

import java.util.List;

import com.backend.global.event.domain.DomainEvent;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ShipFishingPostDeleteEvent extends DomainEvent {

	private final Long shipFishingPostId;
	private final List<Long> fileIdList;
	private final Long memberId;

}
