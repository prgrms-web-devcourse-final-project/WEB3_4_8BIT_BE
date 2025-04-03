package com.backend.domain.member.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.backend.domain.member.dto.MemberResponse;
import com.backend.domain.member.entity.Member;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository {
	private final MemberJpaRepository memberJpaRepository;
	private final MemberQueryRepository memberQueryRepository;

	@Override
	public Member save(Member member) {
		return memberJpaRepository.save(member);
	}

	@Override
	public Optional<Member> findByPhone(String phone) {
		return memberJpaRepository.findByPhone(phone);
	}

	@Override
	public Optional<Member> findById(Long id) {
		return memberJpaRepository.findById(id);
	}

	@Override
	public Optional<MemberResponse.Detail> findDetailById(Long memberId) {
		return memberQueryRepository.findDetailById(memberId);
	}

	@Override
	public boolean existsById(Long memberId) {
		return memberJpaRepository.existsById(memberId);
	}
}
