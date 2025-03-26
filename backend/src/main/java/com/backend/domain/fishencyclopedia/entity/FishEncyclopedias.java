package com.backend.domain.fishencyclopedia.entity;

import org.hibernate.annotations.ColumnDefault;

import com.backend.global.baseentity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@SuperBuilder
@ToString
@EqualsAndHashCode(callSuper = false)
public class FishEncyclopedias extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long fishEncyclopediaId;

	@Column(nullable = false)
	@ColumnDefault("0")
	@Builder.Default
	private Integer count = 0;

	@Column(nullable = false)
	private Integer length;

	@Column(nullable = false)
	private Long fishPointId;

	@Column(nullable = false)
	private Long memberId;

	@Column(nullable = false)
	private Long fishId;
}
