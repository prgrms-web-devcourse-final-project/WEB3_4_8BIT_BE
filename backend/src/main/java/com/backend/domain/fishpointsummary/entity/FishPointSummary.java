package com.backend.domain.fishpointsummary.entity;

import com.backend.global.baseentity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Table(
	name = "fish_point_summaries",
	uniqueConstraints = @UniqueConstraint(columnNames = {"fish_point_id", "fish_id"})
)
@Entity
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FishPointSummary extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long fishPointSummaryId;

	@Column(nullable = false)
	private Long fishPointId;

	@Column(nullable = false)
	private Long fishId;

	@Column(nullable = false)
	private Long fileId;

	@Column(nullable = false)
	private Integer totalCount;

	/**
	 * 낚시 포인트에서 잡힌 물고기 수를 누적하여 총합 증가 메서드
	 *
	 * @param count 추가할 물고기 수
	 */
	public void increaseTotalCount(int count) {
		this.totalCount += count;
	}
}
