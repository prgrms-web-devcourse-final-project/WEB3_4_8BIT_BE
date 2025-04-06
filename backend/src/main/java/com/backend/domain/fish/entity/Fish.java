package com.backend.domain.fish.entity;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.backend.global.baseentity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Table(name = "fishes")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@SuperBuilder
@ToString
public class Fish extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long fishId;

	@Column(nullable = false, length = 30)
	private String name;

	// Length 기본 값인 255로 제한
	@Column(nullable = false, columnDefinition = "TEXT")
	private String description;

	@Column(nullable = false)
	private Long fileId;

	@JdbcTypeCode(SqlTypes.JSON)
	private List<Long> spawnSeasonList = new ArrayList<>();

	@Column(nullable = false, length = 50)
	private String spawnLocation;

	@Column(nullable = false)
	@Builder.Default
	private Long popularityScore = 0L; //인기 점수
}
