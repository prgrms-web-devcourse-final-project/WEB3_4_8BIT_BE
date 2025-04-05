package com.backend.domain.catchmaxlength.entity;

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

@Table(name = "catch_max_lengths")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@SuperBuilder
@ToString
public class CatchMaxLength extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long catchMaxLengthId;

	@Column(nullable = false)
	private Long fishId;

	@Column(nullable = false)
	private Long memberId;

	@Column(nullable = false)
	@Builder.Default
	private Integer bestLength = 0;

	@Column(nullable = false)
	@Builder.Default
	private Integer catchCount = 0;

	public void setBestLength(final Integer bestLength) {
		this.bestLength = Math.max(this.bestLength, bestLength);
	}

	public void setCatchCount(final Integer catchCount) {
		this.catchCount +=  catchCount;
	}
}
