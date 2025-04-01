package com.backend.domain.captain.repository;

import static com.backend.domain.captain.entity.QCaptain.*;
import static com.backend.domain.member.entity.QMember.*;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.backend.domain.captain.dto.Response.CaptainResponse;
import com.backend.domain.captain.dto.Response.QCaptainResponse_Detail;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CaptainQueryRepository {

	private final JPAQueryFactory jpaQueryFactory;

	public Optional<CaptainResponse.Detail> findDetailById(final Long captainId) {

		CaptainResponse.Detail detail = jpaQueryFactory
			.select(new QCaptainResponse_Detail(
				member.memberId,
				member.email,
				member.name,
				member.nickname,
				member.phone,
				member.profileImg,
				member.description,
				member.role,
				captain.shipLicenseNumber,
				captain.shipList
			))
			.from(member)
			.leftJoin(captain)
			.on(member.memberId.eq(captain.memberId))
			.where(member.memberId.eq(captainId))
			.fetchOne();

		return Optional.ofNullable(detail);
	}
}
