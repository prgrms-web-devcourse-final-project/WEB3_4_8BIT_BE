package com.backend.global.auth.oauth2.userinfo;

import java.util.Map;

public class NaverOauth2UserInfo extends OAuth2UserInfo {

	public NaverOauth2UserInfo(Map<String, Object> attributes) {
		super(attributes);
	}

	@Override
	public String getId() {
		Map<String, Object> response = (Map<String, Object>) attributes.get("response");
		return String.valueOf(response.get("id"));
	}

	@Override
	public String getEmail() {
		Map<String, Object> response = (Map<String, Object>) attributes.get("response");
		return (String) response.get("email");
	}

	@Override
	public String getName() {
		Map<String, Object> response = (Map<String, Object>) attributes.get("response");
		return (String) response.get("name");
	}

	@Override
	public String getImageUrl() {
		Map<String, Object> response = (Map<String, Object>) attributes.get("response");
		return (String) response.get("profile_image");
	}

	@Override
	public String getPhoneNumber() {
		Map<String, Object> response = (Map<String, Object>) attributes.get("response");
		return (String) response.get("mobile");
	}
}
