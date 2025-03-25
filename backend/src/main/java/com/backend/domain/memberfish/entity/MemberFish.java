package com.backend.domain.memberfish.entity;

import org.hibernate.annotations.ColumnDefault;

import com.backend.global.baseentity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
public class MemberFish extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long memberFishId;

	@Column(nullable = false)
	@ColumnDefault("0")
	private Integer count;

	@Column(nullable = false)
	private Integer length;

	@Column(nullable = false)
	private Long fishPointId;

	@Column(nullable = false)
	private Long memberId;
}
