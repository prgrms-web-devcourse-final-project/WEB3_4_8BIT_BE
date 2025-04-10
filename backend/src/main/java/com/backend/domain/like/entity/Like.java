package com.backend.domain.like.entity;

import com.backend.domain.like.domain.LikeTargetType;
import com.backend.global.baseentity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Table(
	name = "likes",
	uniqueConstraints = @UniqueConstraint(columnNames = {"memberId", "targetType", "targetId"}),
	indexes = {
		@Index(name = "idx_target_type_id", columnList = "targetType, targetId"),
		@Index(name = "idx_member_id", columnList = "memberId"),
		@Index(name = "idx_member_type_target_deleted", columnList = "memberId, targetType, targetId, isDeleted")
	}
)

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@SuperBuilder
@ToString
public class Like extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long likeId;

	@Column(nullable = false)
	private Long memberId;

	@Column(nullable = false)
	private LikeTargetType targetType;

	@Column(nullable = false)
	private Long targetId;

	@Column(nullable = false)
	private Boolean isDeleted = false;

	public boolean isActive() {
		return Boolean.FALSE.equals(this.isDeleted);
	}

}
