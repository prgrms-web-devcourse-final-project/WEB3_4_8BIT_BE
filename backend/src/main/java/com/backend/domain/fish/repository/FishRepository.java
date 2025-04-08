package com.backend.domain.fish.repository;

import java.util.List;
import java.util.Optional;

import com.backend.domain.fish.dto.FishResponse;
import com.backend.domain.fish.entity.Fish;
import com.querydsl.core.Tuple;

public interface FishRepository {
	/**
	 * 물고기 존재 여부 조회 메소드
	 *
	 * @param fishId {@link Long}
	 * @return {@link Boolean} 데이터가 있다면 true, 없으면 false
	 * @implSpec fishId로 데이터가 있는지 확인 후 결과 반한
	 * @author Kim Dong O
	 */
	boolean existsById(final Long fishId);

	/**
	 * 물고기 저장 메소드
	 *
	 * @param fish {@link Fish}
	 * @return {@link Fish}
	 * @implSpec FishPoint 받아서 저장 후 저장된 엔티티 반환
	 * @author Kim Dong O
	 */
	Fish save(final Fish fish);

	/**
	 * 물고기 조회 메소드
	 *
	 * @param fishId {@link Long}
	 * @return {@link Optional<FishResponse.Detail>}
	 * @implSpec fishId 받아서 조회 후 Optional로 감싸서 반환
	 * @author Kim Dong O
	 */
	Optional<FishResponse.Detail> findDetailById(final Long fishId);

	/**
	 * Id 리스트와 일치하는 물고기 정보 조회 메소드
	 *
	 * @param fishIdList {@link List}
	 * @return {@link List<Fish>}
	 * @implSpec fishId 리스트를 받아서 일치하는 엔티티 반환
	 * @author swjoon
	 */
	List<Fish> findAllById(final List<Long> fishIdList);

	/**
	 * 인기순으로 limit 개수만큼 물고기 조회하는 메소드
	 *
	 * @param size {@link Integer} 최대 값 10
	 * @return {@link List<FishResponse.Popular>}
	 * @implSpec 인기순으로 물고기 limit 개수만큼 조회 후 반환
	 * @author Kim Dong O
	 */
	List<FishResponse.Popular> findPopular(final Integer size);
  /**
	 * 현재 시간부터 1시간 전까지 물고기 도감에 추가된 잡은 횟수를 인기도로 설정하는 메소드
	 *
	 * @param hourlyFishCountSummaryList 물고기 count 집계 리스트
	 * @implSpec 현재 시간부터 1시간 전까지 물고기 도감에 추가된 잡은 횟수를 인기도로 설정
	 * @author Kim Dong O
	 */
	void updateFishPopularityScores(List<Tuple> hourlyFishCountSummaryList);

	/**
	 * 물고기 전체 조회 메소드
	 * <p>물고기 데이터 양이 많지 않고 관리자가 직접 추가하기 때문에 findAll로 구현하였습니다.</p>
	 *
	 * @return {@link List<FishResponse.FishAll>}
	 * @implSpec 물고기 전체 조회 후 결과 값 반환
	 * @author Kim Dong O
	 */
	List<FishResponse.FishAll> findFishAll();
}
