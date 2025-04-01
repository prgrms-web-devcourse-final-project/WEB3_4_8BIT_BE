package com.backend.global.auth.oauth2.userinfo;

import java.util.Map;

public abstract class OAuth2UserInfo {
	protected Map<String, Object> attributes;

	public OAuth2UserInfo(Map<String, Object> attributes) {
		this.attributes = attributes;
	}

	public abstract String getId(); // Oauth 소셜 식별자 (ex : kakao, naver)

	public abstract String getName();

	public abstract String getEmail();

	public abstract String getImageUrl();

	public abstract String getPhoneNumber();
}
