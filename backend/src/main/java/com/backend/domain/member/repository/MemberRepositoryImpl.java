package com.backend.domain.member.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

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
	public Member save(final Member member) {
		return memberJpaRepository.save(member);
	}

	@Override
	public Optional<Member> findByPhone(final String phone) {
		return memberJpaRepository.findByPhone(phone);
	}

	@Override
	public Optional<Member> findById(final Long id) {
		return memberJpaRepository.findById(id);
	}

	@Override
	public Optional<MemberResponse.Detail> findDetailById(final Long memberId) {
		return memberQueryRepository.findDetailById(memberId);
	}

	@Override
	public boolean existsById(final Long memberId) {
		return memberJpaRepository.existsById(memberId);
	}

	@Override
	public List<String> findEmailListByIdList(final List<Long> memberIdList) {
		return memberQueryRepository.findEmailListByIdList(memberIdList);
	}

	@Override
	public Map<Long, String> getFileUrlMapByIdList(final Set<Long> memberIdList) {
		return memberQueryRepository.getFileUrlMapByIdList(memberIdList);
	}
}
