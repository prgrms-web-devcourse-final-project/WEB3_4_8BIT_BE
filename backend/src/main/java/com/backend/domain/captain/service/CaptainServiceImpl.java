package com.backend.domain.captain.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.domain.captain.converter.CaptainConverter;
import com.backend.domain.captain.dto.Request.CaptainRequest;
import com.backend.domain.captain.dto.Response.CaptainResponse;
import com.backend.domain.captain.entity.Captain;
import com.backend.domain.captain.exception.CaptainErrorCode;
import com.backend.domain.captain.exception.CaptainException;
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
		// 멤버 조회
		Member member = getMemberById(memberId);

		// 추가정보 이미 작성했는지 검증
		validAddInfo(member);

		// 멤버 추가 정보 및 role 업데이트
		member.updateMember(requestDto.nickname(), requestDto.profileImg(), requestDto.description());
		member.updateRole(MemberRole.CAPTAIN);

		// 선장 정보 생성 및 저장
		Captain captain = CaptainConverter.fromMemberAndCaptainRequestCreate(memberId, requestDto);
		log.debug("[선장 등록 완료] : {}", captain);

		return captainRepository.save(captain).getMemberId();
	}

	@Override
	@Transactional
	public Long updateCaptainShipList(final Long captainId, final CaptainRequest.Update requestDto) {

		Captain captain = getCaptainById(captainId);

		captain.updateShipList(requestDto.shipList());
		log.debug("선장 배 리스트를 수정하였습니다. 업데이트된 배 리스트 : {}", captain.getShipList());

		return captain.getMemberId();
	}

	@Override
	@Transactional(readOnly = true)
	public CaptainResponse.Detail getCaptainDetail(final Long captainId) {

		// 선장 상세 조회
		CaptainResponse.Detail detail = getCaptainDetailById(captainId);

		// 역할이 선장인지 검증
		validCaptainRole(detail);

		return detail;
	}

	/**
	 * 회원 ID로 회원 엔티티를 조회하는 메소드
	 *
	 * @param memberId 조회할 회원의 ID
	 * @return {@link Member} 조회된 회원 엔티티
	 * @throws MemberException 회원이 존재하지 않는 경우 예외 발생
	 */

	private Member getMemberById(final Long memberId) {

		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
		log.debug("[멤버 조회] : {}", member);

		return member;
	}

	/**
	 * 선장 ID로 선장 엔티티를 조회하는 메소드
	 *
	 * @param captainId 조회할 선장의 ID
	 * @return {@link Captain} 조회된 선장 엔티티
	 * @throws CaptainException 선장이 존재하지 않는 경우 예외 발생
	 */

	private Captain getCaptainById(final Long captainId) {

		Captain captain = captainRepository.findById(captainId)
			.orElseThrow(() -> new CaptainException(CaptainErrorCode.CAPTAIN_NOT_FOUND));
		log.debug("[선장 조회] : {}", captain);

		return captain;
	}

	/**
	 * 선장 ID로 선장 상세 조회하는 메소드
	 *
	 * @param captainId 조회할 선장의 ID
	 * @return {@link CaptainResponse.Detail} 조회된 선장 상세 내역
	 * @throws CaptainException 선장이 존재하지 않는 경우 예외 발생
	 */

	private CaptainResponse.Detail getCaptainDetailById(final Long captainId) {

		CaptainResponse.Detail detail = captainRepository.findDetailById(captainId)
			.orElseThrow(() -> new CaptainException(CaptainErrorCode.CAPTAIN_NOT_FOUND));
		log.debug("[선장 상세 조회] : {}", detail);

		return detail;
	}

	/**
	 * 회원의 추가 정보 등록 여부를 검증하는 메소드
	 *
	 * @param member 검증할 회원 엔티티
	 * @throws MemberException 이미 추가 정보가 등록된 경우 예외 발생
	 */

	private static void validAddInfo(final Member member) {
		if (member.getIsAddInfo()) {
			throw new MemberException(MemberErrorCode.ALREADY_ADDED_INFO);
		}
	}

	/**
	 * 선장 상세 정보에서 역할이 선장인지 검증하는 메소드
	 *
	 * @param detail 검증할 선장 상세 정보
	 * @throws CaptainException 역할이 선장이 아닌 경우 예외 발생
	 */

	private static void validCaptainRole(final CaptainResponse.Detail detail) {
		if (detail.role() != MemberRole.CAPTAIN) {
			throw new CaptainException(CaptainErrorCode.NOT_CAPTAIN);
		}
	}
}
