package com.backend.global.event.publisher;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.backend.global.event.domain.DomainEvent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class InMemoryDomainEventPublisher implements DomainEventPublisher {

	private final ApplicationEventPublisher eventPublisher;

	@Override
	public void publish(final DomainEvent event) {

		eventPublisher.publishEvent(event);
	}
}
