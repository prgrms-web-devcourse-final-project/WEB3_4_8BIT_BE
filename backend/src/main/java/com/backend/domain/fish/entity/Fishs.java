package com.backend.domain.fish.entity;

import com.backend.domain.fish.domain.SpawnLocation;
import com.backend.global.baseentity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
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
public class Fishs extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long fishId;

	@Column(nullable = false, length = 30)
	private String name;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String description;

	@Column(nullable = false)
	private String icon;

	//TODO 추후 방법 고민 후 다시 추가 예정
	/*@Column(nullable = false)
	private List<Long> spawnSeason = new ArrayList<>();*/


	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 10)
	private SpawnLocation spawnLocation;
}
