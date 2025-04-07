package com.backend.domain.member.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import static com.backend.domain.member.entity.QMember.*;
import static com.backend.global.storage.entity.QFile.*;

import com.backend.domain.member.dto.MemberResponse;
import com.backend.domain.member.dto.QMemberResponse_Detail;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MemberQueryRepository {

	private final JPAQueryFactory queryFactory;

	public Optional<MemberResponse.Detail> findDetailById(Long memberId) {
		MemberResponse.Detail detail = queryFactory
			.select(new QMemberResponse_Detail(
				member.memberId,
				member.email,
				member.name,
				member.nickname,
				member.phone,
				file.url,
				member.description,
				member.isAddInfo
			))
			.from(member)
			.leftJoin(file)
			.on(member.fileId.eq(file.fileId))
			.where(member.memberId.eq(memberId))
			.fetchOne();

		return Optional.ofNullable(detail);
	}
}
