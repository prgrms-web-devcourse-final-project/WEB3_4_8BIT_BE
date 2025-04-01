package com.backend.domain.captain.service;

import com.backend.domain.captain.dto.Request.CaptainRequest;
import com.backend.domain.captain.dto.Response.CaptainResponse;

public interface CaptainService {

	/**
	 * 선장 저장 메소드
	 *
	 * @param memberId 회원가입후 선장을 선택한 멤버
	 * @param requestDto {@link CaptainRequest.Create}
	 * @return {@link Long memberId} Long: 선장 memberId
	 * @implSpec 회원가입한 멤버와 선장 추가 정보를 파라미터로 받고 저장한다.
	 * @author Janghyeonsuk
	 */
	Long createCaptain(final Long memberId, final CaptainRequest.Create requestDto);


	/**
	 * 선장 상세 조회 메소드
	 *
	 * @param captainId 선장 Id
	 * @return {@link CaptainResponse.Detail} 선장 상세정보 응답 데이터
	 * @implSpec 회원가입한 멤버와 선장 추가 정보를 파라미터로 받고 저장한다.
	 * @author Janghyeonsuk
	 */
	CaptainResponse.Detail getCaptainDetail(final Long captainId);

	/**
	 * 선장 배 리스트 수정 메소드
	 *
	 * @param captainId 선장 Id
	 * @param requestDto 배 리스트 포함된 dto
	 * @return {@link Long} 업데이트 된 선장 Id
	 * @implSpec 새로운 배 리스트를 받고 선장 정보 업데이트
	 * @author Janghyeonsuk
	 */
	Long updateCaptainShipList(final Long captainId, final CaptainRequest.Update requestDto);
}
