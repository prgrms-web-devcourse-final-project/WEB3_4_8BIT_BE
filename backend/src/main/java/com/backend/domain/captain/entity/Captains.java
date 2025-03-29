package com.backend.domain.captain.entity;

import com.backend.domain.member.entity.Member;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
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
@DiscriminatorValue("captain")
public class Captains extends Member {

	@Column(unique = true, nullable = false, length = 30) //추후 찾아보고 length 맞춰서 바꿔야함
	private String shipLicenseNumber;

	private Long shipId;
}
