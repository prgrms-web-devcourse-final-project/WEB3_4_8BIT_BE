package com.backend.global.event.publisher;

import com.backend.global.event.domain.DomainEvent;

public interface DomainEventPublisher {

	void publish(final DomainEvent event);
}
