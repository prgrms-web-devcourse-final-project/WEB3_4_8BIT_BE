package com.backend.domain.fishencyclopedia.service;

import com.backend.domain.fishencyclopedia.dto.request.FishEncyclopediasRequest;
import com.backend.domain.member.entity.Members;

public interface FishEncyclopediasService {

	/**
	 * 물고기 도감 저장 메소드
	 *
	 * @param create {@link FishEncyclopediasRequest.Create}
	 * @param members                  {@link Members}
	 * @return {@link Long}
	 * @implSpec FishEncyclopediasRequest 받아서 저장 후 ID 값 반환
	 * @author Kim Dong O
	 */
	Long save(FishEncyclopediasRequest.Create create, Members members);
}
