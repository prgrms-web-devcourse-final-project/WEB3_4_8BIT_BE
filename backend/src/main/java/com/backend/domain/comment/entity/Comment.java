package com.backend.domain.comment.entity;

import com.backend.global.baseentity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Table(name = "comments", indexes = {
	@Index(name = "idx_comments_01", columnList = "comment_id, fishing_trip_post_id, created_at"),
	@Index(name = "idx_comments_02", columnList = "comment_id, fishing_trip_post_id, parent_id, created_at")
})
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@SuperBuilder
@ToString
public class Comment extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long commentId;

	private Long parentId;

	@Column(nullable = false)
	private Long memberId;

	@Column(nullable = false)
	private Long fishingTripPostId;

	@Column(nullable = false, length = 100, columnDefinition = "TEXT")
	private String content;

	@Column(nullable = false)
	@Builder.Default
	private Integer childCount = 0;
}
