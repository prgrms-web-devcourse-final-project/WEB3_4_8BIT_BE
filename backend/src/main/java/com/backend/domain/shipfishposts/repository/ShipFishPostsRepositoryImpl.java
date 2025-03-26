package com.backend.domain.shipfishposts.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ShipFishPostsRepositoryImpl {

    private final JPAQueryFactory jpaQueryFactory;

}
