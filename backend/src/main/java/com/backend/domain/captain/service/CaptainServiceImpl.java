package com.backend.domain.captain.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.domain.captain.converter.CaptainConverter;
import com.backend.domain.captain.dto.Request.CaptainRequest;
import com.backend.domain.captain.entity.Captain;
import com.backend.domain.captain.repository.CaptainRepository;
import com.backend.domain.member.domain.MemberRole;
import com.backend.domain.member.entity.Member;
import com.backend.domain.member.exception.MemberErrorCode;
import com.backend.domain.member.exception.MemberException;
import com.backend.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CaptainServiceImpl implements CaptainService {

	private final CaptainRepository captainRepository;
	private final MemberRepository memberRepository;

	@Override
	@Transactional
	public Long createCaptain(final Long memberId, final CaptainRequest.Create requestDto) {
		// 1. 멤버 조회
		Member member = getMember(memberId);

		// 2. 멤버 추가 정보 및 role 업데이트
		member.createAddInfo(requestDto.nickname(), requestDto.profileImg(), requestDto.descrption());
		member.updateRole(MemberRole.CAPTAIN);

		// 3. 선장 정보 생성 및 저장
		Captain captain = CaptainConverter.fromMemberAndCaptainRequestCreate(requestDto, member);

		log.debug("[선장 등록 완료] memberId={}, license={}, shipList={}",
			member.getMemberId(), captain.getShipLicenseNumber(), captain.getShipList());

		return captainRepository.save(captain).getMemberId();
	}

	/**
	 * 회원 ID로 회원 엔티티를 조회하는 메소드
	 *
	 * @param memberId 조회할 회원의 ID
	 * @return {@link Member} 조회된 회원 엔티티
	 * @throws MemberException 회원이 존재하지 않는 경우 예외 발생
	 */

	private Member getMember(final Long memberId) {

		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

		log.debug("[멤버 조회] memberId={} , email={}, name={}", member.getMemberId(), member.getEmail(),
			member.getName());
		return member;
	}
}
