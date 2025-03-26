package com.backend.domain.member.repository;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MembersQueryRepository {

	private JPAQueryFactory queryFactory;

}
