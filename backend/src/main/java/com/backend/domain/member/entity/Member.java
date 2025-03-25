package com.backend.domain.member.entity;

import com.backend.domain.member.domain.MemberRole;
import com.backend.global.baseentity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
public class Member extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long memberId;

	@Column(unique = true, nullable = false, length = 50)
	private String email;

	@Column(nullable = false, length = 10)
	private String name;

	@Column(unique = true, length = 30)
	private String nickname;

	@Column(unique = true, nullable = false, length = 13)
	private String phone;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 10)
	private MemberRole role;

	private String profileImg;

	@Column(nullable = false)
	private Boolean isAddInfo;
}
