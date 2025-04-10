package com.backend.domain.chat.room.repository;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RoomQueryRepository {

	private final JPAQueryFactory jpaQueryFactory;
}
