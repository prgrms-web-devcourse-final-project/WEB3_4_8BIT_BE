package com.backend.global.event.domain;

import java.time.Instant;

import lombok.Getter;

@Getter
public abstract class DomainEvent {

	private final Instant createdAt = Instant.now();

}
