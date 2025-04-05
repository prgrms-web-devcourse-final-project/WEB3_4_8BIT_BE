package com.backend.global.storage.repository;

import static com.backend.global.storage.entity.QFile.*;

import java.time.ZonedDateTime;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class StorageQueryRepository {

	private final JPAQueryFactory jpaQueryFactory;

	public Long deletePendingFilesBefore(final ZonedDateTime expirationTime) {
		return jpaQueryFactory
			.delete(file)
			.where(file.uploaded.eq(false)
				.and(file.createdAt.before(expirationTime)))
			.execute();
	}
}
