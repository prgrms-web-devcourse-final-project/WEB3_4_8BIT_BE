package com.backend.global.baseentity;


import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.time.ZonedDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * BaseEntity
 * <p>엔티티 생성, 수정 일자를 관리하는 BaseEntity 입니다.</p>
 * @author Kim Dong O
 */
@Getter
@MappedSuperclass
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class BaseEntity {

	/**
	 * 생성일시
	 */
	@Column(name = "created_at")
	private ZonedDateTime createdAt;

	/**
	 * 수정일시
	 */
	@Column(name = "modified_at")
	private ZonedDateTime modifiedAt;

	@PrePersist
	public void prePersist() {
		this.createdAt = ZonedDateTime.now();
		this.modifiedAt = ZonedDateTime.now();
	}

	@PreUpdate
	public void preUpdate() {
		this.modifiedAt = ZonedDateTime.now();
	}
}