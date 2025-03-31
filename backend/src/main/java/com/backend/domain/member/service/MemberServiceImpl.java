package com.backend.domain.member.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.domain.member.converter.MemberConverter;
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
	public Long saveAddInfo(final Long memberId, final MemberRequest.form requestDto) {

		Member member = getMember(memberId);

		if (member.getIsAddInfo()) {
			throw new MemberException(MemberErrorCode.ALREADY_ADDED_INFO);
		}

		member.createAddInfo(requestDto.nickname(), requestDto.profileImg(), requestDto.description());
		log.debug("추가 정보저장에 성공하였습니다. 닉네임 :{} ", member.getNickname());

		return member.getMemberId();
	}
  
	public MemberResponse.Detail getMemberDetail(final Long memberId) {

		Member member = getMember(memberId);

		MemberResponse.Detail responseDto = MemberConverter.fromMemberToDetail(member);
		log.debug("responseDto를 만들었습니다. 이름 : {}, 이메일 : {}, 닉네임 : {}", responseDto.name(), responseDto.email(),
			responseDto.nickname());

		return responseDto;
	}

	@Override
	public Long updateMember(Long memberId) {
		return 0L;
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
		log.debug("member를 찾았습니다. 이름 : {} , 이메일 : {}, id :{}", member.getName(), member.getEmail(),
			member.getMemberId());
		return member;
	}
}
