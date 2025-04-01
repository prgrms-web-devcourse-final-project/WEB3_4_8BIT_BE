package com.backend.domain.shipfishingpostfish.entity;

import com.backend.global.baseentity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Table(name = "ship_fish_post_fishes")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@SuperBuilder
@ToString
@IdClass(ShipFishingPostFishId.class)
@EqualsAndHashCode(callSuper = false)
public class ShipFishingPostFish extends BaseEntity {

	@Id
	@Column(insertable = false, updatable = false)
	private Long shipFishingPostId;

	@Id
	@Column(insertable = false, updatable = false)
	private Long fishId;
}
