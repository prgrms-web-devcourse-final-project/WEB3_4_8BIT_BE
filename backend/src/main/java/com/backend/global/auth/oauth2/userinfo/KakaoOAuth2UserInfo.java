package com.backend.global.auth.oauth2.userinfo;

import java.util.Map;


public class KakaoOAuth2UserInfo extends OAuth2UserInfo {

	public KakaoOAuth2UserInfo(Map<String, Object> attributes) {
		super(attributes);
	}

	@Override
	public String getId() {
		return String.valueOf(attributes.get("id"));
	}

	@Override
	public String getEmail() {
		Map<String, Object> account = (Map<String, Object>)attributes.get("kakao_account");
		return (String)account.get("email");
	}

	@Override
	public String getName() {
		Map<String, Object> account = (Map<String, Object>)attributes.get("kakao_account");
		Map<String, Object> profile = (Map<String, Object>)account.get("profile");
		return (String)profile.get("nickname");
	}

	@Override
	public String getImageUrl() {
		Map<String, Object> account = (Map<String, Object>)attributes.get("kakao_account");
		Map<String, Object> profile = (Map<String, Object>)account.get("profile");
		return (String)profile.get("profile_image_url");
	}

	@Override
	public String getPhoneNumber() {
		Map<String, Object> account = (Map<String, Object>)attributes.get("kakao_account");
		return (String)account.get("phone_number");
	}

}

