package com.backend.domain.fishencyclopedia.entity;

import com.backend.global.baseentity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Table(
	name = "fish_encyclopedias",
	indexes = {
		@Index(name = "idx_fish_encyclopedias_01", columnList = ("fish_id, member_id, created_at, fish_encyclopedia_id")),
		@Index(name = "idx_fish_encyclopedias_02", columnList = ("fish_id, member_id, count, fish_encyclopedia_id")),
		@Index(name = "idx_fish_encyclopedias_03", columnList = ("fish_id, member_id, length, fish_encyclopedia_id"))
	}
)
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@SuperBuilder
@ToString
public class FishEncyclopedia extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long fishEncyclopediaId;

	@Column(nullable = false)
	private Long fishId;

	@Column(nullable = false)
	private Integer length;

	@Column(nullable = false)
	private Integer count;

	@Column(nullable = false)
	private Long fishPointId;

	@Column(nullable = false)
	private Long memberId;
}