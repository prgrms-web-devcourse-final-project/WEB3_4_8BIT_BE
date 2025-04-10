package com.backend.domain.comment.repository;

import static com.backend.domain.comment.entity.QComment.*;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CommentQueryRepository {

	private final JPAQueryFactory jpaQueryFactory;

	public void addChildCount(final Long parentId) {
		// 원자적 연산으로 동시성 문제 방지
		jpaQueryFactory
			.update(comment)
			.set(comment.childCount, comment.childCount.add(1))
			.where(comment.commentId.eq(parentId))
			.execute();
	}
}
