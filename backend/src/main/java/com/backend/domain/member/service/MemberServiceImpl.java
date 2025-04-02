package com.backend.domain.member.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.domain.member.dto.MemberRequest;
import com.backend.domain.member.dto.MemberResponse;
import com.backend.domain.member.entity.Member;
import com.backend.domain.member.exception.MemberErrorCode;
import com.backend.domain.member.exception.MemberException;
import com.backend.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

	private final MemberRepository memberRepository;

	@Override
	@Transactional
	public Long saveAddInfo(final Long memberId, final MemberRequest.Form requestDto) {

		Member member = getMemberById(memberId);

		validAddInfo(member);

		member.updateMember(requestDto.nickname(), requestDto.profileImg(), requestDto.description());
		log.debug("추가 정보저장에 성공하였습니다. 닉네임 :{} ", member.getNickname());

		return member.getMemberId();
	}

	@Override
	@Transactional(readOnly = true)
	public MemberResponse.Detail getMemberDetail(final Long memberId) {

		return getMemberDetailById(memberId);
	}

	@Override
	@Transactional
	public Long updateMember(Long memberId, final MemberRequest.Form requestDto) {

		Member member = getMemberById(memberId);

		member.updateMember(requestDto.nickname(), requestDto.profileImg(), requestDto.profileImg());
		log.debug("회원 정보를 수정하였습니다. 닉네임 :{}", member.getNickname());

		return member.getMemberId();
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
		log.debug("member를 찾았습니다. 이름 : {} , 이메일 : {}, id :{}", member.getName(), member.getEmail(),
			member.getMemberId());
		return member;
	}

	/**
	 * 회원의 추가 정보 등록 여부를 검증하는 메소드
	 *
	 * @param member 검증할 회원 엔티티
	 * @throws MemberException 이미 추가 정보가 등록된 경우 예외 발생
	 */

	private static void validAddInfo(Member member) {
		if (member.getIsAddInfo()) {
			throw new MemberException(MemberErrorCode.ALREADY_ADDED_INFO);
		}
	}

	/**
	 * 회원 ID를 기반으로 상세 정보를 조회하는 메소드
	 *
	 * @param memberId 조회할 회원의 ID
	 * @return {@link MemberResponse.Detail} 조회된 회원의 상세 응답 DTO
	 * @throws MemberException 해당 ID의 회원이 존재하지 않을 경우 예외 발생
	 * @implSpec QueryDSL을 통해 필요한 필드만 조회하여 DTO로 변환된 결과를 반환한다.
	 */

	private MemberResponse.Detail getMemberDetailById(Long memberId) {
		MemberResponse.Detail responseDto = memberRepository.findDetailById(memberId)
			.orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
		log.debug("responseDto를 만들었습니다. 이름 : {}, 이메일 : {}, 닉네임 : {}", responseDto.name(), responseDto.email(),
			responseDto.nickname());
		return responseDto;
	}
}
