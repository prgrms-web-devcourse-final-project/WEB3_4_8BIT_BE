package com.backend.domain.captain.repository;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CaptainQueryRepository {

	private JPAQueryFactory queryFactory;
}
