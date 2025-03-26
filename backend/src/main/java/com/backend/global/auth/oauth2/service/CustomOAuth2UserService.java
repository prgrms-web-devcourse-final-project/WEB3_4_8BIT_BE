package com.backend.global.auth.oauth2.service;

import java.util.Collections;
import java.util.Optional;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.domain.member.domain.Provider;
import com.backend.domain.member.entity.Members;
import com.backend.domain.member.repository.MembersRepository;
import com.backend.global.auth.oauth2.CustomOAuth2User;
import com.backend.global.auth.oauth2.userinfo.KakaoOAuth2UserInfo;
import com.backend.global.auth.oauth2.userinfo.NaverOauth2UserInfo;
import com.backend.global.auth.oauth2.userinfo.OAuth2UserInfo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

	private final MembersRepository membersRepository;

	@Override
	@Transactional
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
		OAuth2User oAuth2User = delegate.loadUser(userRequest);

		// 현재 진행중인 서비스를 구분하기 위해 문자열로 받음
		String registrationId = userRequest.getClientRegistration().getRegistrationId();
		log.debug("Provider: {}", registrationId);
		log.debug("OAuth2User attributes: {}", oAuth2User.getAttributes());

		// OAuth2 로그인 시 키가 되는 필드값 (PK)
		String usernameAttributeName = userRequest.getClientRegistration()
			.getProviderDetails()
			.getUserInfoEndpoint()
			.getUserNameAttributeName();
		log.debug("Username attribute name: {}", usernameAttributeName);

		// OAuth2UserInfo 객체 생성
		OAuth2UserInfo userInfo = switch (registrationId.toLowerCase()) {
			case "naver" -> new NaverOauth2UserInfo(oAuth2User.getAttributes());
			case "kakao" -> new KakaoOAuth2UserInfo(oAuth2User.getAttributes());
			default -> throw new OAuth2AuthenticationException("지원하지 않는 소셜 로그인입니다: " + registrationId);
		};

		log.debug("Extracted user info - id:{}, email: {}, name: {}",
			userInfo.getId(), userInfo.getEmail(), userInfo.getName());

		// 유저 정보 저장 또는 업데이트
		Members member = saveOrUpdate(userInfo, registrationId);
		log.debug("Saved/Updated member - id: {}, email: {}", member.getMemberId(), member.getEmail());

		return new CustomOAuth2User(
			Collections.singleton(new SimpleGrantedAuthority(member.getRole().name())),
			oAuth2User.getAttributes(),
			usernameAttributeName,
			member.getMemberId(),
			member.getEmail()
		);
	}

	private Members saveOrUpdate(OAuth2UserInfo userInfo, String registrationId) {
		Provider provider = getProvider(registrationId);
		Optional<Members> optionalMember = membersRepository
			.findByProviderAndProviderId(provider, userInfo.getId());

		if (optionalMember.isPresent()) {
			//TODO 기존 회원 처리
			Members member = optionalMember.get();
			// member.updateOAuth2Profile(
			// 	userInfo.getName(),
			// 	userInfo.getImageUrl()
			// );
			return member;
		} else {
			// 신규 회원 가입
			Members member = Members.builder()
				.email(userInfo.getEmail())
				.name(userInfo.getName())
				.providerId(userInfo.getId())
				.provider(provider)
				.profileImg(userInfo.getImageUrl())
				.build();
			return membersRepository.save(member);
		}
	}

	/**
	 * registrationId를 Provider Enum 처리
	 * @param registrationId OAuth2 서비스 구분 ID (ex: kakao, google)
	 * @throws OAuth2AuthenticationException 지원하지 않는 OAuth2 제공자
	 */
	private Provider getProvider(String registrationId) {
		try {
			return Provider.valueOf(registrationId.toUpperCase());
		} catch (IllegalArgumentException e) {
			log.debug("Unsupported OAuth2 provider: {}", registrationId);
			throw new OAuth2AuthenticationException("지원하지 않는 소셜 로그인입니다: " + registrationId);
		}
	}
}
