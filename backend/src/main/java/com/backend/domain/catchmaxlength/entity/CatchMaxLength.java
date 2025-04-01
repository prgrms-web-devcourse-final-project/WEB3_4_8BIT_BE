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
import lombok.EqualsAndHashCode;
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
@EqualsAndHashCode(callSuper = false)
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

	//TODO 추후 테스트 코드 작성해야함
	public void setBestLength(final Integer bestLength) {
		this.bestLength = this.bestLength > bestLength ? this.bestLength : bestLength;
	}
}
