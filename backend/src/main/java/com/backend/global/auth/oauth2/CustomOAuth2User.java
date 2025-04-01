package com.backend.global.auth.oauth2;

import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import lombok.Getter;

@Getter
public class CustomOAuth2User extends DefaultOAuth2User {

	private final Long id; // 사용자 식별용 id (DB 사용자 id)

	private final String email; // 사용자 이메일

	public CustomOAuth2User(
		Collection<? extends GrantedAuthority> authorities,
		Map<String, Object> attributes,
		String nameAttributeKey,
		Long id,
		String email) {
		super(authorities, attributes, nameAttributeKey);
		this.id = id;
		this.email = email;
	}
}
