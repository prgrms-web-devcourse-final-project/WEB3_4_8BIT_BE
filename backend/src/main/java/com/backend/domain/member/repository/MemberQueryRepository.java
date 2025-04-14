package com.backend.domain.member.repository;

import static com.backend.domain.member.entity.QMember.*;
import static com.backend.global.storage.entity.QFile.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.backend.domain.member.dto.MemberResponse;
import com.backend.domain.member.dto.QMemberResponse_Detail;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MemberQueryRepository {

	private final JPAQueryFactory queryFactory;

	public Optional<MemberResponse.Detail> findDetailById(final Long memberId) {
		MemberResponse.Detail detail = queryFactory
			.select(new QMemberResponse_Detail(
				member.memberId,
				member.email,
				member.name,
				member.nickname,
				member.phone,
				file.url,
				file.fileId,
				member.description,
				member.role,
				member.isAddInfo
			))
			.from(member)
			.leftJoin(file)
			.on(member.fileId.eq(file.fileId))
			.where(member.memberId.eq(memberId))
			.fetchOne();

		return Optional.ofNullable(detail);
	}

	public List<String> findEmailListByIdList(final List<Long> memberIdList) {
		return queryFactory
			.select(member.email)
			.from(member)
			.where(member.memberId.in(memberIdList))
			.fetch();
	}

	public Map<Long, String> getFileUrlMapByIdList(final Set<Long> memberIdList) {
		return queryFactory
			.select(member.memberId, file.url)
			.from(member)
			.join(file).on(member.fileId.eq(file.fileId))
			.where(member.memberId.in(memberIdList))
			.fetch()
			.stream()
			.collect(Collectors.toMap(
				tuple -> tuple.get(member.memberId),
				tuple -> tuple.get(file.url)
			));
	}
}
