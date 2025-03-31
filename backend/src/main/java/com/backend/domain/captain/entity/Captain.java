package com.backend.domain.captain.entity;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.backend.global.baseentity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Table(name = "captains")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@SuperBuilder
@ToString
@EqualsAndHashCode(callSuper = false)
public class Captain extends BaseEntity {

	@Id
	private Long memberId;

	@Column(unique = true, nullable = false, length = 15)
	private String shipLicenseNumber;

	@JdbcTypeCode(SqlTypes.JSON)
	private List<Long> shipList;

	public void updateShipInfo(final List<Long> shipList) {
		this.shipList = shipList != null ? shipList : new ArrayList<>();
	}
}
