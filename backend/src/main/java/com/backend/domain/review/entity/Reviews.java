package com.backend.domain.review.entity;

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
import jakarta.persistence.UniqueConstraint;
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
@Table(	// 복합 unique 키 설정, 게시글당 1개의 리뷰만 등록 가능
	name = "reviews",
	uniqueConstraints = {
		@UniqueConstraint(columnNames = {"member_id", "ship_fish_post_id"})
	}
)
public class Reviews extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long reviewId;

	@Column(nullable = false)
	private Integer rating;

	@Column(nullable = false, columnDefinition = "TEXT", length = 200)
	private String content;

	@JdbcTypeCode(SqlTypes.JSON)
	private List<String> images;

	@Column(nullable = false)
	private Long memberId;

	@Column(nullable = false)
	private Long shipFishPostId;
}
