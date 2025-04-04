package com.backend.domain.member.entity;

import com.backend.domain.member.domain.MemberRole;
import com.backend.domain.member.domain.Provider;
import com.backend.global.baseentity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Table(name = "members")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@SuperBuilder
@ToString
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

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Provider provider;

	@Column(nullable = false, unique = true)
	private String providerId;

	private String profileImg;

	@Column(columnDefinition = "TEXT", length = 500)
	private String description;

	@Column(nullable = false)
	private Boolean isAddInfo;

	public void updateUserProfile(final String name, final String email) {
		this.name = name;
		this.email = email;
	}

	public void updateMember(final String nickname, final String profileImg, final String description) {
		this.nickname = nickname;
		this.profileImg = profileImg;
		this.description = description;
		if (!this.isAddInfo)
			this.isAddInfo = true;
	}

	public void updateRole(final MemberRole role) {
		this.role = role;
	}
}