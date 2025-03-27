package com.backend.domain.member.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.backend.domain.member.entity.Members;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MembersRepositoryImpl implements MembersRepository{
	private final MembersJpaRepository membersJpaRepository;
	private final MembersQueryRepository membersQueryRepository;

	@Override
	public Members save(Members members){
		return membersJpaRepository.save(members);
	}

	@Override
	public Optional<Members> findByPhone(String phone){
		return membersJpaRepository.findByPhone(phone);
	}
}
